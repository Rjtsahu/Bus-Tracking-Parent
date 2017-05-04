package com.sahurjt.btsparent.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
   // private static final String API_BASE_URL = "http://192.168.43.241:5000/api/v1/parent/";
    private static final String API_BASE_URL = "http://sahurjt.pythonanywhere.com/api/v1/parent/";
    //public static final String API_IMAGE_KID_BASE_URL="http://192.168.43.241:5000/api/v1/parent/photo_kid?";
    public static final String API_IMAGE_KID_BASE_URL="http://sahurjt.pythonanywhere.com/api/v1/parent/photo_kid?";

    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
