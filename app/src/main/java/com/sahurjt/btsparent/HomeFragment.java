package com.sahurjt.btsparent;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sahurjt.btsparent.adapter.KidsHomeAdapter;
import com.sahurjt.btsparent.interfaces.ApiInterface;
import com.sahurjt.btsparent.interfaces.RecyclerItemClickListener;
import com.sahurjt.btsparent.models.Kids;
import com.sahurjt.btsparent.models.LatLong;
import com.sahurjt.btsparent.network.ApiClient;
import com.sahurjt.btsparent.toasty.Toasty;
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

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private static final String TAG = "frag_home";
    private static final int PICK_IMAGE_REQUEST = 104;
    private static String mSelectedKidId;
    @BindView(R.id.txtParentEmail)
    TextView txtEmail;
    @BindView(R.id.txtParentName)
    TextView txtParent;
    @BindView(R.id.txtParentPhone)
    TextView txtPhone;
    @BindView(R.id.listKids_home)
    RecyclerView recyclerView;
    private SharedPrefHelper sharedPrefHelper;
    private ApiInterface apiService;
    private KidsHomeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        sharedPrefHelper = SharedPrefHelper.getInstance(getActivity());
        setParentInfo();
        // create api client
        apiService = ApiClient.getClient().create(ApiInterface.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fillKidsList();
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void setParentInfo() {
        txtParent.setText(sharedPrefHelper.getString(SharedPrefHelper.PARENT_NAME));
        txtEmail.setText(sharedPrefHelper.getString(SharedPrefHelper.PARENT_EMAIL));
        String phone = sharedPrefHelper.getString(SharedPrefHelper.PARENT_PHONE);
        if (phone.equals("")) {
            phone = "Phone number not available";
        }
        txtPhone.setText(phone);

    }

    // fill kids recycler view
    private void fillKidsList() {
        apiService.getKidsInfo(sharedPrefHelper.getString(SharedPrefHelper.LOGIN_TOKEN)).enqueue(new Callback<Kids>() {
            @Override
            public void onResponse(Call<Kids> call, Response<Kids> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(ApiInterface.STATUS_OK)) {
                        L.ok(TAG, "list downloaded");
                        adapter = new KidsHomeAdapter(getActivity(), response.body().getKids());
                        recyclerView.setAdapter(adapter);
                    }
                } else if (response.code() == 403) {
                    // logout this user as it may be unauthorised
                    L.verbose(TAG, "unauthorized user");
                } else {
                    Toasty.error(getActivity(), response.body().getMessage()).show();
                    L.err(TAG, "unknown error");
                }
            }

            @Override
            public void onFailure(Call<Kids> call, Throwable t) {
                Toasty.error(getActivity(), "Network error").show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = null;
            if (mSelectedKidId != null) {
                uri = data.getData();
                Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        int col=cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                       String path= cursor.getString(col);
                        L.verbose("path:" + path + " kid_id" + data.getStringExtra("kid_id"));

                    }
                } finally {
                    cursor.close();
                }

            }
        }
    }


    public void onImageClick() {
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                mSelectedKidId = adapter.getKidId(position);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


    }


    private void uploadImage(String kid_id, String filePath) {
        File file = new File(filePath);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        apiService.uploadImage(sharedPrefHelper.getString(SharedPrefHelper.LOGIN_TOKEN), kid_id, body).enqueue(new Callback<com.sahurjt.btsparent.models.Response>() {
            @Override
            public void onResponse(Call<com.sahurjt.btsparent.models.Response> call, Response<com.sahurjt.btsparent.models.Response> response) {

            }

            @Override
            public void onFailure(Call<com.sahurjt.btsparent.models.Response> call, Throwable t) {
                L.err("cant upload image");
            }
        });
    }
}