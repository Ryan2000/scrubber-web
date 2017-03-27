package com.csv.scrubber.service;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import com.csv.scrubber.entity.FordGalpinData;
import com.csv.scrubber.entity.FordUnsold;
import com.csv.scrubber.model.DownloadFileModel;
import com.csv.scrubber.model.FieldMatchingModel;
import com.csv.scrubber.model.FileUploadModel;

import lombok.NonNull;

public interface ScrubberService {

    Optional<FileUploadModel> getFileUploadModel();

    void setFileUploadModel(@NonNull FileUploadModel fileUploadModel);
    
    Optional<Collection<FordGalpinData>> getGalpinRecords();
    
    Optional<Collection<FordUnsold>> getFordUnsoldRecords();
    
    void readFileUploadModel();
    
    Map<String, Field> readScrubberFields(@NonNull Class<?> clazz);
    
    void addFieldMatchingModel(@NonNull FieldMatchingModel fieldMatchingModel);
    //lombok annotation @NonNull - used to ensure that fieldMatchingModel parameter isn't null
    
    Future<DownloadFileModel> scrub();
    //future represents something in the future and has a Get() method, isDone method, and isCancelled method
    //so the idea with a future is you first check isDone && isCancelled to see if the task is complete or not
    //if the task isn't complete, you can do other work while you wait
    //if isDone is true, then follow up with the get() method
    
    //This method will get used to report progress to the user
    double calcPercentComplete();

}
