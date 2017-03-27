package com.csv.scrubber.entity;

import com.google.common.collect.ComparisonChain;
import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FordUnsold implements Comparable<BasicCustomerInfo>, ExtendedCustomerInfo{

    @ScrubberField(displayName = "State")
    @CsvBindByPosition(position = 0)
    private String state;

    @ScrubberField(displayName = "Address Line One")
    @CsvBindByPosition(position = 1)
    private String address;

    @ScrubberField(displayName = "Address Line Two")
    @CsvBindByPosition(position = 2)
    private String addressLineTwo;

    @ScrubberField(displayName = "City")
    @CsvBindByPosition(position = 3)
    private String city;

    @ScrubberField(displayName = "Company")
    @CsvBindByPosition(position = 4)
    private String company;

    @ScrubberField(displayName = "Email Address")
    @CsvBindByPosition(position = 5)
    private String emailAddress;

    @ScrubberField(displayName = "First Name")
    @CsvBindByPosition(position = 6)
    private String firstName;

    @ScrubberField(displayName = "Last Name")
    @CsvBindByPosition(position = 7)
    private String lastName;

    @ScrubberField(displayName = "Zip")
    @CsvBindByPosition(position = 8)
    private String postalCode;

    @Override
    public String getAddressOne() {
        return getAddress();
    }

    @Override
    public String getAddressTwo() {
        return getAddressLineTwo();
    }

    @Override
    public String getZip() {
        return getPostalCode();
    }

    @Override
    public int compareTo(BasicCustomerInfo o) {
        return ComparisonChain.start().compare(isNotBlank(getLastName()) ? getLastName() : "",
                isNotBlank(o.getLastName()) ? o.getLastName() : "")
                .compare(isNotBlank(getFirstName()) ? getFirstName() : "",
                        isNotBlank(o.getFirstName()) ? o.getFirstName() : "")
                .result();
    }
}
