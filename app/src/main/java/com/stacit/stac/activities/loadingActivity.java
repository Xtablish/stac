package com.stacit.stac.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stacit.stac.R;
import com.stacit.stac.databinding.ActivityLoadingBinding;

public class loadingActivity extends AppCompatActivity {

    //call to the activity locally created binding class for this layout
    private ActivityLoadingBinding binding;

    //this is the default method that's called when this activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
       SignInTab();
    }

    //this checks for network connectivity
    private boolean isNetworkConnected(){
        ConnectivityManager test = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkActiveInfo = test.getActiveNetworkInfo();
        return networkActiveInfo != null && networkActiveInfo.isConnected();
    }

    //this checks for internet access
    public boolean internetIsConnected(){
        try{
            String pingCmd = "ping -c l google.com";
            return (Runtime.getRuntime().exec(pingCmd).waitFor() == 0);
        } catch (Exception e){
            return false;
        }
    }

    //internet connection test function for view change and validation
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