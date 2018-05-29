package com.example.kamhi.myapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

public class AddPhoto extends Activity {
//add photo screen, you can downloal from gallery or take a picture

    Button buttonSelectImage;
    Button buttonCaptureImage;
    EditText editTextTitle;
    EditText editTextDescription;
    Button buttonSubmit;
    ImageView imageView;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceUser;

    static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 2;
    static final int MY_PERMISSIONS_CAMERA = 3;
    ProgressDialog progressDialog;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("photos");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("users").child(MainActivity.currentUserUid);

        buttonSelectImage = (Button) findViewById(R.id.buttonSelectImage);
        buttonCaptureImage = (Button) findViewById(R.id.buttonCaptureImage);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        buttonSubmit = (Button) findViewById(R.id.buttonBuyItem);
        imageView = (ImageView) findViewById(R.id.image);

        progressDialog = new ProgressDialog(AddPhoto.this);

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
            }
        });

        buttonCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), MY_PERMISSIONS_CAMERA);
            }
        });


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

    }

    //start posting the chosen image
    private void startPosting() {
        progressDialog.setMessage("Posting...");
        final String title = editTextTitle.getText().toString().trim();
        final String description = editTextDescription.getText().toString().trim();

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && uri != null){
            progressDialog.show();
            StorageReference filepath = storageReference.child("photos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    databaseReferenceUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final DatabaseReference newPost = databaseReference.push();
                            newPost.child("title").setValue(title);
                            newPost.child("description").setValue(description);
                            newPost.child("image").setValue(downloadUrl.toString());
                            Calendar calander = Calendar.getInstance();
                            String cDay = Integer.toString(calander.get(Calendar.DAY_OF_MONTH));
                            String cMonth = Integer.toString(calander.get(Calendar.MONTH) + 1);
                            String cYear = Integer.toString(calander.get(Calendar.YEAR));
                            newPost.child("date").setValue(cDay+"."+cMonth+"."+cYear);
                            newPost.child("uid").setValue(MainActivity.currentUserUid);
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue());
                            newPost.child("parent").setValue(newPost.getKey());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    MainActivity.currentUserMoney+=100;
                    progressDialog.dismiss();
                    Toast.makeText(AddPhoto.this, "Image was posted", Toast.LENGTH_LONG).show();
                    Intent goToAccountActivity = new Intent(AddPhoto.this, AccountActivity.class);
                    goToAccountActivity.putExtra("userUid", MainActivity.currentUserUid);
                    startActivity(goToAccountActivity);
                }
            });
            filepath.putFile(uri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AddPhoto.this, "Failed to post", Toast.LENGTH_LONG).show();
                }
            });
        }
        else
            Toast.makeText(AddPhoto.this, "You need to fill all filled", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode == MY_PERMISSIONS_READ_EXTERNAL_STORAGE || requestCode == MY_PERMISSIONS_CAMERA)&& resultCode == RESULT_OK){
            uri = data.getData();
            //https://github.com/ArthurHub/Android-Image-Cropper
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1) //square
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                Picasso.with(AddPhoto.this).load(uri).fit().centerCrop().into(imageView);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}