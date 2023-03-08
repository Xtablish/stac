package com.stacit.stac.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.stacit.stac.R;
import com.stacit.stac.databinding.ActivitySignInBinding;

import java.util.HashMap;

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
        binding.textCreateNewAccount.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), signUpActivity.class)));
        binding.btnSignIn.setOnClickListener(v -> addDataToFirebase());
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