package com.csv.scrubber.model;

//relatively simple class that holds the Fields (Objects) that we will use to extract values from Galpin and Unsold
//we ac ess these objects by using reflection (reflect)

import lombok.*;

import java.lang.reflect.Field;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FieldMatchingModel {
	
	@NonNull
	private Field lhs;
	
	@NonNull
    private Field rhs;

}
