package com.stacit.stac.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.stacit.stac.R;
import com.stacit.stac.databinding.ActivitySignInBinding;

public class signInActivity extends AppCompatActivity {


    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }

    //onClick Listener for view change
    private void setListener(){
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), signUpActivity.class)));
    }

}