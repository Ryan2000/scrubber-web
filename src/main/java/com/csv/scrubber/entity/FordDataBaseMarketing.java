package com.csv.scrubber.entity;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FordDataBaseMarketing {
	
	public FordDataBaseMarketing(BasicCustomerInfo basicCustomerInfo){
        this(isNotBlank(basicCustomerInfo.getFirstName()) ? basicCustomerInfo.getFirstName() : "",
                isNotBlank(basicCustomerInfo.getLastName()) ? basicCustomerInfo.getLastName() : "",
                isNotBlank(basicCustomerInfo.getEmailAddress()) ? basicCustomerInfo.getEmailAddress() : "");
    }

    @CsvBindByPosition(position = 0)
    private String firstName = "";

    @CsvBindByPosition (position = 1)
    private String lastName = "";

    @CsvBindByPosition( position = 2)
    private String emailAddress = "";

}
