package com.example.kamhi.myapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends Activity {

    FirebaseAuth firebaseAuth;
    Button buttonSignIn;
    EditText editTextEmail;
    EditText editTextPassword;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressDialog = new ProgressDialog(SignInActivity.this);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void startSignIn() {
        progressDialog.setMessage("Starting sign in");
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, "Sign in problem", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent goToAccountActivity = new Intent(SignInActivity.this, AccountActivity.class);
                        goToAccountActivity.putExtra("userUid", firebaseAuth.getCurrentUser().getUid().toString());
                        progressDialog.dismiss();
                        startActivity(goToAccountActivity);
                    }
                }
            });
        }
        else {
            Toast.makeText(SignInActivity.this, "A field is empty, you need to fill all fields", Toast.LENGTH_LONG).show();
        }
    }
}
