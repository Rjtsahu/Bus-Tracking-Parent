package com.sahurjt.btsparent;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sahurjt.btsparent.adapter.ArrivingBusAdapter;
import com.sahurjt.btsparent.interfaces.ApiInterface;
import com.sahurjt.btsparent.models.ArrivingBuses;
import com.sahurjt.btsparent.network.ApiClient;
import com.sahurjt.btsparent.utils.L;
import com.sahurjt.btsparent.utils.SharedPrefHelper;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rajat_Sahu on 09-04-2017.
 */

public class ArrivingBusFragment extends Fragment {

    private static final String STRING_NO_BUS_TO_ARRIVE = "No active bus.";
    private static final Handler mHandler = new Handler();
    private static final int LIST_UPDATE_TIME = 5000;
    @BindView(R.id.recyclerArrivingBus)
    RecyclerView recyclerView;
    @BindView(R.id.txtNoneArriving)
    TextView txtNotArriving;
    private ApiInterface apiService;
    private ArrivingBusAdapter adapter;
    private SharedPrefHelper sharedPrefHelper;
    private String mToken;
    // runnable that update arriving bus list
    Runnable arrivingRunnable = new Runnable() {
        @Override
        public void run() {
            getList();
            mHandler.postDelayed(arrivingRunnable, LIST_UPDATE_TIME);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_arriving, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHandler.post(arrivingRunnable);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefHelper = SharedPrefHelper.getInstance(getActivity());
        mToken = sharedPrefHelper.getString(SharedPrefHelper.LOGIN_TOKEN);

    }

    private void getList() {
        apiService.getArrivingBus(mToken).enqueue(new Callback<ArrivingBuses>() {
            @Override
            public void onResponse(Call<ArrivingBuses> call, Response<ArrivingBuses> response) {
                if (response.body() != null && response.isSuccessful()) {
                    if (response.body().getArrivingBuses().size() == 0) {
                        txtNotArriving.setVisibility(View.VISIBLE);
                    } else {
                        txtNotArriving.setVisibility(View.GONE);
                        adapter = new ArrivingBusAdapter(getActivity(), response.body().getArrivingBuses(), recyclerView);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrivingBuses> call, Throwable t) {
                L.err("can't load list arrivingBus :" + t.getMessage());
            }
        });
    }



    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(arrivingRunnable);
    }
}
