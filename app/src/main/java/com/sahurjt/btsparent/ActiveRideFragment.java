package com.sahurjt.btsparent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahurjt.btsparent.adapter.ActiveRideAdapter;
import com.sahurjt.btsparent.interfaces.ApiInterface;
import com.sahurjt.btsparent.models.ActiveRides;
import com.sahurjt.btsparent.network.ApiClient;
import com.sahurjt.btsparent.toasty.Toasty;
import com.sahurjt.btsparent.utils.L;
import com.sahurjt.btsparent.utils.SharedPrefHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rajat_Sahu on 05-04-2017.
 */

public class ActiveRideFragment extends Fragment {

    @BindView(R.id.listActiveRide)
    RecyclerView recyclerViewActiveRide;

    // @BindView(R.id.listActiveBus)
    //  RecyclerView recyclerViewActiveBus;

    // @BindView(R.id.txtActive_Ride_header)
    // TextView txtHeader;
    ActiveRideAdapter activeRideAdapter;
    private ApiInterface apiService;
    private SharedPrefHelper sharedPrefHelper;
    //private ActiveRides rides;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_ride, container, false);
        ButterKnife.bind(this, view);
        recyclerViewActiveRide.setLayoutManager(new LinearLayoutManager(getActivity()));
        getList();
       /* recyclerViewActiveRide.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerViewActiveRide, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        */
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefHelper = SharedPrefHelper.getInstance(getActivity());

    }

    private void getList() {
        apiService.getActiveRides(sharedPrefHelper.getString(SharedPrefHelper.LOGIN_TOKEN)).enqueue(new Callback<ActiveRides>() {
            @Override
            public void onResponse(Call<ActiveRides> call, Response<ActiveRides> response) {
                if (response.isSuccessful()) {
                    ActiveRides rides = response.body();
                    activeRideAdapter = new ActiveRideAdapter(getActivity(), rides, recyclerViewActiveRide);
                    recyclerViewActiveRide.setAdapter(activeRideAdapter);
                }
            }

            @Override
            public void onFailure(Call<ActiveRides> call, Throwable t) {
                Toasty.info(getActivity(), "cant load list").show();
                L.err("can't download active_ride list");
            }
        });
    }

    // add arriving bus fragment
    private void addFrag() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frag_arrived, new ArrivingBusFragment()).commit();
    }
}
