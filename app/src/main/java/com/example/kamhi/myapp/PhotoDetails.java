package com.example.kamhi.myapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class PhotoDetails extends Activity {

    ImageView photo;
    TextView title;
    TextView description;
    TextView username;
    TextView date;
    Button option;
    ProgressDialog progressDialog;
    DatabaseReference databaseReferenceDeals;
    DatabaseReference databaseReferenceUsers;
    DatabaseReference item;
    MyNotification notif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_details);

        photo = (ImageView) findViewById(R.id.photo);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        username = (TextView) findViewById(R.id.username);
        date = (TextView) findViewById(R.id.date);
        option = (Button) findViewById(R.id.buttonPictureOption);
        progressDialog = new ProgressDialog(PhotoDetails.this);
        databaseReferenceDeals = FirebaseDatabase.getInstance().getReference().child("deals");
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("users");

        Glide.with(PhotoDetails.this).load(getIntent().getStringExtra("image")).into(photo);
        title.setText("Title: "+ getIntent().getStringExtra("title"));
        description.setText("Description: " + getIntent().getStringExtra("description"));
        username.setText(getIntent().getStringExtra("username"));
        date.setText(getIntent().getStringExtra("date"));
        if (MainActivity.currentUserUid.equals(getIntent().getStringExtra("uid"))){
            option.setText("Delete Post");
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(PhotoDetails.this)
                            .setTitle("Delete item")
                            .setMessage("Do you want to delete this item?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.setMessage("deleting item...");
                                    progressDialog.show();
                                    item = FirebaseDatabase.getInstance().getReference().child("photos").child(getIntent().getStringExtra("parent"));
                                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(getIntent().getStringExtra("image"));
                                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            item.removeValue(new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    progressDialog.dismiss();
                                                    Intent goToAccountActivity = new Intent(PhotoDetails.this, AccountActivity.class);
                                                    goToAccountActivity.putExtra("userUid", MainActivity.currentUserUid);
                                                    startActivity(goToAccountActivity);
                                                }
                                            });
                                        }
                                    });

                                }
                            }).create().show();

                }
            });
        }
        else{
            option.setText("Buy Item");
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("buying item...");
                    progressDialog.show();
                    if (MainActivity.currentUserMoney < 100) {
                        Toast.makeText(PhotoDetails.this, "You don't have enoght money to buy this item", Toast.LENGTH_LONG).show();
                        return;
                    }
                    item = FirebaseDatabase.getInstance().getReference().child("photos").child(getIntent().getStringExtra("parent"));
                    final DatabaseReference newDeal = databaseReferenceDeals.push();
                    //write the deal
                    newDeal.child("formerUser").child("uid").setValue(getIntent().getStringExtra("uid"));
                    newDeal.child("formerUser").child("username").setValue(getIntent().getStringExtra("username"));
                    newDeal.child("title").setValue(getIntent().getStringExtra("title"));
                    newDeal.child("description").setValue(getIntent().getStringExtra("description"));
                    newDeal.child("image").setValue(getIntent().getStringExtra("image"));
                    newDeal.child("parent").setValue(getIntent().getStringExtra("parent"));
                    Calendar calander = Calendar.getInstance();
                    String cDay = Integer.toString(calander.get(Calendar.DAY_OF_MONTH));
                    String cMonth = Integer.toString(calander.get(Calendar.MONTH) + 1);
                    String cYear = Integer.toString(calander.get(Calendar.YEAR));
                    newDeal.child("date").setValue(cDay + "." + cMonth + "." + cYear);
                    newDeal.child("newUser").child("uid").setValue(MainActivity.currentUserUid);
                    databaseReferenceUsers.child(MainActivity.currentUserUid).child("name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newDeal.child("newUser").child("username").setValue(dataSnapshot.getValue());
                            //make the change
                            item.child("uid").setValue(MainActivity.currentUserUid);
                            item.child("username").setValue(dataSnapshot.getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    MainActivity.currentUserMoney-=100;
                    databaseReferenceUsers.child(MainActivity.currentUserUid).child("money").setValue(MainActivity.currentUserMoney);
                    MainActivity.currentUserDeals+=1;
                    databaseReferenceUsers.child(MainActivity.currentUserUid).child("deals").setValue(MainActivity.currentUserDeals);
                    //set message to the former user
    //                DatabaseReference message = databaseReferenceUsers.getParent().child(MainActivity.currentUserUid).push();
  //                  message.child("message").setValue("this item was bought");
//                    message.child("image").setValue();
  //                  showNotification(item.child("image").toString());
                    progressDialog.dismiss();
                    Toast.makeText(PhotoDetails.this, "item was bought", Toast.LENGTH_LONG).show();
                    Intent goToAccountActivity = new Intent(PhotoDetails.this, AccountActivity.class);
                    goToAccountActivity.putExtra("userUid", MainActivity.currentUserUid);
                    startActivity(goToAccountActivity);
                }

            });
        }

    }

    public void showNotification(String url){
        this.notif = new MyNotification(this, PhotoDetails.class);
        notif.update(MyNotification.NOTIF1, "An item was brougt from you", url);
    }

}