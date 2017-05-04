package com.sahurjt.btsparent.models;

/**
 * Created by Rajat_Sahu on 03-04-2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Kid {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("photo")
    @Expose
    private Object photo;

    @SerializedName("section")
    @Expose
    private String section;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public Object getPhoto() {
        return photo;
    }


    public String getSection() {
        return section;
    }

}