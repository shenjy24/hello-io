package com.jonas.serialization.xml.pojo;

import lombok.Data;

import java.util.List;

/**
 * Customer
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-29
 */
@Data
public class Customer {
    private long customerNumber;
    private String firstName;
    private String lastName;
    private List<String> middleNames;
}
