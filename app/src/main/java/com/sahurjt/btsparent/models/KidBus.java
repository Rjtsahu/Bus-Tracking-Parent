package com.sahurjt.btsparent.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


// forms combined bus and kid object
public class KidBus {

        @SerializedName("bus")
        @Expose
        private Bus bus;

        @SerializedName("kid")
        @Expose
        private Kid kid;

        public Bus getBus() {
            return bus;
        }

        public Kid getKid() {
            return kid;
        }
    }
