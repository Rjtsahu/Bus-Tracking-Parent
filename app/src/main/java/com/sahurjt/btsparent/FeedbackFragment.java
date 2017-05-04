package com.sahurjt.btsparent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sahurjt.btsparent.interfaces.ApiInterface;
import com.sahurjt.btsparent.models.Feedback;
import com.sahurjt.btsparent.models.Response;
import com.sahurjt.btsparent.network.ApiClient;
import com.sahurjt.btsparent.toasty.Toasty;
import com.sahurjt.btsparent.utils.SharedPrefHelper;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Rajat_Sahu on 07-04-2017.
 */

public class FeedbackFragment extends Fragment {


    @BindView(R.id.feed_title)
    TextView txtTitle;

    @BindView(R.id.feed_detail)
    TextView txtDetail;

    @BindView(R.id.btn_feedback)
    Button btnSend;

    private ApiInterface apiService;
    private String token;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_feedback,container,false);
        ButterKnife.bind(this,v);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFeed();
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        token= SharedPrefHelper.getInstance(getActivity()).getString(SharedPrefHelper.LOGIN_TOKEN);
    }


    public void sendFeed(){
        String title=txtTitle.getText().toString();
        String detail=txtDetail.getText().toString();
        if(title.equals("")||detail.equals("")){
            Toasty.error(getActivity(),"fill both details").show();
            return;
        }

        apiService.sendFeedback(token,new Feedback(title,detail)).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if(response.isSuccessful()){
                    Toasty.success(getActivity(),"message send ...").show();
                    clearBoxes();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toasty.error(getActivity(),"can't send message :-{ ").show();
            }
        });

    }

    private void clearBoxes(){
        this.txtTitle.setText("");
        this.txtDetail.setText("");
    }
}
