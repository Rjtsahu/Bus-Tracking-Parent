package com.sahurjt.btsparent.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rajat_Sahu on 09-04-2017.
 */

public class ArrivingBuses extends Response{

    @SerializedName("buses")
    @Expose
    private List<ArrivingBus> arrivingBuses;

    public List<ArrivingBus> getArrivingBuses() {
        return arrivingBuses;
    }
}
