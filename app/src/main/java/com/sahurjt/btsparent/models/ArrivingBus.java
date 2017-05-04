package com.sahurjt.btsparent.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajat_Sahu on 09-04-2017.
 */

public class ArrivingBus {

    @SerializedName("id")
    @Expose
    private Integer journeyId;

    @SerializedName("bus_id")
    @Expose
    private Integer busId;

    @SerializedName("kid_id")
    @Expose
    private Integer kidId;

    @SerializedName("name")
    @Expose
    private String kidName;

    @SerializedName("current_gps")
    @Expose
    private String currentGps;

    @SerializedName("last_update")
    @Expose
    private String lastUpdate;

    public Integer getJourneyId() {
        return journeyId;
    }

    public Integer getBusId() {
        return busId;
    }

    public Integer getKidId() {
        return kidId;
    }

    public String getKidName() {
        return kidName;
    }

    public String getCurrentGps() {
        return currentGps;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }


}
