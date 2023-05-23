package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapp.Models.User;
import com.example.newsapp.databinding.ActivitySiginupBinding;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SiginupActivity extends AppCompatActivity {

    private ActivitySiginupBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySiginupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.signupUsername.getText().toString();
                String email = binding.signupEmail.getText().toString();
                String password = binding.signupPassword.getText().toString();
                String confirmPassword = binding.signupConfirmPassword.getText().toString();


                if(username.isEmpty()&&email.isEmpty() && password.isEmpty() && confirmPassword.isEmpty())
                {
                    Toast.makeText(SiginupActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (password.equals(confirmPassword)) {
                        auth.createUserWithEmailAndPassword(email,password)         //Firebase Authentication
                                .addOnCompleteListener(SiginupActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(SiginupActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                                            Database.addUser(new User(username,email,password));
                                            startActivity(new Intent(SiginupActivity.this,MainActivity.class));
                                        }
                                        else
                                        {
                                            Toast.makeText(SiginupActivity.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(SiginupActivity.this, "Password and Confirm Password should be same", Toast.LENGTH_SHORT).show();
                    }
                }
        }});
        binding.LoginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(SiginupActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}
