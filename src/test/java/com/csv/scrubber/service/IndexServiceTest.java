package com.csv.scrubber.service;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.csv.scrubber.entity.FordGalpinData;
import com.csv.scrubber.entity.FordUnsold;
import com.csv.scrubber.model.FileUploadModel;

import mockit.Expectations;
import mockit.Mocked;

@SpringBootTest // This tells JUnit that we need to have a Spring Boot
				// Environment
@RunWith(SpringJUnit4ClassRunner.class) // This annotation tells JUnit that it
										// needs
										// to run this test with Spring
public class IndexServiceTest {

	@Autowired
	private ScrubberService indexService;
	
	@Test
    public void getFileUploadModel(@Mocked MultipartFile galpin, @Mocked MultipartFile unsold) throws Exception {
        //Check the default state of the FileUploadModel. It should be an empty Optional
        assertThat(indexService.getFileUploadModel().isPresent()).isFalse();
        
        //Call the setter
        indexService.setFileUploadModel(FileUploadModel.builder()
                                        .galpinName("galpin").galpin(galpin)
                                        .unsoldName("unsold").unsold(unsold).build());
        //Make sure it set the optional
        assertThat(indexService.getFileUploadModel().isPresent()).isTrue();
    }

	@Test
	public void setFileUploadModel(@Mocked MultipartFile galpin, @Mocked MultipartFile fordUnsold) throws Exception {
		FileUploadModel fum = FileUploadModel.builder()
				.galpin(galpin)
				.galpinName("galpin")
				.unsold(fordUnsold)
				.unsoldName("unsold")
				.build();
		indexService.setFileUploadModel(fum);
	}

/*
 * we use JMockit or @Mocked annotation bc we do not have a way of uploading a file
 * to server in test environment so we tell JMockit to create fake file upload
 * objects for us to test - galpin, fordUnsold as far as the code goes, doesn't
 * know the objects are fake
 */
	
	@Test
	public void readFileUploadModel(@Mocked MultipartFile galpin, 
	                                @Mocked MultipartFile fordUnsold) throws Exception {
	    //This is how we create input streams out of our files in the
	    //resources folder
	    InputStream unsoldInputStream = IndexServiceTest.class.getResourceAsStream("/Ford_unsold_5yrs.csv");
        InputStream galpinInputStream = IndexServiceTest.class.getResourceAsStream("/Galpin_Ford_Data.csv");
        
        //Expectations is from JMockit
        //It let's us record results to mock objects. In other words, objects that are injected with @Mocked
        //on their own, @Mocked objects don't do anything. They just exist
        //Expectations let us add behavior to these objects
        new Expectations(){{
            galpin.getInputStream(); result = galpinInputStream;
            fordUnsold.getInputStream(); result = unsoldInputStream;
        }};
        
        indexService.setFileUploadModel(
            FileUploadModel.builder()
                .galpin(galpin).galpinName("galpin")
                .unsold(fordUnsold).unsoldName("unsold")
                .build());
        indexService.readFileUploadModel();

        assertThat(indexService.getGalpinRecords().isPresent()).isTrue();
        assertThat(indexService.getFordUnsoldRecords().isPresent()).isTrue();
	}
	
	@Test
	public void readScrubberFields() throws Exception {
	    Map<String, Field> galpinFields = indexService.readScrubberFields(FordGalpinData.class);
	    assertThat(galpinFields).hasSize(16);
        galpinFields.values().stream().forEach(f -> assertThat(f.isAccessible()));  	    
	    
	    Map<String, Field> unsoldFields = indexService.readScrubberFields(FordUnsold.class);
	    assertThat(unsoldFields).hasSize(9);
        unsoldFields.values().stream().forEach(f -> assertThat(f.isAccessible()));
	}
	
}
