package com.csv.scrubber.controller;
//SourceTree Verification


import com.csv.scrubber.model.FileUploadModel;
import com.csv.scrubber.service.ScrubberService;

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.Valid;

@RequestMapping("/")
@CommonsLog
@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class IndexController {
	
	@Autowired
    private ScrubberService indexService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model){
    	//Model is a transport object
    	//anything we pass to it using the addAttribute method will get taken back to the view
    	//on indexController, we passed a new FileUploadModel to the model
        //On a HTTP.Get request, we need to pass a new FileUploadModel to the view (view is our web pages)
        model.addAttribute("fileUploadModel", new FileUploadModel());
        return "index";
    }

    //@Valid annotation - Spring will scan the object for any JSR-303 annotation
    //each field with that annotation will be checked for validity 
    
    @RequestMapping(method = RequestMethod.POST)
    public String uploadFiles(@Valid FileUploadModel fileUploadModel,
                              Errors errors){
    	//errors object - holds info about each field that fails JSR-303 validation
    	//so if user forgets to upload file, receives an error message generated from properties file
        String result = "index";
        if(!errors.hasErrors()){
        	fileUploadModel.writeToTempFiles();
            indexService.setFileUploadModel(fileUploadModel);
            result = "redirect:/matching";
        }
        return result;
    }
}