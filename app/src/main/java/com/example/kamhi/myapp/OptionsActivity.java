package com.example.kamhi.myapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class OptionsActivity extends Activity {

    Button buttonGoTOSignIn;
    Button buttonRegister;
    static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 2;
    static final int MY_PERMISSIONS_CAMERA = 3;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            MainActivity.currentUserUid = firebaseAuth.getCurrentUser().getUid().toString();
            startActivity(new Intent(OptionsActivity.this, MainActivity.class));
        }

        buttonGoTOSignIn = (Button) findViewById(R.id.buttonGoToSignIn);
        buttonGoTOSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this,SignInActivity.class));
            }
        });
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this,RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    @Override
    protected void onResume() {
        CheckPermissoins();
        super.onResume();
    }

    private void CheckPermissoins(){
        if ((checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
        }
        if ((checkSelfPermission(android.Manifest.permission.CAMERA)) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
        }
    }

}
