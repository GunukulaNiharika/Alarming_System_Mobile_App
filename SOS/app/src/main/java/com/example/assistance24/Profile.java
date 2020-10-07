package com.example.assistance24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.protobuf.StringValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    private EditText Name,email,phonenumber,alternatenumber,age,address,state,district;
   private Button save;
    CheckBox male,female;
     String nam,em,al_num,gen,ag,add,st,dis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Name=findViewById(R.id.user);
        email=findViewById(R.id.em);
        phonenumber=findViewById(R.id.phone);
        alternatenumber=findViewById(R.id.Alternate);
        male=findViewById(R.id.male);
        female=findViewById(R.id.female);
        age=findViewById(R.id.age);
        address=findViewById(R.id.address);
        state=findViewById(R.id.state);
        district=findViewById(R.id.district);
        save=findViewById(R.id.save);

        final String id=FirebaseAuth.getInstance().getUid();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                     if(task.isSuccessful()){
                         try {
                             DocumentSnapshot documentSnapshot=task.getResult();
                             if(documentSnapshot.exists()){
                                 phonenumber.setText(documentSnapshot.getString("phone number"));
                             }
                         }catch (Exception e){
                             e.printStackTrace();
                         }
                        }
                    }
                });
       save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (male.isChecked() && female.isChecked()) {
                   Toast.makeText(Profile.this, "Select one", Toast.LENGTH_SHORT).show();
               } else {
                   nam = Name.getText().toString();
                   em = email.getText().toString();
                   al_num = alternatenumber.getText().toString();
                   if (female.isChecked()) {
                       gen = "Female";
                   } else if (male.isChecked()) {
                       gen = "Male";
                   } else {
                       gen = " ";
                   }
                   ag = age.getText().toString();
                   add = address.getText().toString();
                   st = state.getText().toString();
                   dis = district.getText().toString();
                   //uploading users details into firebase
                   final ArrayList<String> names = new ArrayList<>();
                   final ArrayList<String> mobile = new ArrayList<>();
                   final Map<String, Object> data = new HashMap<>();
                   data.put("Name", nam);
                   data.put("Email", em);
                   data.put("Alternate Mobile number", al_num);
                   data.put("Gender", gen);
                   data.put("Age", ag);
                   data.put("Address", add);
                   data.put("State", st);
                   data.put("District", dis);
                   data.put("names", names);
                   data.put("numbers", mobile);
                   db.collection("users").document(id).set(data, SetOptions.merge())
                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(Profile.this, "successful", Toast.LENGTH_SHORT).show();
                                   startActivity(new Intent(Profile.this, Home.class));
                                   finish();
                               }
                           });
               }
           }

       });
    }
}