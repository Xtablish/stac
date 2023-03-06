package com.stacit.stac.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Toast;

import com.stacit.stac.R;
import com.stacit.stac.databinding.ActivityLoadingBinding;

import org.w3c.dom.Text;

public class loadingActivity extends AppCompatActivity {

    private ActivityLoadingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
       SignInTab();
    }

    private boolean isNetworkConnected(){
        ConnectivityManager test = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkActiveInfo = test.getActiveNetworkInfo();
        return networkActiveInfo != null && networkActiveInfo.isConnected();
    }

    public boolean internetIsConnected(){
        try{
            String pingCmd = "ping -c l google.com";
            return (Runtime.getRuntime().exec(pingCmd).waitFor() == 0);
        } catch (Exception e){
            return false;
        }
    }

    //basic onClick listener function to change layouts or views
    //and check the internet connection status during the app launch process
    private void SignInTab(){
        binding.textLoadStatus.setText(R.string.loading);
        //when using a physical device add the internetIsConnected to the if statement
        if (isNetworkConnected()){
            binding.textLoadStatus.setText(R.string.loading);
            startActivity(new Intent(getApplicationContext(), signInActivity.class));
        }
        else{
            binding.textLoadStatus.setText(R.string.internet);
            Context context = getApplicationContext();
            CharSequence text = "No Internet Connection";
            int duration = Toast.LENGTH_LONG;

            Toast.makeText(context, text, duration).show();
        }
    }
}