package com.sahurjt.btsparent.interfaces;

import android.media.session.MediaSession;

import com.sahurjt.btsparent.models.ActiveRides;
import com.sahurjt.btsparent.models.ArrivingBuses;
import com.sahurjt.btsparent.models.Feedback;
import com.sahurjt.btsparent.models.Kids;
import com.sahurjt.btsparent.models.Location;
import com.sahurjt.btsparent.models.LocationHistory;
import com.sahurjt.btsparent.models.Locations;
import com.sahurjt.btsparent.models.LoginRequest;
import com.sahurjt.btsparent.models.LoginResponse;
import com.sahurjt.btsparent.models.RecentRides;
import com.sahurjt.btsparent.models.Response;
import com.sahurjt.btsparent.models.RideStatus;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface ApiInterface {
    public static String QRY_TOKEN = "token";
    public static String QRY_KID_ID = "kid_id";
    public static String STATUS_OK = "ok";
    public static String STATUS_FAIL = "error";
    public static String STATUS_COMPLETED="completed";

    // method to login driver and get access token
    @POST("access_token")
    Call<LoginResponse> loginAndGetToken(@Body LoginRequest login);

    // get kids detail with bus detail
    @GET("get_kids")
    Call<Kids> getKidsInfo(@Query(QRY_TOKEN) String token);

    // get active ride for particular kid
    @GET("active_rides")
    Call<RideStatus> getCurrentStatusMessage(@Query(QRY_TOKEN) String token, @Query(QRY_KID_ID) String kid_id);

    // get active ride for this parent
    @GET("active_rides")
    Call<ActiveRides> getActiveRides(@Query(QRY_TOKEN) String token);

    // recent ride history for @param#kid_id
    @GET("recent_rides")
    Call<RecentRides> getRecentRides(@Query(QRY_TOKEN) String token, @Query(QRY_KID_ID) String kid_id);

    @POST("feedback")
    Call<Response> sendFeedback(@Query(QRY_TOKEN) String token, @Body Feedback feedback);

    // get currently arriving buses
    @GET("arriving")
    Call<ArrivingBuses> getArrivingBus(@Query(QRY_TOKEN) String token);

    // get current location of bus
    @GET("location")
    Call<Location> getLocation(@Query(QRY_TOKEN) String token, @Query(QRY_KID_ID) String kid_id);

    // get all location history associated to this journey
    @GET("locations")
    Call<Locations> getLocations(@Query(QRY_TOKEN) String token, @Query(QRY_KID_ID) String kid_id);

    // upload kid image to server
    @Multipart
    @POST("upload")
    Call<Response> uploadImage(@Query(QRY_TOKEN) String token,@Query(QRY_KID_ID) String kid_id, @Part MultipartBody.Part image);
}
