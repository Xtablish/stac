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
        binding.btnSignUp.setOnClickListener(v -> addDataToFirebase());
    }

    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }


    private void addDataToFirebase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("first_name", "Franklin");
        data.put("last_name", "Roberts");

        db.collection("users")
                .add(data)
                .addOnSuccessListener(documentReference -> showToast("Data Inserted"))
                .addOnFailureListener(exception -> showToast(exception.getMessage()));
    }
}