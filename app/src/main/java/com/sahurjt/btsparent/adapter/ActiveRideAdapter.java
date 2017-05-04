package com.sahurjt.btsparent.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahurjt.btsparent.MapActivity;
import com.sahurjt.btsparent.R;
import com.sahurjt.btsparent.interfaces.ApiInterface;
import com.sahurjt.btsparent.models.ActiveRide;
import com.sahurjt.btsparent.models.ActiveRides;
import com.sahurjt.btsparent.models.LatLong;
import com.sahurjt.btsparent.network.ApiClient;
import com.sahurjt.btsparent.utils.DateTimeUtils;
import com.sahurjt.btsparent.utils.L;
import com.sahurjt.btsparent.utils.LocationHelper;
import com.sahurjt.btsparent.utils.SharedPrefHelper;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Rajat_Sahu on 05-04-2017.
 */

public class ActiveRideAdapter extends RecyclerView.Adapter<ActiveRideAdapter.MyHolder> implements View.OnClickListener {

    private Context context;
    private ActiveRides activeRides;
    private ApiInterface apiService;
    private SharedPrefHelper sharedPrefHelper;
    private String token;
    private final RecyclerView mRecyclerView;

    public ActiveRideAdapter(Context context, ActiveRides rides,final RecyclerView recyclerView) {
        this.context = context;
        this.activeRides = rides;
        apiService = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefHelper = SharedPrefHelper.getInstance(context);
        token = sharedPrefHelper.getString(SharedPrefHelper.LOGIN_TOKEN);
        this.mRecyclerView = recyclerView;
    }

    @Override
    public ActiveRideAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_active_ride, parent, false);
        v.setOnClickListener(this);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(ActiveRideAdapter.MyHolder holder, int position) {
        try {
            populateRecyclerView(holder, position);
        } catch (NullPointerException e) {
            L.err("Cant populate list:" + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return activeRides.getRides().size();
    }

    private void populateRecyclerView(MyHolder holder, int position) {
        ActiveRide ride = activeRides.getRides().get(position);
        String kid_name = ride.getKid().getName();

        int kid_id = ride.getKid().getId();
        String start_time = ride.getStartTime();
        String start_gps = ride.getStartGps();
        String current_gps = ride.getCurrentGps();
        int ride_type = ride.getJourneyType();

        // load image using picasso
        if (ride.getKid().getPhoto() != null) {
            // String kid_photo = ride.getKid().getPhoto().toString();
            final String final_url = ApiClient.API_IMAGE_KID_BASE_URL + "token=" +
                    token + "&kid_id=" + kid_id;
            Picasso.with(context).load(final_url).error(R.drawable.ic_account_circle_grey600_48dp).into(holder.imageView);

        }

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
        holder.txtKidName.setText(kid_name);
        holder.txtRideTime.setText(DateTimeUtils.getPreetyTimeString(start_time));
        holder.txtReachTime.setText(getDistanceString(current_gps));
    }

    private String getDistanceString(String current_loc) {
        // get current distance between bus and home
        // do only if home address is given

        if (current_loc != null) {
            // if school address is present in shared_pref
            String school_loc = sharedPrefHelper.getString(SharedPrefHelper.HOME_GPS);
            if (!school_loc.equals(SharedPrefHelper.DEFAULT_STRING)) {
                // return distance string in km
                LatLong a = new LatLong(school_loc);
                LatLong b = new LatLong(current_loc);
                float distance = LocationHelper.getDistance(a.getLat(), a.getLon(), b.getLat(), b.getLon());
                return distance + " km from home";
            }
        }
        return "";
    }

    // handle
    @Override
    public void onClick(View view) {
        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
        ActiveRide r = activeRides.getRides().get(itemPosition);
        int kid_id = r.getKid().getId();
        // create intent to open map with type # ActiveRide
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra("type", MapActivity.Type.TYPE_ACTIVE_RIDE);
        intent.putExtra("kid_id", kid_id);
        context.startActivity(intent);
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgKid_list)
        public CircleImageView imageView;

        @BindView(R.id.txtKidName_list)
        public TextView txtKidName;

        @BindView(R.id.txtReachingIn_list)
        public TextView txtReachTime;

        // time since ride started
        @BindView(R.id.txtRideTime)
        public TextView txtRideTime;

        @BindView(R.id.imgStartPlace_list)
        public ImageView imgStartTypeIcon;

        @BindView(R.id.imgEndPlace_list)
        public ImageView imgEndTypeIcon;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

    }
}
