package com.csv.scrubber.controller; //testing our controllers using Spring *Don't have to fire up a webserver each time

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class IndexControllerTest {

	//Inject the controller into the test class
    @Autowired
    private IndexController indexController;
    
    @Test
    public void index() throws Exception {
        //Setup MockMvc to simulate HTTP requests
        MockMvc mockMvc = standaloneSetup(indexController).build();
        
        //Now test the result of the request
        mockMvc.perform(
        		
        		//Simulate an HTTP get request
        		get("/"))
        
        		//Check that it's returning the correct page (which is the view)
        		.andExpect(view().name("index"))
        		
        		//Check that the model has the correct data attached to it
        		.andExpect(model().attributeExists("fileUploadModel"));
    }
}
