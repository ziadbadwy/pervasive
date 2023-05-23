package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapp.Models.User;
import com.example.newsapp.databinding.ActivityLoginBinding;


import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.TokenData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

//import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private String adminEmail="admin";
    private String adminPassword="admin";

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        sharedPreferences=getSharedPreferences("NewsApp",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("Role","");
        editor.apply();
        binding.loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.liEmail.getText().toString();
                String password = binding.loginPassword.getText().toString();

                if(email.isEmpty() && password.isEmpty())
                    Toast.makeText(LoginActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                else {
                    if(email.equals(adminEmail) && password.equals(adminPassword))
                    {
                        editor.putString("Role","Admin");
                        editor.apply();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                        return;
                    }
                    List<User> users=Database.getUsers();
                    for(User user:users)
                    {
                        if(user.getEmail().equals(email) && user.getPassword().equals(password))
                        {
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();
                            return;
                        }
                    }
                    Toast.makeText(LoginActivity.this, "Your Email does not exist!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getApplicationContext(), androidx.appcompat.R.style.Theme_AppCompat));
                View dialogView = getLayoutInflater().inflate(R.layout.forget, null);
                String email = dialogView.findViewById(R.id.editBox).toString();
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        compareEmail(email);
                        dialog.dismiss();
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }});
        binding.LoginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SiginupActivity.class));
            }
        });


        //FaceBook
       CallbackManager callbackManager = CallbackManager.Factory.create();
       LoginButton loginButton = (LoginButton) findViewById(R.id.facebookButton);
       loginButton.setPermissions("email", "public_profile");
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
                                String id = jsonObject.optString("id");
                                String email = jsonObject.optString("email");
                                Log.d("meow", "onCompleted: " + email);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,id,name");
                request.setParameters(parameters);
                request.executeAsync();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onError(@NonNull FacebookException e) {

            }

            @Override
            public void onCancel() {

            }
        });
        //google

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("690521433685-c4htk3264o387fjp7i26g53d5cl6mmlj.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);
        binding.googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, 100);
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
        // check condition
        if (signInAccountTask.isSuccessful()) {
            // Initialize sign in account
            try {
                // Initialize sign in account
                GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                // Check condition
                if (googleSignInAccount != null) {
                    // When sign in account is not equal to null initialize auth credential
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                    // Check credential
                    auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Check condition
                            if (task.isSuccessful()) {
                                   Database.addUser(new User(googleSignInAccount.getDisplayName(),googleSignInAccount.getEmail(),"1234"));
                                   Toast.makeText(LoginActivity.this, "Your Defult Password is 1234", Toast.LENGTH_SHORT).show();
                                   startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                   finish();
                            } else {
                                    Log.d("meow", "onComplete: " + task.getException().getMessage());
                            }
                        }
                    });
                }
            } catch (ApiException e) {
                e.printStackTrace();

            }
        }
        else
        {
            Log.d("meow", "onActivityResult: " + signInAccountTask.getException().getMessage());
        }
    }

    private void compareEmail(String email) {
        if (email.isEmpty()){
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return;
        }
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("meow", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            Log.d("meow", "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("meow", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
}









