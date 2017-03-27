package com.csv.scrubber.controller;

import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import com.csv.scrubber.model.UserRegistration;

import lombok.extern.apachecommons.CommonsLog;

@Controller
@RequestMapping("/sandbox") // endpoint sandbox must match html file
@CommonsLog
@Scope(WebApplicationContext.SCOPE_SESSION)

public class SandboxController {
	
	private Optional<UserRegistration> registrationOptional = Optional.empty();

	@RequestMapping(method = RequestMethod.GET) // this method handles HTTP Get
												// request - request web browser
												// makes when you navigate to a
												// page
	public String pageLoad(Model model) {
		registrationOptional.ifPresent(userRegistration -> model.addAttribute("registration", userRegistration));
		return "sandbox";
	}

	@RequestMapping(method = RequestMethod.POST) // this method handles post
													// request from browser
	public String RegistrationDataPost(@RequestParam("fName") String fName, @RequestParam("lName") String lName,
			@RequestParam("email") String email, Model model) {
		log.info(String.format("First Name: %s", fName));
		
		UserRegistration userRegistration = 
				UserRegistration.builder() //lombok Constructor - See UserRegistration class
				.firstName(fName)
				.lastName(lName)
				.email(email)
				.build();
		registrationOptional = Optional.of(userRegistration);
		model.addAttribute("registration", registrationOptional.get()); //addAttribute sends info back to view
		return "sandbox";
	}

}
