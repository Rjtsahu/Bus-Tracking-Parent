package com.sahurjt.btsparent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sahurjt.btsparent.interfaces.ApiInterface;
import com.sahurjt.btsparent.models.Kids;
import com.sahurjt.btsparent.models.LoginRequest;
import com.sahurjt.btsparent.models.LoginResponse;
import com.sahurjt.btsparent.network.ApiClient;
import com.sahurjt.btsparent.toasty.Toasty;
import com.sahurjt.btsparent.utils.NetworkHelper;
import com.sahurjt.btsparent.utils.SharedPrefHelper;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_user) // username is email
    TextView txtUser;

    @BindView(R.id.login_pass)
    TextView txtPass;

    @BindView(R.id.btn_login)
    FloatingActionButton btnLogin;

    @BindView(R.id.progress_login)
    ProgressBar progressBar;

    private ApiInterface apiService;
    private SharedPrefHelper sharedPrefInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // bind view with ButterKnife annotation
        ButterKnife.bind(this);
        // get sp instance
        sharedPrefInstance=SharedPrefHelper.getInstance(this);
        loadSpToTextBox();
        //create api service instance
        apiService = ApiClient.getClient().create(ApiInterface.class);
        //TODO remove this line,just for testing
        saveMockLocation();
    }

    @OnClick(R.id.btn_login)
    public void doLogin(){
        final String user=txtUser.getText().toString();
        final String pass=txtPass.getText().toString();
        if (user.equals("")||pass.equals("")){
            Toasty.error(this,"Enter username & password").show();
        }else {
            // check for correct credentials
            if(NetworkHelper.isInternetAvailable(this)){
                // hide button
                hideButton();
                final LoginRequest request=new LoginRequest(user,pass);
                apiService.loginAndGetToken(request).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful()){
                            if (response.body().getStatus().equals(ApiInterface.STATUS_OK)){
                                savePref(response.body());
                                sharedPrefInstance.addString(SharedPrefHelper.LOGIN_USERNAME,user);
                                sharedPrefInstance.addString(SharedPrefHelper.LOGIN_PASSWORD,pass);
                                Toasty.success(getBaseContext(),"OK login").show();
                                Intent i=new Intent(getBaseContext(), HomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                //finish(); // another hack
                            }
                            else {
                                Toasty.error(getBaseContext(),response.body().getMessage()).show();
                            }
                        }else if(response.code()==403){
                            Toasty.error(getBaseContext(),"Invalid Credentials").show();
                        }
                        showButton();
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toasty.info(getBaseContext(),"Network Problem, Try again").show();
                        showButton();
                    }
                });
            }else {
                Toasty.info(this,"Must enable Internet").show();
            }
        }
    }

    private void showButton(){
        btnLogin.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void hideButton(){
        btnLogin.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    // save login response to shared preferences
    private void savePref(LoginResponse response){

        if (response!=null){
            //Map<String,String> map=new HashMap<>();
            sharedPrefInstance.addString(SharedPrefHelper.PARENT_EMAIL,response.getEmail());
            sharedPrefInstance.addString(SharedPrefHelper.PARENT_NAME,response.getName());
            sharedPrefInstance.addString(SharedPrefHelper.LOGIN_TOKEN,response.getToken());
            sharedPrefInstance.addString(SharedPrefHelper.LOGIN_TOKEN_VALIDITY,response.getExpires());
            sharedPrefInstance.addString(SharedPrefHelper.PARENT_PHONE,response.getPhone());
        }
    }

    private void loadSpToTextBox(){
        txtUser.setText(sharedPrefInstance.getString(SharedPrefHelper.LOGIN_USERNAME));
        txtPass.setText(sharedPrefInstance.getString(SharedPrefHelper.LOGIN_PASSWORD));
    }

    private void saveMockLocation(){
        sharedPrefInstance.addString(SharedPrefHelper.HOME_GPS,"23.1758917,75.8076765");
    }
}
