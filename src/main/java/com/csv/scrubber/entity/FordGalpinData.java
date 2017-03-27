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
public class FordGalpinData implements Comparable<BasicCustomerInfo>, ExtendedCustomerInfo{

    @ScrubberField(displayName = "Account")
    @CsvBindByPosition(position = 0)
    private String acct;

    @ScrubberField(displayName = "First Name")
    @CsvBindByPosition(position = 1)
    private String firstName;

    @ScrubberField(displayName = "Middle Initial")
    @CsvBindByPosition(position = 2)
    private String middlelnit;

    @ScrubberField(displayName = "Last Name")
    @CsvBindByPosition(position = 3)
    private String lastName;

    @ScrubberField(displayName = "Address Line 1")
    @CsvBindByPosition(position = 4)
    private String address1;

    @ScrubberField(displayName = "Address Line 2")
    @CsvBindByPosition(position = 5)
    private String address2;

    @ScrubberField(displayName = "City")
    @CsvBindByPosition(position = 6)
    private String city;

    @ScrubberField(displayName = "State")
    @CsvBindByPosition(position = 7)
    private String state;

    @ScrubberField(displayName = "Zip (First 5 Digits)")
    @CsvBindByPosition(position = 8)
    private String zip5;

    @ScrubberField(displayName = "Zip (Last 4 Digits)")
    @CsvBindByPosition(position = 9)
    private String zip4;

    @ScrubberField(displayName = "Car Make")
    @CsvBindByPosition(position = 10)
    private String make1;

    @ScrubberField(displayName = "Car Model")
    @CsvBindByPosition(position = 11)
    private String model1;

    @ScrubberField(displayName = "Car Year")
    @CsvBindByPosition(position = 12)
    private String year1;

    @ScrubberField(displayName = "Group")
    @CsvBindByPosition(position = 13)
    private String group;

    @ScrubberField(displayName = "Email")
    @CsvBindByPosition(position = 14)
    private String vdeEmail;

    @ScrubberField(displayName = "Fillers")
    @CsvBindByPosition(position = 15)
    private String fillers;

    @Override
    public String getAddressOne() {
        return getAddress1();
    }

    @Override
    public String getAddressTwo() {
        return getAddress2();
    }

    @Override
    public String getZip() {
        return getZip5();
    }

    @Override
    public String getEmailAddress() {
        return getVdeEmail();
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
