package com.sahurjt.btsparent.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajat_Sahu on 04-04-2017.
 */

public class ActiveRide extends Ride{

    private static final String MSG="Can't use this method in ActiveRide class";

    @SerializedName("kid")
    @Expose
    private Kid kid;

    public Kid getKid(){
        return kid;
    }

    @Override
    public Integer getCompleted() throws NoSuchMethodException {
       throw new NoSuchMethodException(MSG);
    }

    public String getEndGps() throws NoSuchMethodException {
        throw new NoSuchMethodException(MSG);
    }


    public String getEndTime() throws NoSuchMethodException {
        throw new NoSuchMethodException(MSG);
    }

    @Override
    public Boolean isRideCompleted() throws NoSuchMethodException {
        throw new NoSuchMethodException(MSG);
    }
}
