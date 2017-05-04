package com.sahurjt.btsparent.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ride {
    @SerializedName("completed")
    @Expose
    private Integer completed;
    @SerializedName("end_gps")
    @Expose
    private String endGps;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("journey_id")
    @Expose
    private Integer journeyId;
    @SerializedName("journey_type")
    @Expose
    private Integer journeyType;
    @SerializedName("start_gps")
    @Expose
    private String startGps;

    @SerializedName("start_time")
    @Expose
    private String startTime;

    @SerializedName("current_gps")
    @Expose
    private String currentGps;

    public Integer getCompleted() throws NoSuchMethodException {
        return completed;
    }

    public String getEndGps() throws NoSuchMethodException {
        return endGps;
    }


    public String getEndTime() throws NoSuchMethodException {
        return endTime;
    }


    public Integer getJourneyId() {
        return journeyId;
    }


    public Integer getJourneyType() {
        return journeyType;
    }


    public String getStartGps() {
        return startGps;
    }


    public String getStartTime() {
        return startTime;
    }

    public String getCurrentGps() {
        return currentGps;
    }

    // ride started but not completed
    public Boolean isRideCompleted() throws NoSuchMethodException {
        if (this.completed == null) return false;
        if (this.completed == 0) return false;
        else if (this.completed == 1) return true;
        return false;
    }
}