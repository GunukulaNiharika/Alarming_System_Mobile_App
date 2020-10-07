package com.example.assistance24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.firestore.FieldValue.arrayRemove;

public class Selected extends AppCompatActivity {
TextView n1,n2,n3,n4,n5,num1,num2,num3,num4,num5;
ImageView del1,del2,del3,del4,del5;
    ArrayList<String> display;
    ArrayList<String> phone_numbes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected);
        n1=findViewById(R.id.name1);
        n2=findViewById(R.id.name2);
        n3=findViewById(R.id.name3);
        n4=findViewById(R.id.name4);
        n5=findViewById(R.id.name5);
        num1=findViewById(R.id.number1);
        num2=findViewById(R.id.number2);
        num3=findViewById(R.id.number3);
        num4=findViewById(R.id.number4);
        num5=findViewById(R.id.number5);
        del1=findViewById(R.id.del1);
        del2=findViewById(R.id.del2);
        del3=findViewById(R.id.del3);
        del4=findViewById(R.id.del4);
        del5=findViewById(R.id.del5);
        final FirebaseFirestore db=FirebaseFirestore.getInstance();
        final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
   //retrieving contact details
       db.collection("users").document(id).get()
               .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                      DocumentSnapshot documentSnapshot=task.getResult();
                      if(documentSnapshot.exists()){
                          Map<String, Object> dataToLoad = documentSnapshot.getData();
                           display = (ArrayList<String>) dataToLoad.get("names");
                           phone_numbes = (ArrayList<String>) dataToLoad.get("numbers");
                              try{
                              n1.setText(display.get(0));
                              num1.setText(phone_numbes.get(0));
                              }catch (Exception e) {
                                  e.printStackTrace();
                              }
                              try{
                              n2.setText(display.get(1));
                              num2.setText(phone_numbes.get(1));
                              }catch (Exception e1) {
                                  e1.printStackTrace();
                              }
                              try{
                              n3.setText(display.get(2));
                               num3.setText(phone_numbes.get(2));
                              }catch (Exception e2) {
                                  e2.printStackTrace();
                              }
                               try{
                               n4.setText(display.get(3));
                                num4.setText(phone_numbes.get(3));
                               }catch (Exception e3){
                                   e3.printStackTrace();}
                              try {
                                  n5.setText(display.get(4));
                                  num5.setText(phone_numbes.get(4));
                          }catch (Exception e4){
                              e4.printStackTrace();
                          }

                      }
                   }
               });
//deleting selected contacts
          del1.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                delete(0);

              }
          });
          del2.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  delete(1);

              }
          });
          del3.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  delete(2);
              }
          });
          del4.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  delete(3);

              }
          });
          del5.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  delete(4);
              }
          });

    }
    //function to delete contacts
    private void delete(int i) {
        try{
        final FirebaseFirestore db=FirebaseFirestore.getInstance();
        final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(id);
        Map<String,Object> updates = new HashMap<>();
        updates.put("names", FieldValue.arrayRemove(display.get(i)));
        updates.put("numbers", FieldValue.arrayRemove(phone_numbes.get(i)));
        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
            Toast.makeText(Selected .this," Deleted ",Toast.LENGTH_SHORT).show();
            if(i==0){
                n1.setText("----");
                num1.setText("----");
            }
            if(i==1){
                n2.setText("----");
                num2.setText("----");
            }
            if(i==2){
                n3.setText("----");
                num3.setText("----");
            }
            if(i==3){
                n4.setText("----");
                num4.setText("----");
            }
            if(i==4){
                n5.setText("----");
                num5.setText("----");
            }
    }catch (Exception e){
            e.printStackTrace();
        }
    }
}