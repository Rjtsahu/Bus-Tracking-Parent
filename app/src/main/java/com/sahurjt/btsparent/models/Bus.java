package com.sahurjt.btsparent.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajat_Sahu on 03-04-2017.
 */

public class Bus {

    @SerializedName("bus_id")
    @Expose
    private Integer busId;

    @SerializedName("bus_name")
    @Expose
    private String busName;

    public Integer getBusId() {
        return busId;
    }

    public String getBusName() {
        return busName;
    }

}