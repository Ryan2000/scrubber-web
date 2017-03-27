package com.csv.scrubber.service;
import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import com.csv.scrubber.async.ScrubberMatching;
import com.csv.scrubber.entity.ExtendedCustomerInfo;
import com.csv.scrubber.entity.FordDataBaseMarketing;
import com.csv.scrubber.entity.FordDirectMailMarketing;
import com.csv.scrubber.entity.FordGalpinData;
import com.csv.scrubber.entity.FordUnsold;
import com.csv.scrubber.entity.ScrubberField;
import com.csv.scrubber.model.DownloadFileModel;
import com.csv.scrubber.model.FieldMatchingModel;
import com.csv.scrubber.model.FileUploadModel;
import com.csv.scrubber.repository.CSVExportDao;
import com.csv.scrubber.repository.InputRepository;
import com.google.common.collect.ImmutableList;
import com.opencsv.bean.CsvBindByPosition;

import static java.util.stream.Collectors.toMap;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;

import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;

@Service
@Scope(WebApplicationContext.SCOPE_SESSION) // This bean will live for the
// duration of the web session
// before it gets garbage collected
//created when user logs in, and destroyed when session is over (*holds data particular to each user)
//if we want relevant data to be held from each user and live on, must be stored in database
@CommonsLog
public class ScrubberServiceImpl implements ScrubberService {
	
    
    @Autowired
    private Validator validator;
    
    @Autowired
    private InputRepository<FordGalpinData> fordGalpinDataInputRepository;
    
    @Autowired 
    private InputRepository<FordUnsold> fordUnsoldInputRepository;
    
    @Autowired
    private CSVExportDao csvExportDao;
    
    private Optional<FileUploadModel> fileUploadModelOptional;
    private Optional<Collection<FordGalpinData>> fordGalpinDataRecords;
    private Optional<Collection<FordUnsold>> fordUnsoldRecords;
    private Collection<FieldMatchingModel> fieldMatchingModels;
    private AtomicInteger progress = new AtomicInteger();
    
    @PostConstruct
    void init(){
    	fileUploadModelOptional = Optional.empty();
        fordUnsoldRecords = Optional.empty();
        fordGalpinDataRecords = Optional.empty();
        fieldMatchingModels = new ArrayList<>();

         
    }

    /*
     * using Optional for the following reasons:
     * making access to contents of our 2 files; FordUnsold and FordGalpinData, part of public interface of this class
     * we want to indicate that these collections can be empty, which is why we use optional
     * Anytime a value can be empty in an interface, use optional to indicate that to users of the interface
     */
    
	@Override
	public Optional<FileUploadModel> getFileUploadModel() {
		return this.fileUploadModelOptional;
	}
	
	@Override
	public Optional<Collection<FordGalpinData>> getGalpinRecords(){
		return this.fordGalpinDataRecords;
	}
	
	@Override
	public Optional<Collection<FordUnsold>> getFordUnsoldRecords(){
		return this.fordUnsoldRecords;
	}

	@Override
	@SneakyThrows
	public void readFileUploadModel(){
		//little Google Guava Magic (checkArgument)
		//takes a boolean (true/false) does nothing if true, if false throws an illegal exception
		//allows us to make sure everything is valid in our method - will generate an error of which we can debug.
	    checkArgument(fileUploadModelOptional.isPresent());
	    
	    //Little bit of Java 8 magic
	    //want to make sure they have added a fileUploadModel before we try and read our files
	    //our precondition is that the fileUploadModelOptional is present
	    //so now we can use the ifPresent method on optional to process the files
	    //this method takes a reference to a method, thus - this::readInputStream
	    fileUploadModelOptional.ifPresent(this::readInputStream);
	   
	}
	
	@SneakyThrows //allows code to compile and run - pushing any errors or exceptions to runtime 
				// wrapping a checked exception to a runtime exception
				//adds code behind the scenes allowing program to run
	private void readInputStream(FileUploadModel fileUploadModel){
		//Read the GalpinFile
	    //fordGalpinDataRecords = Optional.of(fordGalpinDataInputRepository.read(fileUploadModel.getGalpin().getInputStream(), FordGalpinData.class));
	    
	    //Read the UnsoldFile
        //fordUnsoldRecords = Optional.of(fordUnsoldInputRepository.read(fileUploadModel.getUnsold().getInputStream(), FordUnsold.class));
        /*
         * We pass our file's input stream to our repository classes 
         * (formerly called DAO classes) and it does the work of reading the files into memory
         * Since we are storing these in an Optional, 
         * we are using the Optional.of method to wrap the collection into an optional
         */
		
		fordGalpinDataRecords = Optional.of(fordGalpinDataInputRepository.read(
                new BufferedInputStream(new ByteArrayInputStream(fileUploadModel.getGalpinTempFile().get())), FordGalpinData.class));
        fordUnsoldRecords = Optional.of(
                fordUnsoldInputRepository.read(new BufferedInputStream(new ByteArrayInputStream(fileUploadModel.getUnsoldTempFile().get())), FordUnsold.class));
        
	}
	
	@Override
	public void setFileUploadModel(FileUploadModel fileUploadModel) {
		Set<ConstraintViolation<FileUploadModel>> violations = validator.validate(fileUploadModel);
		//validator has a validate method - pass object to that method and scans for JSR-303 annotations
		if(CollectionUtils.isNotEmpty(violations)){
			throw new ConstraintViolationException(violations);
			//pass violations to exception
		}
		this.fileUploadModelOptional = Optional.of(fileUploadModel);
		/*concept of why we validate this fileUploadModel
		 * service layer and controller layer are not interconnected. Completely diff areas of the application
		 * Spring ties these layers together, however since Service is unable to confirm that it is receiving a valid
		 * FileUploadModel, we need to run the validation, otherwise class will not work as intended
		 */
	}

	@Override
    public Map<String, Field> readScrubberFields(Class<?> clazz){
        Map<String, Field> results = Arrays.stream(clazz.getDeclaredFields())
                .filter(p -> p.isAnnotationPresent(ScrubberField.class))
                .collect(toMap(f -> f.getAnnotation(ScrubberField.class).displayName(), p -> p));
        
        /*
         * display Name is what the user sees *see fields in FordUnsold. @CsvBindByPosition then maps to the position
         */
        
        /*
        OLD CODE:
         Map<String, Field> results = 
                 //First, clazz.getDeclaredFields() returns an array of Fields
                 //so we use Arrays.stream to create a stream out of the array
                 Arrays.stream(clazz.getDeclaredFields())
                     //Next, we need to filter the Fields based on if the @ScruuberField annotation
                     //is present or not.
                     .filter(p -> p.isAnnotationPresent(ScrubberField.class))
                     //All of the fields with @ScrubberField are collected
                     .collect(
                         //And we want to take that collection and turn it into a map
                         toMap(
                             //The first lambda is the key. In our case, it's a String
                             //and we are using displayName()
                             f -> f.getAnnotation(ScurbberField.class).displayName(), 
                                 //The second lamda is the value. In this case we are
                                 //just going to use the Field object which is why
                                 //we have p -> p
                                 p -> p));
         */
        results.values().stream().forEach(p -> p.setAccessible(true));
        //we have set the values to accessible so that we can access private fields from another class
        //all of our fields in GalpinFordData and FordUnsold are private. *this is our workaround. 
        
        //*Streams allows code to perform faster
        //Java can delegate the work to different cores in your computer's processor
        //so if your computer has 4 cores, then all 4 cores may work on the stream at the same time 
        //if Java decides that's the most efficient way to do the job
        //*stream method is attached to collection interface
        //so things like ArrayList, HashSet, TreeSet, PriorityQueue, etc
        //similar to Fork-join
        return results;
    }

	@Override
	//using validator class to perform validation
	//this class tells us about the errors
	//in this case not so concerned about what the errors are, but if they exist
	//So we CollectionUtils to see if this is an empty collection
	//once complete add the field fieldMatchingModels to the class
	//the initialize in the @PostConstructMethod
	public void addFieldMatchingModel(FieldMatchingModel fieldMatchingModel) {
		Set<ConstraintViolation<FieldMatchingModel>> violations = validator.validate(fieldMatchingModel);
		if(CollectionUtils.isNotEmpty(violations)){
            throw new ConstraintViolationException(violations);
        }
        fieldMatchingModels.add(fieldMatchingModel);
        log.info("Size of fieldMatchingModels is " + fieldMatchingModels.size());
	}

	@Async //This annotation is what tells Spring to run this method in it's own thread
	@Override
	public Future<DownloadFileModel> scrub() {
		//First: Read the FordGalpinData and the FordUnsold files
		readFileUploadModel();
		Collection<BiFunction<FordGalpinData, FordUnsold, Boolean>> matchingStrategy = generateMatchingStrategy();
		DownloadFileModel dfm = new DownloadFileModel();
		
		//same class as System.out.println
		//In this case, we are making a PrintWriter object and it needs and output stream
		//so every time we call println on this PrintWriter, it will write the output to this ByteArrayOutputStream
		PrintWriter printWriter = new PrintWriter(dfm.getScrubberLog(), true);
		
		//Next we have a Consumer that let's us write to the scrubber log file
		// if you remember from the JavaFX application, we wrote to a window every time we found a match
		// in this case, we are going to write it to a file and let the user download it when they are done
		Consumer<String> messageCallback = (msg) -> printWriter.println(msg);
		
		log.info("performReduction started");
        Collection<ExtendedCustomerInfo> scrubbed = performReduction(fordGalpinDataRecords.get(), fordUnsoldRecords.get(), matchingStrategy, messageCallback);
        log.info("performReduction done");
        
        writeFiles(scrubbed, dfm);
		
        //This line will return a Future object to the caller.
      	//That allows this method to keep running while the program does
      	//other work
		return new AsyncResult<>(dfm);
	}
	
	
	@SneakyThrows
    private void writeFiles(Collection<ExtendedCustomerInfo> scrubbed, DownloadFileModel dfm) {
        //Convert the scrubbed collection into a collection of
        //FordDataBaseMarketing
        Collection<FordDataBaseMarketing> dataBaseMarketings = scrubbed.stream().map(FordDataBaseMarketing::new).collect(toList());
        
        //Convert the scrubbed collection into a collection of FordDirectMailMarketing
        Collection<FordDirectMailMarketing> fordDirectMailMarketings = scrubbed.stream().map(FordDirectMailMarketing::new).collect(toList());

        //Use our Export Dao classes to write the files
        csvExportDao.write(new BufferedOutputStream(dfm.getFordDatabaseMarketing()),
                dataBaseMarketings, FordDataBaseMarketing.class);
        csvExportDao.write(new BufferedOutputStream(dfm.getFordDirectMailMarketing()),
                fordDirectMailMarketings, FordDirectMailMarketing.class);
    }
	
	
	private Collection<ExtendedCustomerInfo> performReduction(Collection<FordGalpinData> fordGalpinDatas,
            												 Collection<FordUnsold> fordUnsolds,
            												 Collection<BiFunction<FordGalpinData, FordUnsold, Boolean>> matchingStrategy,
            												 Consumer<String> messageCallback) {
		//Start the ForkJoinPool for the Fork / Join Framework
		Collection<FordGalpinData> matches = new ForkJoinPool().invoke(
												new ScrubberMatching<>(ImmutableList.copyOf(fordGalpinDatas),
																		ImmutableList.copyOf(fordUnsolds),
																		matchingStrategy,
																		messageCallback, progress));
		//Get the Scrubbed Data
		Collection<FordGalpinData> reduced = fordGalpinDatas.stream().filter(p -> !matches.contains(p)).sorted().collect(toList());

		//Transform them into ExtendedCustomerInfo
		Collection<ExtendedCustomerInfo> results = new TreeSet<>(reduced);

		//Remove the duplicate entries
		results.addAll(fordUnsolds.stream().distinct().collect(toList()));

		//And return an ImmutableList of the data
		return ImmutableList.copyOf(results);
}
	
	
	
	private Collection<BiFunction<FordGalpinData, FordUnsold, Boolean>> generateMatchingStrategy() {
        Collection<BiFunction<FordGalpinData, FordUnsold, Boolean>> functions = new ArrayList<>();
        
        //Go through each FieldMatchingModel
        fieldMatchingModels.forEach(model -> {
            
            //Add a new BiFunction to functions collection
            functions.add((galpin, unsold) ->{
                try {
                	//Read the value from the Galpin Field using Reflection
                    Object lhs = model.getLhs().get(galpin);
                    
                    //Read the value from the Unsold Field using Reflection
                    Object rhs = model.getRhs().get(unsold);

                    if(nonNull(lhs) && nonNull(rhs)){
                        return lhs.toString().equals(rhs.toString());
                    } else {
                        //Here we got null values from one of the two fields
                        //so they can't be equal
                        return false;
                    }
                } catch (IllegalAccessException e) {
                    log.error(e.toString(), e);
                    //Reading the fields using Reflection failed, so throw
                    //an exception
                    throw new RuntimeException(e);
                }
            });
        });
        return functions;
	

	}

	public double calcPercentComplete() {
        double percent = 0.0;
        
        //Only do the calculation if they have uploaded the Galpin
        //and Unsold files
        if(getGalpinRecords().isPresent() && fordUnsoldRecords.isPresent()){
        	
        	//We are just going to calculate a percent here based on the total number of records
        	//and where we are with our progress
            long totalRecords = fordGalpinDataRecords.get().size() + fordUnsoldRecords.get().size();
            
            //ScrubberMatching.SIZE is the number of records we process at a time
            //Everytime we complete a chunk of records, we increment progress by one
            //so get the number of records processed, we multiply SIZE by progress
            
            //Then we divide number of records processed by the total number of records
            long numRecords = ScrubberMatching.SIZE * progress.get(); //Remember that progress
            														  //is an atomic Integer that is updated
            														  //In the ScrubberMatching class
            percent = (double) numRecords / (double) totalRecords;
        }
        return percent;
    }
	
}
