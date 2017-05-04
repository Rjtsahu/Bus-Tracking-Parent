package com.sahurjt.btsparent.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sahurjt.btsparent.models.LatLong;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Rajat_Sahu on 05-04-2017.
 */

public class LocationHelper {
    private static final Location a = new Location("loc_a");
    private static final Location b = new Location("loc_b");

    // get distance between two coordinates
    public static float getDistance(Double lat_a, Double lon_a, Double lat_b, Double lon_b) {

        a.setLatitude(lat_a);
        a.setLongitude(lon_a);
        b.setLatitude(lat_b);
        b.setLongitude(lon_b);
        return round(a.distanceTo(b)/1000,2);
    }

    private static float round(float d, int decimalPlace) {
        return BigDecimal.valueOf(d).setScale(decimalPlace,BigDecimal.ROUND_HALF_UP).floatValue();
    }


    @Nullable
    public static String getLocationName(Context context, double lat, double lon) {
        Geocoder g = new Geocoder(context);
        try {
            Address address = g.getFromLocation(lat, lon, 1).get(0);
            String address_line = "";
            int max_address = address.getMaxAddressLineIndex();
            for (int i = 0; i < max_address; i++) {

                address_line += address.getAddressLine(i) + " ";
            }
            return address_line;
        } catch (IOException | IndexOutOfBoundsException e) {
        }
        return null;
    }

    public static String getLocationName(Context context, LatLong latLong){
        if(latLong!=null){
            if(latLong.isValid()) return getLocationName(context,latLong.getLat()
            ,latLong.getLon());
        }
        return null;
    }
}
