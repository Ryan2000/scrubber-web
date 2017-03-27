package com.csv.scrubber.controller;

import java.util.Iterator;
import java.lang.reflect.Field;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import com.csv.scrubber.entity.FordGalpinData;
import com.csv.scrubber.entity.FordUnsold;
import com.csv.scrubber.model.FieldMatchingModel;
import com.csv.scrubber.model.FileUploadModel;
import com.csv.scrubber.service.ScrubberService;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


@RequestMapping("/matching")
@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)

public class MatchingController {
	
	@Autowired
    private ScrubberService scrubberService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String requestGet(Model model){
		//Model is a transport object
		//anything we pass to it using the addAttribute method will get taken back to the view
		//in this case passing 2 maps back to view
		//we are passing 2 Map<String, Field> to the view *See indexService Interface (ReadScrubberFields)
		model.addAttribute("galpinHeaders",
				scrubberService.readScrubberFields(FordGalpinData.class));
		model.addAttribute("unsoldHeaders",
				scrubberService.readScrubberFields(FordUnsold.class));
		return "matching";
	}
	
	//the post method is what fires when the user hit's the submit button 
	//on the form we were writing in the html page
	//the outcome of click on submit
	@RequestMapping(method = RequestMethod.POST)
    public String requestPost(@RequestParam Map<String, String> params){
        //First remove the Cross Site Forgery token
        if(params.containsKey("_csrf")){
            params.remove("_csrf");
        }
        
        //Grab the reflection information from FordUnsold and FordGalpinData
        Map<String, Field> galpinFields = scrubberService.readScrubberFields(FordGalpinData.class);
        Map<String, Field> unsoldFields = scrubberService.readScrubberFields(FordUnsold.class);
        
        //Now we are going to process our parameters
        Iterator<String> iterator = params.keySet().iterator();
        while(iterator.hasNext()){
            //Get the next two values. FordGalpin and FordUnsold
            //come in pairs from the form
            String galpinKey = iterator.next();
            String unsoldKey = iterator.next();
            
            //Make sure both Galpin and Unsold are present. Otherwise this doesn't work
            if(isNotBlank(params.get(galpinKey)) && isNotBlank(params.get(unsoldKey))){
            	//Look up the field objects from galpinFields and unsoldFields
            	Field galpinField = galpinFields.get(params.get(galpinKey));
                Field unsoldField = unsoldFields.get(params.get(unsoldKey));
            
              //Now we are going to call a method on scrubberService
                //that doesn't exist yet
                scrubberService.addFieldMatchingModel(
                    FieldMatchingModel.builder()
                        .lhs(galpinField)
                        .rhs(unsoldField)
                        .build());
            }
        }
        return "redirect:/scrub";
    }

}





