package com.sahurjt.btsparent.models;
import com.sahurjt.btsparent.utils.L;

/**
 * Created by Rajat_Sahu on 05-04-2017.
 */

public class LatLong {
    // class represent lat-long coordinates
    private Double lat;
    private Double lon;
    private String location;
    private final Double INVALID = 0.0; // well theorytically its valid

    public LatLong(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public LatLong(String location) {
        this.location = location;
        fromString();
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    // set lat lon to 0.0 if can't parse
    private void fromString() {
        if (this.location == null) {
            this.lat = this.lon = INVALID;
        } else {
            if (this.location.equals("")) {
                this.lat = this.lon = INVALID;
            } else {
                //now try to parse
                String temp = this.location;
              try {
                  String lat = temp.substring(0, temp.indexOf(","));
                  String lon = temp.substring(temp.indexOf(",") + 1);

                  this.lat = Double.parseDouble(lat);
                    this.lon = Double.parseDouble(lon);
                } catch (Exception e) {
                    L.err("error in parsing location string :" + temp);
                    this.lat = this.lon = INVALID;
                }

            }
        }
    }

    public Boolean isValid() {
        if (this.lat == 0.0 || this.lon == 0.0) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;

    }

    public String toString() {
        if (this.lat == null || this.lon == null) {
            throw new NullPointerException("latitude or longitude cant be null");
        }
        return String.valueOf(this.lat) + "," + String.valueOf(this.lon);
    }
}