package com.kafkadiscovery.cs2.model;

import lombok.Data;

@Data
public class Movement {
    private int id;
    private int personId;
    private int stayId;
    private String date;
    private String service;
    private String room;
    private String bed;
}
