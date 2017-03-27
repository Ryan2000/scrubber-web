package com.csv.scrubber.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DownloadFileModel {
	
	//ByteArrayInputStream and ByteArrayOutputStream 
	//These classes are really similar to FileInputStream and FileOutputStream
	//the difference is that FileInputStream represents a File on the file system, ByteArrayInputStream is a byte [] array
	//FileOutputStream is also backed by a File and ByteArrayOutputStream is backed by a byte [] array
	//ByteArrayInputStream and ByteArrayOutputStream are useful because we can write a file to them and hold that file in memory
	//as an array of byte [], Once a file is stored as a byte array, our possibilities are endless
	//we can write it to a real file, upload the data to the cloud, store this file in a database as a blob, etc

	
	/*
	limitation to be aware of:
	When you use the File object, you are writing the data to the hard disk
	when you use byte [], you are writing the information to your machine's ram
	so keep in mind two things actually when using byte rather than file
	first, if you're program crashes, your data is lost
	because it's stored in ram rather than on disk
	second, you can only store files that are smaller than your ram capacity
	*/
	
	//These objects will create dynamically expanding byte [] arrays that
    //store the contents of our files (as if they were a file on the disk)
	private ByteArrayOutputStream FordDirectMailMarketing = new ByteArrayOutputStream();
    private ByteArrayOutputStream FordDatabaseMarketing = new ByteArrayOutputStream();
    private ByteArrayOutputStream scrubberLog = new ByteArrayOutputStream();

    
    //These methods just convert the output streams into input streams
    public ByteArrayInputStream getScrubberLogIn(){
    	//toByteArray() let's us get to the underlying byte array
        return new ByteArrayInputStream(scrubberLog.toByteArray());
    }

    public ByteArrayInputStream getFordDirectMailMarketingIn(){
        return new ByteArrayInputStream(FordDirectMailMarketing.toByteArray());
    }

    public ByteArrayInputStream getFordDatabaseMarketingIn(){
        return new ByteArrayInputStream(FordDatabaseMarketing.toByteArray());
    }

}
