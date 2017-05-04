package com.sahurjt.btsparent.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajat_Sahu on 06-04-2017.
 */

public class Feedback {

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("detail")
    private String detail;

    public Feedback(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }
}
