package com.example.kamhi.myapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends Activity {

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    Button buttonRegister;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextName;
    ProgressDialog progressDialog;

    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        progressDialog = new ProgressDialog(RegisterActivity.this);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }


        });


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null){

                    //      startActivity(new Intent(MainActivity.this, AccountActivity.class));

                }
            }
        };

    }

    private void startRegister() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name)){

            progressDialog.setMessage("Signing up...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        DatabaseReference usersReference = databaseReference.child(userId);
                        usersReference.child("name").setValue(name);
                        usersReference.child("email").setValue(email);
                        usersReference.child("uid").setValue(userId);
                        usersReference.child("image").setValue("https://firebasestorage.googleapis.com/v0/b/myapp-e45b8.appspot.com/o/defult_profile_pic.png?alt=media&token=e40fb70c-c858-4869-8f37-e3c09666b357");
                        usersReference.child("money").setValue(10000);
                        usersReference.child("deals").setValue(0);
                        usersReference.child("birthday").setValue("00.00.0000");
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Signing up successed", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, SetUpAccountActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Signing up failed", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }
}
