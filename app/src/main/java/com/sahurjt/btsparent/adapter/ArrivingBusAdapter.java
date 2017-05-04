package com.sahurjt.btsparent.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sahurjt.btsparent.MapActivity;
import com.sahurjt.btsparent.R;
import com.sahurjt.btsparent.models.ActiveRide;
import com.sahurjt.btsparent.models.ArrivingBus;
import com.sahurjt.btsparent.models.LatLong;
import com.sahurjt.btsparent.utils.LocationHelper;
import com.sahurjt.btsparent.utils.SharedPrefHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rajat_Sahu on 09-04-2017.
 */

public class ArrivingBusAdapter extends RecyclerView.Adapter<ArrivingBusAdapter.MyHolder> implements View.OnClickListener {

    private Context context;
    private List<ArrivingBus> arrivingBuses;
    private SharedPrefHelper sharedPrefHelper;
    private final RecyclerView mRecyclerView;

    public ArrivingBusAdapter(Context context, List<ArrivingBus> arrivingBuses,final RecyclerView recyclerView) {
        this.arrivingBuses = arrivingBuses;
        this.context = context;
        sharedPrefHelper = SharedPrefHelper.getInstance(context);
        this.mRecyclerView=recyclerView;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_arriving_bus, parent, false);
        v.setOnClickListener(this);
        return new ArrivingBusAdapter.MyHolder(v);

    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        populateList(holder, position);
    }

    private void populateList(MyHolder holder, int position) {
        ArrivingBus detail = arrivingBuses.get(position);
        int bus_id = detail.getBusId();
        int kid_id = detail.getKidId();
        int j_id = detail.getJourneyId();
        String name = detail.getKidName();
        if (name == null) {
            name = "";
        } else if (name.contains(" ")){
            name = name.substring(0, name.indexOf(" ")-1);
        }
        String gps = detail.getCurrentGps();
        String last_update_time = detail.getLastUpdate();
        // TODO: merge kid having same bus
        float distance;
        String home_gps = sharedPrefHelper.getString(SharedPrefHelper.HOME_GPS);
        if (TextUtils.isEmpty(home_gps)) {
            String txt = name.substring(0, name.indexOf(" ")) + "'s bus is arriving.";
            holder.txtArriving.setText(txt);
        } else {
            LatLong latLong1 = new LatLong(gps);
            LatLong latLong2 = new LatLong(home_gps);
            distance = LocationHelper.getDistance(latLong1.getLat(), latLong1.getLon(), latLong2.getLat(), latLong2.getLon());
            String txt = name + "'s bus is just " + distance + " KM away.";
            holder.txtArriving.setText(txt);
        }

    }

    @Override
    public int getItemCount() {
        return arrivingBuses.size();

    }

    @Override
    public void onClick(View view) {
        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
        ArrivingBus bus = arrivingBuses.get(itemPosition);
        int kid_id=bus.getKidId();
        // create intent to open map with type # ArrivingBus
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra("type", MapActivity.Type.TYPE_ARRIVING);
        intent.putExtra("kid_id", kid_id);
        context.startActivity(intent);
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtArriving)
        public TextView txtArriving;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
