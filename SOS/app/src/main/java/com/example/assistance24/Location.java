package com.example.assistance24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Location extends AppCompatActivity {
    private TextView tvLatitude,tvLongitude,tvAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        tvLatitude = findViewById(R.id.lat);
        tvLongitude = findViewById(R.id.lon);
        tvAddress=findViewById(R.id.add);
        Double latitude=getIntent().getDoubleExtra("latitude",0.0);
        Double longitude=getIntent().getDoubleExtra("longitude",0.0);
        String Address=getIntent().getStringExtra("address");
        //setting location details
        tvLatitude.setText(latitude.toString());
        tvLongitude.setText(longitude.toString());
        tvAddress.setText(Address);

    }

}