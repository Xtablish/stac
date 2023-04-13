package com.stacit.stac.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.stacit.stac.BuildConfig;
import androidx.appcompat.app.AppCompatActivity;

import com.stacit.stac.activities.StacAI.ConnectionReceiver;
import com.stacit.stac.databinding.ActivityLoadingBinding;

public class loadingActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener
{

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

    private void checkConnection()
    {

        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();

        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");

        // register receiver
        registerReceiver(new ConnectionReceiver(), intentFilter);

        // Initialize listener
        ConnectionReceiver.Listener = this;

        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        // go to the sign in page
        SignInTab(isConnected);
    }

    //a function that makes calls to the Toast method
    private void showToast(){
        Toast.makeText(getApplicationContext(), "Internet connection error", Toast.LENGTH_SHORT).show();
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
    private void SignInTab(Boolean isConnected){
         if (isConnected)
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
    private void checkFirstRun()
    {

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
            checkConnection();

        } else if (savedVersionCode == ABSENT) {

            // This is a new install
            startActivity(new Intent(getApplicationContext(), getStartedActivity.class));

        } else if (currentVersionCode > savedVersionCode) {

            //This is an upgrade
            startActivity(new Intent(getApplicationContext(), getStartedActivity.class));
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        //call the SignInTab function
        SignInTab(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // call method
        checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // call method
        checkConnection();
    }
}
