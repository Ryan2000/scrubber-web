package com.csv.scrubber.controller;

import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import static java.util.Objects.nonNull;

import java.io.InputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;

import com.csv.scrubber.model.DownloadFileModel;
import com.csv.scrubber.service.ScrubberService;

import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;

@RequestMapping("/scrub")
@CommonsLog
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class ScrubController {
	
	@Autowired
    private ScrubberService scrubberService;
	
	//scrubberService.scrub runs asynchronously so it returns a Future object
    private Future<DownloadFileModel> fileModelFuture;

	@RequestMapping(method=RequestMethod.GET)
	public String scrub(){
		return "scrub";
	
	}
	
	@RequestMapping(path = "/start.begin", method = RequestMethod.POST)
    public String startScrubbing(){
		fileModelFuture = scrubberService.scrub(); //This kicks off scrubbing
		 											//in it's own thread
		//if scrubberService.scrub did not run in it's own thread
		//then the page would not load in the browser until the scrubbing is finished
		//In most cases, the browser would timeout out and never load the page
		
        log.info("Scrubbing has started");
        return "redirect:/scrub";
    }
    
	@SneakyThrows
    @RequestMapping(path = "/start.status", method = RequestMethod.GET)
    public String checkStatus(Model model){
        String outcome = "fragments/progress :: progress";
        
        //First we need to check if scrubbing has started or not
        //If scrubbing hasn't started, fileModelFuture will be null
        //Otherwise it's not null
        if(nonNull(fileModelFuture)){
            
            //We can check if the scrubbing is done by using Future::isDone
            if(fileModelFuture.isDone()){
                //We can report 100% progress since we are done scrubbing
                model.addAttribute("progress", 100);
                //We can render the download links so that the user
                //can download the finished files at this point
                outcome = "fragments/download :: download";
            } else if (fileModelFuture.isCancelled()){
                //The user could also cancel the task.
                //We can check for cancellation by using Future::isCancelled
                log.info("Scrubbing has been cancelled");
            } else {
            	//At this point, the scrubbing is still running.
                //We can use scrubberService::calcPercentComplete to get
                //the progress
                double progress = scrubberService.calcPercentComplete();
                //Convert to a percent and round
                progress = progress * 100;
                progress = Math.round(progress);

                log.info("Scrubbing hasn't finished yet");
                log.info("Progress is " + (int) progress);

                //Send the progress back to the view
                model.addAttribute("progress", (int) progress);
            }
        } else {
            //Scrubbing hasn't started yet, so tell the viewer we are
            //at 0%
            model.addAttribute("progress", 0);
        }
        return outcome;
    }
	
	@RequestMapping(path = "/direct.download", method = RequestMethod.GET)
    @SneakyThrows
    public void downloadDirect(HttpServletResponse httpServletResponse){
		//HttpServletResponse is a class that sends a response back to the
        //web browser
		
        //At this point, the scrubbing is done. We can use future::get to 
        //get the result
        DownloadFileModel fileModel = fileModelFuture.get();
        
        //Now write the file to the servlet's response
        fileDownload(httpServletResponse, fileModel.getFordDirectMailMarketingIn(),
                fileModel.getFordDirectMailMarketing().size(), "direct.csv", "text/csv");
    }
	
	@RequestMapping(path = "/database.download", method = RequestMethod.GET)
    @SneakyThrows
    public void downloadDatabase(HttpServletResponse response){
        DownloadFileModel fileModel = fileModelFuture.get();
        fileDownload(response, fileModel.getFordDatabaseMarketingIn(),
                fileModel.getFordDatabaseMarketing().size(), "database.csv", "text/csv");
    }

	//everytime the program finds a match between customers, it writes that information to a log file
	//View ScrubberMatching for reference -  messageCallback.accept(String.format("Found match %s == > %s", source.toString(), reduce.toString()));
	//View ScrubberServiceImpl for reference - Consumer<String> messageCallback = (msg) -> printWriter.println(msg);
    @RequestMapping(path = "/log.download", method = RequestMethod.GET)
    @SneakyThrows
    public void downloadLog(HttpServletResponse response){
        DownloadFileModel fileModel = fileModelFuture.get();
        fileDownload(response, fileModel.getScrubberLogIn(),
                fileModel.getScrubberLog().size(), "scrubber.log", "text/plain"); //plain text is opened in text editor
    }
    
    @SneakyThrows
    private void fileDownload(HttpServletResponse response, InputStream inputStream, int length, String fName, String mime){
    	//Tell the browser it is dealing with text/csv
        response.setContentType(mime);
        
        //Tell the browser the size of the file
        response.setContentLength(length);

        //These tell the browser what kind of response it is getting
        //you will see that we are telling it that it's getting a file attachment
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", fName);

        response.setHeader(headerKey, headerValue);
        
        //Now, response has an output stream, so we can just copy our file
        //to the outputstream and the browser will downlaod the file!
        
        //IOUtils is an Apace Commons class that has a lot of file utility methods
        //this method makes it easy to copy one file to another
        IOUtils.copy(inputStream, response.getOutputStream());
    }

}




