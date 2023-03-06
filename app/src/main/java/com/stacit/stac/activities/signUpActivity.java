package com.stacit.stac.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.stacit.stac.R;
import com.stacit.stac.databinding.ActivitySignUpBinding;

public class signUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }

    //basic onClick listener function to change layouts or views
    private void setListener(){
        binding.textSignIn.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), signInActivity.class)));
    }
}