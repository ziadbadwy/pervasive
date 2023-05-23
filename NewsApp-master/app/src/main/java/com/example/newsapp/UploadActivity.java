package com.example.newsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsapp.Models.Post;
import com.example.newsapp.databinding.ActivityUploadBinding;

import java.text.BreakIterator;

public class UploadActivity extends AppCompatActivity implements SensorEventListener {

    ActivityUploadBinding binding;
    Uri imageUri;
    boolean imageAdded=false;
    private SensorManager sensorManager;
    private Sensor Temp_sensor;

    private boolean isSensorAvailable;
    private TextView tempSensor;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tempSensor= findViewById(R.id.tempSensor);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!=null){
            Temp_sensor=sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            isSensorAvailable=true;
        }
        else{
            Toast.makeText(this, "No sensor found", Toast.LENGTH_SHORT).show();
            isSensorAvailable=false;
        }






        binding.uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),45);
            }

        });
        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=binding.TitleImg.getText().toString();
                String description=binding.DescpImage.getText().toString();

                if(title.isEmpty() || description.isEmpty()){
                    Toast.makeText(UploadActivity.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(imageAdded) {
                        Bitmap bitmap = ((BitmapDrawable) binding.uploadImage.getDrawable()).getBitmap();
                        Database.uploadImage(bitmap, title)
                                .thenAccept(uri -> {
                                    Database.addPost(new Post(title, description, uri, 0.0f, ""));
                                    Log.d("meow", "onClick: " + uri);
                                    startActivity(new Intent(UploadActivity.this, PostsList.class));
                                });
                    }
                    else{
                        Database.addPost(new Post(title, description, "", 0.0f, ""));
                        startActivity(new Intent(UploadActivity.this, PostsList.class));
                    }
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            binding.uploadImage.setImageURI(imageUri);
            imageAdded=true;
        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        
        tempSensor.setText(sensorEvent.values[0]+" °C");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    protected void onResume() {
        super.onResume();
        if(isSensorAvailable){
            sensorManager.registerListener(this,Temp_sensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    protected void onPause() {
        super.onPause();
        if(isSensorAvailable){
            sensorManager.unregisterListener(this);
        }
    }
}