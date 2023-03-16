package com.stacit.stac.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.stacit.stac.BuildConfig;
import androidx.appcompat.app.AppCompatActivity;
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
        checkFirstRun();
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

    //a function that makes calls to the Toast method
    private void showToast(){
        Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
    }

    //this function makes calls to the view to change the progress bar visibility
    private void loading(Boolean isLoading)
    {
        if (isLoading)
        {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    //internet connection test function for application startup
    private void SignInTab(){
        //when using a physical device add the internetIsConnected to the if statement
        if (isNetworkConnected())
        {
            loading(true);
            startActivity(new Intent(getApplicationContext(), signInActivity.class));
        } else if (internetIsConnected())
        {
            loading(true);
            startActivity(new Intent(getApplicationContext(), signInActivity.class));
        } else
        {
            loading(false);
            showToast();
        }
    }

    //check if the application is on its first run after installation
    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int ABSENT = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, ABSENT);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            SignInTab();

        } else if (savedVersionCode == ABSENT) {

            // This is a new install
            startActivity(new Intent(getApplicationContext(), getStartedActivity.class));

        } else if (currentVersionCode > savedVersionCode) {

            //This is an upgrade
            startActivity(new Intent(getApplicationContext(), getStartedActivity.class));
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }}
