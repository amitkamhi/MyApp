package com.example.kamhi.myapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends Activity {
// private account screen, shows details about the user

    String uid;
    CircleImageView profilePicture;
    ProgressBar progressBar;
    Button buttonGoToMainPage;
    TextView textViewUser;
    TextView textViewMoney;
    DatabaseReference databaseReferenceUser;
    DatabaseReference databaseReferencePhotos;
    GridView photosList;
    ArrayList<FirebasePhoto> photos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        uid = getIntent().getStringExtra("userUid");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        databaseReferenceUser.keepSynced(true);
        buttonGoToMainPage = (Button) findViewById(R.id.buttonGoToMainPage);
        textViewUser = (TextView) findViewById(R.id.textViewUser);
        textViewMoney = (TextView) findViewById(R.id.textViewMoney);
        profilePicture = (CircleImageView) findViewById(R.id.profilePicture);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        textViewMoney.setText(MainActivity.currentUserMoney +"$");

        databaseReferenceUser.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            textViewUser.setText(dataSnapshot.getValue().toString()+"'s page");
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

        databaseReferenceUser.child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            Glide.with(AccountActivity.this).load(dataSnapshot.getValue()).into(profilePicture);
                            progressBar.setVisibility(View.GONE);
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

        buttonGoToMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        databaseReferencePhotos = FirebaseDatabase.getInstance().getReference().child("photos");
        databaseReferencePhotos.keepSynced(true);
        databaseReferencePhotos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot photoSnapshot: dataSnapshot.getChildren()) {
                    FirebasePhoto photo = photoSnapshot.getValue(FirebasePhoto.class);
                    if(uid.equals(photo.getUid()))
                        photos.add(photo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        photosList = (GridView) findViewById(R.id.userPhotos);
        photosList.setAdapter(new ImageAdapter(AccountActivity.this, photos));
        photosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent photoDetailes = new Intent(AccountActivity.this, PhotoDetails.class);
                FirebasePhoto firebasePhoto = photos.get(position);
                photoDetailes.putExtra("title", firebasePhoto.getTitle());
                photoDetailes.putExtra("description", firebasePhoto.getDescription());
                photoDetailes.putExtra("date", firebasePhoto.getDate());
                photoDetailes.putExtra("image", firebasePhoto.getImage());
                photoDetailes.putExtra("username", firebasePhoto.getUsername());
                photoDetailes.putExtra("uid", firebasePhoto.getUid());
                photoDetailes.putExtra("parent", firebasePhoto.getParent());
                startActivity(photoDetailes);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add){
            startActivity(new Intent(AccountActivity.this, AddPhoto.class));
        }

        if(item.getItemId() == R.id.action_messages){
            startActivity(new Intent(AccountActivity.this, MessagesActivity.class));
        }

        if(item.getItemId() == R.id.action_set_up_account){
            startActivity(new Intent(AccountActivity.this, SetUpAccountActivity.class));
        }

        if(item.getItemId() == R.id.action_sign_out){
            new AlertDialog.Builder(AccountActivity.this)
                    .setTitle("Sign out")
                    .setMessage("Do you want to sign out?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("users").child(MainActivity.currentUserUid).child("money").setValue(MainActivity.currentUserMoney);
                            FirebaseDatabase.getInstance().getReference().child("users").child(MainActivity.currentUserUid).child("deals").setValue(MainActivity.currentUserDeals);
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(AccountActivity.this, OptionsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    }).create().show();}

        return super.onOptionsItemSelected(item);
    }

}
