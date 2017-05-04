package com.sahurjt.btsparent.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahurjt.btsparent.MapActivity;
import com.sahurjt.btsparent.R;
import com.sahurjt.btsparent.interfaces.ApiInterface;
import com.sahurjt.btsparent.models.LatLong;
import com.sahurjt.btsparent.models.Ride;
import com.sahurjt.btsparent.network.ApiClient;
import com.sahurjt.btsparent.utils.DateTimeUtils;
import com.sahurjt.btsparent.utils.L;
import com.sahurjt.btsparent.utils.LocationHelper;
import com.sahurjt.btsparent.utils.SharedPrefHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rajat_Sahu on 06-04-2017.
 */

public class RecentRideAdapter extends RecyclerView.Adapter<RecentRideAdapter.MyHolder> implements View.OnClickListener {


    private static final String RIDE_COMPLETE = "Completed";
    private static final String RIDE_INCOMPLETE = "Incomplete";
    private final RecyclerView mRecyclerView;
    private Context context;
    private List<Ride> recentRides;
    private ApiInterface apiService;
    private SharedPrefHelper sharedPrefHelper;
    private String token;

    public RecentRideAdapter(Context context, List<Ride> rides, final RecyclerView recyclerView) {
        this.context = context;
        this.recentRides = rides;
        this.mRecyclerView = recyclerView;
        apiService = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefHelper = SharedPrefHelper.getInstance(context);
        token = sharedPrefHelper.getString(SharedPrefHelper.LOGIN_TOKEN);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recent_ride, parent, false);
        v.setOnClickListener(this);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        try {
            populateRecyclerView(holder, position);
        } catch (NullPointerException | NoSuchMethodException npe) {
            L.err("Can't populate list:" + npe.getMessage());
        }
    }

    private void populateRecyclerView(MyHolder holder, int position) throws NoSuchMethodException {
        Ride ride = recentRides.get(position);
        String start_time = ride.getStartTime();
        String end_time = ride.getEndTime();
        String start_gps = ride.getStartGps();
        String end_gps = ride.getEndGps();
        int ride_type = ride.getJourneyType();

        // load icon for ride
        if (ride_type == 0) {
            // from home to school
            holder.imgStartTypeIcon.setImageResource(R.drawable.home_variant_icon_36dp);
            holder.imgEndTypeIcon.setImageResource(R.drawable.school_icon_black_36dp);
        } else if (ride_type == 1) {
            // from school to home
            holder.imgStartTypeIcon.setImageResource(R.drawable.school_icon_black_36dp);
            holder.imgEndTypeIcon.setImageResource(R.drawable.home_variant_icon_36dp);
        }

        holder.txtRideDate.setText(DateTimeUtils.getPreetyTimeString(start_time));
        // only if ride is completed
        String time_delta;
        if (ride.isRideCompleted()) {
            time_delta = DateTimeUtils.getTime(start_time) + "->" + DateTimeUtils.getTime(end_time);

            LocationLoader l_start = new LocationLoader();
            l_start.execute(new LocViewHolder(holder.txtStartGps, new LatLong(start_gps)));

            LocationLoader l_end = new LocationLoader();
            l_end.execute(new LocViewHolder(holder.txtEndGps, new LatLong(end_gps)));

            holder.txtRideStatus.setText(RIDE_COMPLETE);
            holder.txtRideStatus.setTextColor(Color.GREEN);
            LatLong a = new LatLong(start_gps);
            LatLong b = new LatLong(end_gps);
            if (a.isValid() && b.isValid()) {
                holder.txtRideDistance.setText(LocationHelper.getDistance(a.getLat(), a.getLon(), b.getLat(), b.getLon()) + " km");
            }
        } else {
            time_delta = DateTimeUtils.getTime(start_time) + ": ?";
            // show location name
            // fetch async
            LocationLoader l_start = new LocationLoader();
            l_start.execute(new LocViewHolder(holder.txtStartGps, new LatLong(start_gps)));

            String end_gps_str = "unknown";
            holder.txtEndGps.setText(end_gps_str);

            holder.txtRideStatus.setText(RIDE_INCOMPLETE);
            holder.txtRideStatus.setTextColor(Color.RED);
        }
        holder.txtDateFromTo.setText(time_delta);

    }


    @Override
    public int getItemCount() {
        return recentRides.size();
    }

    public void updateList(List<Ride> rides) {
        // updates recycler view with new 'ride' data
        if (rides != null) {
            this.recentRides.clear();
            this.recentRides.addAll(rides);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
        Ride ride = recentRides.get(itemPosition);

        // create intent to open map with type # ActiveRide
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra("type", MapActivity.Type.TYPE_RECENT_RIDE);
        intent.putExtra("start_gps", ride.getStartGps());
        intent.putExtra("start_time", ride.getStartTime());
        intent.putExtra("current_gps", ride.getCurrentGps());
        try {
            intent.putExtra("end_gps", ride.getEndGps());
            intent.putExtra("end_time", ride.getEndTime());
        } catch (NoSuchMethodException e) {
            L.verbose(e.getMessage());
        }
        context.startActivity(intent);

    }

    private class LocationLoader extends AsyncTask<LocViewHolder, String, String> {
        private LocViewHolder holder;

        @Override
        protected String doInBackground(LocViewHolder... holders) {
            this.holder = holders[0];
            return LocationHelper.getLocationName(context, holder.latLong);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                holder.txtView.setText(s);
            }else {
                holder.txtView.setText(holder.latLong.toString());
            }
        }

    }

    private class LocViewHolder {
        TextView txtView;
        LatLong latLong;

        LocViewHolder(TextView view, LatLong latLong) {
            this.txtView = view;
            this.latLong = latLong;
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgStartPlace_list)
        public ImageView imgStartTypeIcon;

        @BindView(R.id.imgEndPlace_list)
        public ImageView imgEndTypeIcon;

        @BindView(R.id.txtDate_list)
        public TextView txtRideDate;

        @BindView(R.id.txtDistance_list)
        public TextView txtRideDistance;

        @BindView(R.id.txtDateFromTo_list)
        public TextView txtDateFromTo;

        @BindView(R.id.txtStatus_list)
        public TextView txtRideStatus;

        @BindView(R.id.txtGpsStart_list)
        public TextView txtStartGps;

        @BindView(R.id.txtGpsEnd_list)
        public TextView txtEndGps;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
