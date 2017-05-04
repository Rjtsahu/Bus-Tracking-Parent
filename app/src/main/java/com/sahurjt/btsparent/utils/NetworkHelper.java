package com.sahurjt.btsparent.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.sahurjt.btsparent.toasty.Toasty;

public class NetworkHelper {

    // This method still not working as i thought :-(
    public static boolean isInternetAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    // check if google-play service is available in clients device
    public static boolean isPlayServicesAvailable(final Activity ctx) {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        int isAvailable = availability.isGooglePlayServicesAvailable(ctx);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (availability.isUserResolvableError(isAvailable)) {
            Dialog dialog = availability.getErrorDialog(ctx, isAvailable, 0);
            dialog.show();
        }else{
            L.err("cant find play services.");
            Toasty.error(ctx,"cant find play services").show();
        }
        return false;
    }
}
