package com.jonas.serialization.xml.pojo;

import lombok.Data;

/**
 * Address
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-29
 */
@Data
public class Address {
    private String street1;
    private String street2;
    private String city;
    private String state;
    private String postCode;
    private String country;
}
