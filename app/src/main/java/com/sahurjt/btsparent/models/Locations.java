package com.sahurjt.btsparent.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rajat_Sahu on 11-04-2017.
 */

public class Locations extends Response {
    // record for location history
    @SerializedName("locations")
    @Expose
    private List<LocationHistory> locations;

    public List<LocationHistory> getLocations() {
        return locations;
    }

}
