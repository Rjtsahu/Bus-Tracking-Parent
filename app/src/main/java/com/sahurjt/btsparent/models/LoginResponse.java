package com.sahurjt.btsparent.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajat_Sahu on 03-04-2017.
 */

public class LoginResponse extends Response {

    @SerializedName("expires")
    @Expose
    private String expires;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("token")
    @Expose
    private String token;

    public String getExpires() {
        return expires;
    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }


    public String getToken() {
        return token;
    }


}
