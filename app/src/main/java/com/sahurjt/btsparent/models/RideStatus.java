package com.sahurjt.btsparent.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajat_Sahu on 05-04-2017.
 */

public class RideStatus extends Response{
    @SerializedName("rides")
    @Expose
    private String rides;

    public String getRideStatus(){
        return rides;
    }
}
