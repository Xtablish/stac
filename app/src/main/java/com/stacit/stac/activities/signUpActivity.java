package com.stacit.stac.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.stacit.stac.databinding.ActivitySignUpBinding;

import java.util.HashMap;

public class signUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }

    //onClick Listener for view change
    private void setListener(){
        binding.textSignIn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), signInActivity.class)));
    }

}