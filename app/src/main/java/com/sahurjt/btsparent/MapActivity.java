package com.sahurjt.btsparent;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sahurjt.btsparent.interfaces.ApiInterface;
import com.sahurjt.btsparent.models.LatLong;
import com.sahurjt.btsparent.models.Location;
import com.sahurjt.btsparent.models.LocationHistory;
import com.sahurjt.btsparent.models.Locations;
import com.sahurjt.btsparent.network.ApiClient;
import com.sahurjt.btsparent.toasty.Toasty;
import com.sahurjt.btsparent.utils.DateTimeUtils;
import com.sahurjt.btsparent.utils.L;
import com.sahurjt.btsparent.utils.LocationHelper;
import com.sahurjt.btsparent.utils.NetworkHelper;
import com.sahurjt.btsparent.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rajat_Sahu on 10-04-2017.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "map_activity";
    //private static final LatLng HOME_GPS = new LatLng(22.2136881, 79.7462894);
    private static final int DEFAULT_ZOOM_LEVEL = 14;
    private static final Handler handler = new Handler();
    private static final int UPDATE_INTERVAL = 10000; // 10 seconds
    private GoogleMap mGoogleMap;
    private Marker mMarker;
    private ApiInterface apiService;
    private SharedPrefHelper sharedPrefHelper;
    private String mToken;
    private Polyline mLine;
    Runnable updateMarkerRunnable = new Runnable() {
        @Override
        public void run() {
            loadLocation(getKidId());
            handler.postDelayed(updateMarkerRunnable, UPDATE_INTERVAL);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (NetworkHelper.isPlayServicesAvailable(this)) {
            apiService = ApiClient.getClient().create(ApiInterface.class);
            sharedPrefHelper = SharedPrefHelper.getInstance(MapActivity.this);
            mToken = sharedPrefHelper.getString(SharedPrefHelper.LOGIN_TOKEN);
            init();
            L.verbose("map init");
        } else {
            // else no google  map layout
            Toasty.error(this, "Play services not found").show();
            finish();
        }
    }

    private void init() {
        MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_frag);
        fragment.getMapAsync(this);
    }

    private void moveCameraHereWithZoom() {
        LatLng home_hps = getHomeGps();
        if (home_hps != null)
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home_hps, DEFAULT_ZOOM_LEVEL));
    }

    private void moveCameraHere(LatLng location) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }

    private void enableMyHomeLocation(boolean enable) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(enable);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        L.verbose("map ready");
        moveCameraHereWithZoom();
        whichMap();
    }

    private void addMarker(LatLng location, String time) {
        if (mGoogleMap != null) {
            MarkerOptions mMarkerOption = new MarkerOptions();
            mMarkerOption.position(location);
            mMarkerOption.title("Bus is here");
            mMarkerOption.snippet(time);
            mMarkerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_24dp));
            removeMarker();
            mMarker = mGoogleMap.addMarker(mMarkerOption);
        }
    }

    private void addCustomMarker(MarkerOptions options) {
        if (mGoogleMap != null && options != null) {
            mGoogleMap.addMarker(options);
        }
    }

    private void removeMarker() {
        if (mGoogleMap != null && mMarker != null) {
            mMarker.remove();
        }
    }

    private void addLine(LatLng from, LatLng to) {
        if (mGoogleMap != null) {
            PolylineOptions lineOption = new PolylineOptions().add(from).add(to).color(Color.GRAY);
            // remove line if already drawn
            if (mLine != null) mLine.remove();
            mLine = mGoogleMap.addPolyline(lineOption);
        }
    }

/*    private void addLine(LatLng ... latlngs){
        if (mGoogleMap != null) {
            PolylineOptions lineOption = new PolylineOptions().add(latlngs).color(Color.GRAY);
            mGoogleMap.addPolyline(lineOption);
        }
    }*/

    private void addLine(List<LatLng> lines) {
        if (mGoogleMap != null) {
            PolylineOptions lineOption = new PolylineOptions().addAll(lines).color(Color.BLUE).geodesic(true);
            mGoogleMap.addPolyline(lineOption);
        }
    }


    private String getKidId() {
        int kid_id = getIntent().getIntExtra("kid_id", -1);
        if (kid_id == -1) {
            throw new UnknownKidIdException("Forgot to send 'kid_id' integer in intent ?");
        }
        return String.valueOf(kid_id);
    }

    private int getType() {
        return getIntent().getIntExtra("type", -1);
    }

    private LatLng getHomeGps() {
        String home_gps = sharedPrefHelper.getString(SharedPrefHelper.HOME_GPS);
        if (!home_gps.equals(SharedPrefHelper.DEFAULT_STRING)) {
            LatLong l = new LatLong(home_gps);
            if (l.isValid()) return new LatLng(l.getLat(), l.getLon());
        }
        return null;
    }


    //create line and marker for recent rides
    private void forRecentRide() {
        // get details from intent
        String start_gps = getIntent().getStringExtra("start_gps");
        String end_gps = getIntent().getStringExtra("end_gps");
        String start_time = getIntent().getStringExtra("start_time");
        String end_time = getIntent().getStringExtra("end_time");
        String current_gps = getIntent().getStringExtra("current_gps");

        LatLong sGps = new LatLong(start_gps);
        LatLong eGps = new LatLong(end_gps);
        LatLong cGps = new LatLong(current_gps);
        if (sGps.isValid()) {
            start_time = DateTimeUtils.getTime(start_time);
            end_time = DateTimeUtils.getTime(end_time);
            MarkerOptions s_options = new MarkerOptions();
            s_options.position(new LatLng(sGps.getLat(), sGps.getLon()));
            s_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            s_options.title("Started :" + start_time);
            s_options.snippet(LocationHelper.getLocationName(this, sGps.getLat(), sGps.getLon()));
            addCustomMarker(s_options);
            if (eGps.isValid()) {
                // draw line
                //addLine(new LatLng(sGps.getLat(), sGps.getLat()), new LatLng(eGps.getLat(), eGps.getLon()));

                MarkerOptions e_options = new MarkerOptions();
                e_options.position(new LatLng(eGps.getLat(), eGps.getLon()));
                e_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                e_options.title("Completed :" + end_time);
                e_options.snippet(LocationHelper.getLocationName(this, eGps.getLat(), eGps.getLon()));
                addCustomMarker(e_options);
            } else if (cGps.isValid()) {
                // draw line
                //addLine(new LatLng(cGps.getLat(), cGps.getLat()), new LatLng(sGps.getLat(), sGps.getLon()));

                MarkerOptions c_options = new MarkerOptions();
                c_options.position(new LatLng(cGps.getLat(), cGps.getLon()));
                c_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                c_options.title("Incomplete ?");
                c_options.snippet(LocationHelper.getLocationName(this, cGps.getLat(), cGps.getLon()));
                addCustomMarker(c_options);
            }
        }
    }


    private void whichMap() {
        int type = getType();
        // handler will be posted in onResume
        if (type == Type.TYPE_ACTIVE_RIDE) {
            // show active ride map
            L.verbose(TAG, "activity for ACTIVE_RIDE started.");
            createPolyline(getKidId());
        } else if (type == Type.TYPE_RECENT_RIDE) {
            // show recent ride map
            L.verbose(TAG, "activity for RECENT_RIDE started.");
            forRecentRide();
        } else if (type == Type.TYPE_ARRIVING) {
            // show arriving bus
            L.verbose(TAG, "activity for ARRIVING started.");
        } else {
            // throw exception
            throw new UnknownTypeException("unknown type,Pass correct intent 'type' from Type class");
        }
        // create home marker
        addMarkerHome(getHomeGps());
    }

    private void addMarkerHome(LatLng pos) {
        if (pos == null) return;
        mGoogleMap.addMarker(new MarkerOptions().position(pos).
                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_black_24dp)))
                .setTitle("My Home | Pick Point");
    }

    // async update current location marker
    private void loadLocation(String kid_id) {
        apiService.getLocation(mToken, kid_id).enqueue(new Callback<Location>() {
            @Override
            public void onResponse(Call<Location> call, Response<Location> response) {
                if (response.isSuccessful()) {
                    Location locationResponse = response.body();
                    if (locationResponse.isCompleted()) {
                        // show completed message
                        Toasty.success(getBaseContext(), "Ride Completed.").show();
                        // remove handler
                        handler.removeCallbacks(updateMarkerRunnable);
                    } else {
                        // update marker
                        LatLong l = new LatLong(locationResponse.getGps());
                        if (l.isValid()) {
                            LatLng pos = new LatLng(l.getLat(), l.getLon());
                            addMarker(pos, DateTimeUtils.getPreetyTimeString(locationResponse.getLastUpdate()));
                            moveCameraHere(pos);
                            L.verbose(TAG, "updating marker here :" + l.getLat() + " , " + l.getLon());

                            // add polyline between home and bus
                            LatLng home_gps = getHomeGps();
                            if (home_gps != null) {
                                addLine(new LatLng(l.getLat(), l.getLon()), home_gps);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Location> call, Throwable t) {
                L.err(TAG, "can't download current location");
            }
        });
    }


    // create poly-lines between all location
    private void createPolyline(String kid_id) {
        apiService.getLocations(mToken, kid_id).enqueue(new Callback<Locations>() {
            @Override
            public void onResponse(Call<Locations> call, Response<Locations> response) {
                if (response.isSuccessful()) {
                    List<LocationHistory> locs = response.body().getLocations();
                    // draw start marker
                    if (locs.size() != 0) {
                        LatLong latLong = new LatLong(locs.get(locs.size() - 1).getGps());
                        addCustomMarker(new MarkerOptions().title(" Bus Trip Started")
                                .position(new LatLng(latLong.getLat(), latLong.getLon()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        );
                    }
                    Collections.sort(locs);
                    List<LatLng> latLngs = new ArrayList<LatLng>();
                    for (LocationHistory l : locs) {
                        LatLong latLong = new LatLong(l.getGps());
                        if (latLong.isValid()) {
                            latLngs.add(new LatLng(latLong.getLat(), latLong.getLon()));
                            L.verbose(TAG, l.getId() + "-line point added :" + l.getGps());
                            // add markers
                            addCustomMarker(new MarkerOptions().position(new LatLng(latLong.getLat(), latLong.getLon()))
                                    .title("time")
                                    .snippet(DateTimeUtils.getTime(l.getLocationTime()))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        }
                    }
                    addLine(latLngs);
                }
            }

            @Override
            public void onFailure(Call<Locations> call, Throwable t) {
                L.err(TAG, "can't download  locations");
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateMarkerRunnable);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // don't update on recent ride
        if (getType() != Type.TYPE_RECENT_RIDE) {
            handler.post(updateMarkerRunnable);
        }
    }

    /*     In this project three type of maps can be used
     1) In 'ActiveRide' draw polyline till known locations,
            and update marker to latest location(use handler here)
     2) In 'Recent Ride' where draw only single line between source and destination
            and make Marker at start and end place (correctly labeled)
     3) In 'ArrivingBus' show marker at home and current location of bus
        and draw possible route using map-api(only if home gps is known)
        */
    public static final class Type {
        public static final int TYPE_ACTIVE_RIDE = 0;
        public static final int TYPE_RECENT_RIDE = 1;
        public static final int TYPE_ARRIVING = 2;
    }

    public class UnknownTypeException extends IllegalArgumentException {
        UnknownTypeException(String msg) {
            super(msg);
        }
    }

    public class UnknownKidIdException extends IllegalArgumentException {
        UnknownKidIdException(String msg) {
            super(msg);
        }
    }

}
