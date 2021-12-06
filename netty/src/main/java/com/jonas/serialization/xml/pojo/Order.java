package com.jonas.serialization.xml.pojo;

import lombok.Data;

/**
 * Order
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-29
 */
@Data
public class Order {
    private long orderNumber;
    private Customer customer;
    private Address billTo;
    private Shipping shipping;
    private Address shipTo;
    private Float total;
}
