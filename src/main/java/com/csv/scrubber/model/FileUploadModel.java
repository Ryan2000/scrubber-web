package com.csv.scrubber.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadModel {

	//@notnull & @NotEmpty
	// Java EE Standard, JSR-30 - Validate objects in web form submissions
	// and can validate an object before its saved to a db
	// can be used to validate throughout the entire program
	//Java EE standard has a reference implementation - use specific project to give developers
	//an idea of what the tech will do.  JSR-303 used Hibernate Validator as its reference implementation
	
	
	//@NotNull — If the object is null, it will fail validation
    //@NotEmpty - If the string is null or blank, it will fail validation
	
	 
	private MultipartFile galpin;

	private MultipartFile unsold;
	
	private Optional<byte []> galpinTempFile;

    private Optional<byte []> unsoldTempFile;

	@NotEmpty(message = "{galpin.required}")
	private String galpinName;

	@NotEmpty(message = "{unsold.required}")
	private String unsoldName;
	
	@SneakyThrows
    public void writeToTempFiles(){
        galpinTempFile = Optional.of(galpin.getBytes());
        unsoldTempFile = Optional.of(unsold.getBytes());

        galpin = null;
        unsold = null;
    }
}
