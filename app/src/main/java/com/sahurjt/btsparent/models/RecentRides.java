package com.sahurjt.btsparent.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rajat_Sahu on 04-04-2017.
 */

public class RecentRides extends Response{

    @SerializedName("rides")
    @Expose
    private List<Ride> rides;

    public List<Ride> getRides(){
        return rides;
    }
}
