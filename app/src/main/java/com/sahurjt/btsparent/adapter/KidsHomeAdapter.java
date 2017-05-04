package com.sahurjt.btsparent.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sahurjt.btsparent.R;
import com.sahurjt.btsparent.interfaces.ApiInterface;
import com.sahurjt.btsparent.models.Bus;
import com.sahurjt.btsparent.models.Kid;
import com.sahurjt.btsparent.models.KidBus;
import com.sahurjt.btsparent.models.RideStatus;
import com.sahurjt.btsparent.network.ApiClient;
import com.sahurjt.btsparent.utils.L;
import com.sahurjt.btsparent.utils.SharedPrefHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KidsHomeAdapter extends RecyclerView.Adapter<KidsHomeAdapter.MyHolder> {

    private Context context;
    private List<KidBus> kidsList;
    private ApiInterface apiService;
    private SharedPrefHelper sharedPrefHelper;
    private String token;

    public KidsHomeAdapter(Context context, List<KidBus> kids) {
        this.context = context;
        this.kidsList = kids;
        apiService = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefHelper = SharedPrefHelper.getInstance(context);
        token = sharedPrefHelper.getString(SharedPrefHelper.LOGIN_TOKEN);

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kid_home, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        try {
            populateRecylerView(holder, position);
        } catch (NullPointerException e) {
            L.err("Cant populate list:" + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        if (kidsList != null) return kidsList.size();
        else return 0;
    }

    private void populateRecylerView(MyHolder holder, int position) throws NullPointerException {
        KidBus kid_bus = kidsList.get(position);
        Kid kid = kid_bus.getKid();
        Bus bus = kid_bus.getBus();

        holder.txtName.setText(kid.getName());
        holder.txtSection.setText("Section: " + kid.getSection());
        holder.txtBus.setText(bus.getBusName());
        setStatus(holder.txtStatus, kid.getId()); //fetch async using retrofit
        // load image using picasso
        if (kid.getPhoto() != null) {
            // String kid_photo = ride.getKid().getPhoto().toString();
            final String final_url = ApiClient.API_IMAGE_KID_BASE_URL + "token=" +
                    sharedPrefHelper.getString(SharedPrefHelper.LOGIN_TOKEN) + "&kid_id=" + kid.getId();
            Picasso.with(context).load(final_url).error(R.drawable.ic_account_circle_grey600_48dp).into(holder.imgKid);

        }
    }

    // fetch status async
    private void setStatus(final TextView st_text, int kid_id) {
        String pos = String.valueOf(kid_id);
        apiService.getCurrentStatusMessage(this.token, pos).enqueue(new Callback<RideStatus>() {
            @Override
            public void onResponse(Call<RideStatus> call, Response<RideStatus> response) {
                if (response.isSuccessful()) {
                    String t = response.body().getRideStatus();
                    if (!t.contains("no")) {
                        // set to green color
                        st_text.setTextColor(Color.GREEN);
                    }
                    st_text.setText(t);
                }
            }

            @Override
            public void onFailure(Call<RideStatus> call, Throwable t) {

            }
        });
    }



    public String getKidId(int position){
        if(kidsList!=null) return kidsList.get(position).getKid().getId().toString();
        return null;
    }


    public class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgKid_list)
        public CircleImageView imgKid;
        @BindView(R.id.txtKidName_list)
        public TextView txtName;
        @BindView(R.id.txtKidSection_list)
        public TextView txtSection;
        @BindView(R.id.txtStatus_list)
        public TextView txtStatus;
        @BindView(R.id.txtBus_list)
        public TextView txtBus;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
