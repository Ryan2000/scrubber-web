package com.csv.scrubber.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegistration {
	private String firstName;
    private String lastName;
    private String email;
}


