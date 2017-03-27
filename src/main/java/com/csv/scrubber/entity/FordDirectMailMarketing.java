package com.csv.scrubber.entity;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FordDirectMailMarketing {

    public FordDirectMailMarketing(ExtendedCustomerInfo extendedCustomerInfo){
        this(isNotBlank(extendedCustomerInfo.getFirstName()) ? extendedCustomerInfo.getFirstName() : "",
                isNotBlank(extendedCustomerInfo.getLastName()) ? extendedCustomerInfo.getLastName() : "",
                isNotBlank(extendedCustomerInfo.getAddressOne()) ? extendedCustomerInfo.getAddressOne() : "",
                isNotBlank(extendedCustomerInfo.getAddressTwo()) ? extendedCustomerInfo.getAddressTwo() : "",
                isNotBlank(extendedCustomerInfo.getCity()) ? extendedCustomerInfo.getCity() : "",
                isNotBlank(extendedCustomerInfo.getZip()) ? extendedCustomerInfo.getZip() : "",
                isNotBlank(extendedCustomerInfo.getState()) ? extendedCustomerInfo.getState() : "");
    }

    @CsvBindByPosition(position = 0)
    private String firstName = "";

    @CsvBindByPosition(position = 1)
    private String lastName = "";

    @CsvBindByPosition(position = 2)
    private String address1 = "";

    @CsvBindByPosition(position = 3)
    private String address2 = "";

    @CsvBindByPosition(position = 4)
    private String city = "";

    @CsvBindByPosition(position = 5)
    private String postalCode = "";

    @CsvBindByPosition(position = 6)
    private String state = "";
}
