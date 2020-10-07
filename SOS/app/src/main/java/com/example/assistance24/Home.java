package com.example.assistance24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Home extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    ImageView personal,emergency,sos,location;
    private GpsTracker gpsTracker;
    ImageView logout;
    String message;
    ArrayList<String> display;
    ArrayList<String> phone_numbes;
    Double latitude,longitude;
    String locationAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        personal=findViewById(R.id.btn_personal);
        emergency=findViewById(R.id.emergency);
        sos=findViewById(R.id.sos);
        location=findViewById(R.id.location);
        logout=findViewById(R.id.logout);


        try {
            //checking permission for location
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED  ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        //getting Latitude and longitude of current location
        getLocation();
        //getting address from latitude and longitude
        getAddressFromLocation(latitude, longitude,
                getApplicationContext(), new GeocoderHandler());

        location.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(Home.this,Location.class);
               intent.putExtra("longitude",longitude);
               intent.putExtra("latitude",latitude);
               intent.putExtra("address",locationAddress);
               startActivity(intent);
           }
       });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                // Setting Dialog Title
                builder.setTitle("Logout");
                // Setting Dialog Message
                builder.setMessage("Are you sure you want to logout?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                         startActivity(new Intent(Home.this,Login.class));
                            }
                        }).setNegativeButton("CANCEL", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Personal.class));
            }
        });
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Add.class));
            }
        });
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                //checking permission for sms
                if (checkPermission()) {
                    //if permission is already granted
                    Log.e("permission", "Permission already granted.");
                } else {
                    //if permission is not granted request permission
                    requestPermission();
                }
                if(checkPermission()) {
                    db.collection("users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            //retrieving emergency contact details from firebase
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Map<String, Object> dataToLoad = documentSnapshot.getData();
                                display = (ArrayList<String>) dataToLoad.get("names");
                                phone_numbes = (ArrayList<String>) dataToLoad.get("numbers");
                                //sending sms
                                sendSMSMessage();
                            }
                        }
                    });
                }
                else {
                    //if permission denied
                    Toast.makeText(Home.this, "permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
//checking permission
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Home.this, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
//if permission is not enabled,request permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              // permission allowed
                    Toast.makeText(Home.this,
                            "Permission accepted", Toast.LENGTH_LONG).show();
              // if permission is denied
                } else {
                    Toast.makeText(Home.this,
                            "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
  //function to get current location
    public boolean getLocation(){
         gpsTracker = new GpsTracker(Home.this);
        if(gpsTracker.canGetLocation()){
             latitude = gpsTracker.getLatitude();//get latitude
             longitude = gpsTracker.getLongitude();//get longitude
            if(latitude==0.0){
                getLocation();
            }
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //uploading  location data into firebase
            Map<String,Object>data=new HashMap<>();
            data.put("Latitude",latitude);
            data.put("Longitude",longitude);
            db.collection("users").document(id).set(data, SetOptions.merge());
        }else{
            gpsTracker.showSettingsAlert();
        }
        return true;
    }
    //function to send messages
    private void sendSMSMessage() {
            message = "I need help " + "\nLatitude: " + latitude + "\n Longitude: " + longitude + "\nAddress: " + locationAddress;
            try {
                SmsManager smgr = SmsManager.getDefault();
                //sending messages to selected contacts
                for (int i = 0; i < phone_numbes.size(); i++) {
                    if (phone_numbes.get(i) != null) {
                        smgr.sendTextMessage(phone_numbes.get(i), null, message, null, null);
                    }
                }
                Toast.makeText(Home.this, "SMS Sent Successfully ", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                //if sms sent failed
                Toast.makeText(Home.this, "failed", Toast.LENGTH_SHORT).show();
            }
    }
    @Override
    //closing the app on backpressed
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Home.this);
        builder.setMessage("Are you sure you want to Exit?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitFromApp();
                    }
                }).setNegativeButton("CANCEL",null);
        AlertDialog alert= builder.create();
        alert.show();
    }
    private void exitFromApp() {
        ActivityCompat.finishAffinity(Home.this);
        finish();
    }
    //function to get  address from latitude and longitude
    public static void getAddressFromLocation(final double latitude, final double longitude, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(address.getSubLocality()).append("\n");
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getAdminArea()).append("\n");
                        sb.append(address.getCountryName()).append("\n");

                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e("Location Address Loader", "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = " Unable to get address";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            if (locationAddress != null) {
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                //uploading  location data into firebase
                Map<String,Object>data=new HashMap<>();
                data.put("Current Address",locationAddress);
                db.collection("users").document(id).set(data, SetOptions.merge());
            }
            Log.e("location Address=", locationAddress);
        }
    }


}