package com.sahurjt.btsparent.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Kids extends Response{

    @SerializedName("kids")
    @Expose
    private List<KidBus> kids = null;

    public List<KidBus> getKids() {
        return kids;
    }
}