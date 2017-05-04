package com.sahurjt.btsparent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.sahurjt.btsparent.adapter.RecentRideAdapter;
import com.sahurjt.btsparent.interfaces.ApiInterface;
import com.sahurjt.btsparent.models.Kids;
import com.sahurjt.btsparent.models.RecentRides;
import com.sahurjt.btsparent.network.ApiClient;
import com.sahurjt.btsparent.toasty.Toasty;
import com.sahurjt.btsparent.utils.L;
import com.sahurjt.btsparent.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rajat_Sahu on 05-04-2017.
 */


public class RecentRideFragment extends Fragment {

    // list of kid id,name
    private static List<KidSpinnerHolder> mSpinnerList;
    @BindView(R.id.recyclerRecentKids)
    RecyclerView recyclerView;
    @BindView(R.id.spinnerKids)
    Spinner spinnerKids;
    private RecentRideAdapter recentRideAdapter;
    private ApiInterface apiService;
    private SharedPrefHelper sharedPrefHelper;
    private String mToken;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_ride, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fillKidsSpinner();
        // getList();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefHelper = SharedPrefHelper.getInstance(getActivity());
        mToken = sharedPrefHelper.getString(SharedPrefHelper.LOGIN_TOKEN);

    }

    private void getList(int id) {
        apiService.getRecentRides(sharedPrefHelper.getString(SharedPrefHelper.LOGIN_TOKEN), String.valueOf(id)).enqueue(new Callback<RecentRides>() {
            @Override
            public void onResponse(Call<RecentRides> call, Response<RecentRides> response) {
                if (response.isSuccessful()) {

                    if (recentRideAdapter == null && response.body().getRides() != null) {
                        recentRideAdapter = new RecentRideAdapter(getActivity(), response.body().getRides(), recyclerView);
                        recyclerView.setAdapter(recentRideAdapter);

                    } else {
                        recentRideAdapter.updateList(response.body().getRides());
                    }
                }
            }

            @Override
            public void onFailure(Call<RecentRides> call, Throwable t) {
                Toasty.warning(getActivity(), "problem in connection").show();
            }
        });
    }

    private void fillKidsSpinner() {
        apiService.getKidsInfo(mToken).enqueue(new Callback<Kids>() {
            @Override
            public void onResponse(Call<Kids> call, Response<Kids> response) {
                int count = response.body().getKids().size();
                String[] names = new String[count];
                mSpinnerList = new ArrayList<KidSpinnerHolder>(count);

                for (int i = 0; i < count; i++) {
                    String name = response.body().getKids().get(i).getKid().getName();
                    int id = response.body().getKids().get(i).getKid().getId();
                    names[i] = name;
                    mSpinnerList.add(new KidSpinnerHolder(id, name));
                }
                spinnerKids.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, names));

            }

            @Override
            public void onFailure(Call<Kids> call, Throwable t) {
                L.err("can't load kids spinner");
            }
        });
        onSpinnerClick();
    }


    public void onSpinnerClick() {
        spinnerKids.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                int id = mSpinnerList.get(i).id;
                getList(id); //create or update list

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toasty.info(getActivity(), "Select a kid").show();
            }
        });
    }

    class KidSpinnerHolder {
        int id;
        String name;

        public KidSpinnerHolder(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
