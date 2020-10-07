package com.example.assistance24;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.protobuf.StringValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Add extends AppCompatActivity {
    private static final int REQUEST_CODE =99 ;
    Button b,b1;
    TextView t1,t2;
   ArrayList<String>names=new ArrayList<>();
    ArrayList<String>mobile=new ArrayList<>();
    String num = "";
    String name = "";
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        t1 = findViewById(R.id.t1);
        t2 = findViewById(R.id.t);
        b = findViewById(R.id.button1);
        b1=findViewById(R.id.contacts);
        final String id = FirebaseAuth.getInstance().getUid();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(Add.this,Selected.class));
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                //retrieving contact details from firebase
                DocumentReference mDocRef = db1.collection("users").document(id);
                mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> dataToLoad = documentSnapshot.getData();
                            //storing retrieved contact details in Array Lists
                            ArrayList<String> display = (ArrayList<String>) dataToLoad.get("names");
                            ArrayList<String> phone_numbes = (ArrayList<String>) dataToLoad.get("numbers");
                            //checking number of contacts
                            if (display.size() == 5) {
                                t1.setText("You reached maximum limit");
                                t1.setTextColor(getResources().getColor(R.color.red));
                                t2.setText("");
                                Toast.makeText(Add.this, "only 5 contacts are allowed", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    //checking permission for contacts
                                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED  ) {
                                        ActivityCompat.requestPermissions(Add.this, new String[]{android.Manifest.permission.READ_CONTACTS}, 123);
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                //copying retrieved contact details
                                names= (ArrayList<String>) display.clone();
                                mobile= (ArrayList<String>) phone_numbes.clone();
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        ContactsContract.Contacts.CONTENT_URI);//opening contacts Intent
                                startActivityForResult(intent, REQUEST_CODE);
                                num="";
                                name="";

                            }
                        }
                    }
                });

            }
        });
    }
    @Override
        public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query
                            (contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex
                                (ContactsContract.Contacts._ID));
                        String hasNumber = c.getString(c.getColumnIndex
                                (ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getContentResolver().query
                                    (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (numbers.moveToNext()) {
                                //get selected contact number
                                num = numbers.getString(numbers.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Phone.NUMBER));
                                //get selected contact name
                                name = numbers.getString(numbers.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            }
                            t1.setText(""+name);
                            t2.setText(""+num);
                        }
                    }
                    break;
                }
        }
        if(name!=""&&num!="") {
        names.add(name);
        mobile.add(num);
        final Map<String, ArrayList<String>> array = new HashMap<>();
            array.put("names", names);
            array.put("numbers", mobile);
            final String id = FirebaseAuth.getInstance().getUid();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            //uploading selected contact to firebase
            db.collection("users").document(id).set(array, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Add.this, "Contact added" , Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        //if contact is not selected
        else{
            Toast.makeText(Add.this, "Contact is not selected", Toast.LENGTH_SHORT).show();
        }
    }

}