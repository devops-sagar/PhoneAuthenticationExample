package com.topscode.phoneauthenticationexample;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {

    PhoneAuthProvider phoneAuthProvider;

    FirebaseAuth firebaseAuth;
    EditText OTP_EditText;
    String verificationId;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        phoneAuthProvider = PhoneAuthProvider.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();

        OTP_EditText = findViewById(R.id.phone_otp_id);

        String mobile = getIntent().getStringExtra("mobile");

        sendVerificationCode(mobile);

        findViewById(R.id.Verify_button_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode(code);
            }
        });

    }


    private void sendVerificationCode(String number) {

        phoneAuthProvider.verifyPhoneNumber("+91" + number, 60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            code = phoneAuthCredential.getSmsCode();

                if (code != null) {
                OTP_EditText.setText(code);
                verifyCode(code);  //TODO : Automatic Verify Code
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };


    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        SignInPhoneAuthentication(credential);
    }


    private void SignInPhoneAuthentication(PhoneAuthCredential phoneAuthCredential) {

        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful() && task.isComplete()) {
                    Intent intent = new Intent(VerifyActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(VerifyActivity.this, "Verification failed'", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}
