package com.kafkadiscovery.cs2.model;

import lombok.Data;

@Data
public class Person {
    private int id;
    private String firstname;
    private String lastname;
    private Address address;
}
