package com.example.assistance24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Personal extends AppCompatActivity {
    private EditText Name,email,phonenumber,alternatenumber,age,address,state,district;
    CheckBox male,female;
    private Button update;
    String nam,em,pn_num,al_num,gen,ag,add,st,dis,gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
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
        update=findViewById(R.id.update);
        final String id= FirebaseAuth.getInstance().getUid();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        //retrieving user details
        db.collection("users").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            try {
                                DocumentSnapshot documentSnapshot=task.getResult();
                                if(documentSnapshot.exists()){
                                    phonenumber.setText(documentSnapshot.getString("phone number"));
                                    Name.setText(documentSnapshot.getString("Name"));
                                    email.setText(documentSnapshot.getString("Email"));
                                    alternatenumber.setText(documentSnapshot.getString("Alternate Mobile number"));
                                    gender=documentSnapshot.getString("Gender");
                                    if(gender.equals("Male")){
                                        male.setChecked(true);
                                    }
                                    else if(gender.equals("Female")){
                                        female.setChecked(true);
                                    }
                                    else if(gender.equals(" ")){
                                        male.setChecked(false);
                                        female.setChecked(false);
                                    }
                                    age.setText(documentSnapshot.getString("Age"));
                                    address.setText(documentSnapshot.getString("Address"));
                                    state.setText(documentSnapshot.getString("State"));
                                    district.setText(documentSnapshot.getString("District"));
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (male.isChecked() && female.isChecked()) {
                    Toast.makeText(Personal.this, "Select one", Toast.LENGTH_SHORT).show();
                } else {
                    try {
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
                        pn_num = phonenumber.getText().toString();
                        //updating user details
                        DocumentReference p = db.collection("users").document(id);
                        p.update("phone number", pn_num);
                        p.update("Name", nam);
                        p.update("Email", em);
                        p.update("Alternate Mobile number", al_num);
                        p.update("Gender", gen);
                        p.update("Age", ag);
                        p.update("Address", add);
                        p.update("State", st);
                        p.update("District", dis);
                        Toast.makeText(Personal.this, "Updated", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}