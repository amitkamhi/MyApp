package com.example.kamhi.myapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpAccountActivity extends Activity {

    CircleImageView imageButton;
    EditText editTextUserName;
    EditText editTextBirthday;
    DatePickerDialog.OnDateSetListener dateSetListener;
    EditText editTextPhoneNumber;
    Button buttonSetUp;

    Uri imageUri;
    static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 2;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReferenceUser;
    StorageReference storageReferenceProfilePic;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_account);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReferenceProfilePic = FirebaseStorage.getInstance().getReference().child("profile images");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("users").child(MainActivity.currentUserUid);

        progressDialog = new ProgressDialog(SetUpAccountActivity.this);
        imageButton = (CircleImageView) findViewById(R.id.profilePicture);
        editTextUserName = (EditText) findViewById(R.id.username);
        editTextBirthday = (EditText) findViewById(R.id.userBirthday);
        editTextPhoneNumber = (EditText) findViewById(R.id.userPhoneNumber);
        buttonSetUp = (Button) findViewById(R.id.buttonBuyItem);

        databaseReferenceUser.child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Glide.with(SetUpAccountActivity.this).load(dataSnapshot.getValue()).into(imageButton);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReferenceUser.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            editTextUserName.setText(dataSnapshot.getValue().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        databaseReferenceUser.child("birthday").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            editTextBirthday.setText(dataSnapshot.getValue().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        databaseReferenceUser.child("phoneNumber").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            editTextPhoneNumber.setText(dataSnapshot.getValue().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        editTextBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SetUpAccountActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                editTextBirthday.setText(dayOfMonth + "." + month + "." + year);
            }
        };

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
            }
        });

        buttonSetUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetUpAccount();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSIONS_READ_EXTERNAL_STORAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            //https://github.com/ArthurHub/Android-Image-Cropper
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setFixAspectRatio(true)//circle
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                imageButton.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void startSetUpAccount() {
        String name = editTextUserName.getText().toString().trim();
        String birthday = editTextBirthday.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        progressDialog.setMessage("Setting up account");
        progressDialog.show();
        if (imageUri != null) {
             databaseReferenceUser.child("image").addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     storageReferenceProfilePic.child(dataSnapshot.getValue().toString()).delete();
                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {
                 }
             });
             StorageReference filepath = storageReferenceProfilePic.child(imageUri.getLastPathSegment());
             filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                     databaseReferenceUser.child("image").setValue(downloadUrl.toString());
                 }
             });
             filepath.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     progressDialog.dismiss();
                     Toast.makeText(SetUpAccountActivity.this, "Failed to set profile pic", Toast.LENGTH_LONG).show();
                 }
             });
         }
        if (!TextUtils.isEmpty(name)) {
            databaseReferenceUser.child("name").setValue(name);
        }
        if (!TextUtils.isEmpty(birthday)) {
            databaseReferenceUser.child("birthday").setValue(birthday);
        }
        if (!TextUtils.isEmpty(phoneNumber)) {
            databaseReferenceUser.child("phoneNumber").setValue(phoneNumber);
        }
        progressDialog.dismiss();
        Toast.makeText(SetUpAccountActivity.this, "Account was set up", Toast.LENGTH_LONG).show();
        startActivity(new Intent(SetUpAccountActivity.this, MainActivity.class));
    }
}
