package com.csv.scrubber.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration //Tells Spring this is a configuration class
@EnableWebSecurity //Tells Spring to turn on Web Security
public class SpringSecurityConfig  
    extends WebSecurityConfigurerAdapter { //In order to customize our security, we need to extend this class


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth //The container provides us with an instance of AuthenticationManagerBuilder
                .inMemoryAuthentication() //For now, we will use inMemoryAuthentication, but we will change this to
                						 //database drive later on
                .withUser("admin") //this set's a user name 'admin'
                .password("admin") //this set's a password 'admin'
                .roles("ADMIN"); //this set's a security role (normally USER)
    }
}
