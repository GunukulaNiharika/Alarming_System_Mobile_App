package com.example.assistance24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity {
    String VerificationCode,phone_num;
   PinView pin;
   Button verify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
     pin=findViewById(R.id.pin);
     verify=findViewById(R.id.verify);
      phone_num=getIntent().getStringExtra("phone_num");
        sendVerificationcodeTouser(phone_num);
    verify.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String code1=pin.getText().toString();
            if(code1.isEmpty()||code1.length()<6){
                pin.setError("Wrong OTP");
                pin.requestFocus();
                return;
            }
            else{
                Verify(code1);
            }
        }
    });
    }
    //sending otp to user's device
    private void sendVerificationcodeTouser(String phone_num) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_num,        // Phone number to verify
                60,              // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,    // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            VerificationCode=s;
        }
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
        String code=phoneAuthCredential.getSmsCode();
        if(code!=null)
            Verify(code);
        }
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
       Toast.makeText(OTP.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.d("TAG", e.getMessage());        }
    };
    private void Verify(String code) {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(VerificationCode,code);
        signIn(credential);
    }
    //method to signIn
    private void signIn(PhoneAuthCredential credential) {
        final FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(OTP.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                          final String id=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            final FirebaseFirestore db=FirebaseFirestore.getInstance();
                            //uploading users phone number to firebase
                            final Map<String,Object> data=new HashMap<>();
                            data.put("phone number",phone_num);
                            db.collection("users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot documentSnapshot=task.getResult();
                                    if (documentSnapshot != null) {
                                        if (documentSnapshot.exists()) {
                                            startActivity(new Intent(OTP.this, Home.class));
                                            finish();
                                        } else {
                                            db.collection("users").document(id).set(data);
                                            startActivity(new Intent(OTP.this, Profile.class));
                                            finish();
                                        }
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(OTP.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}