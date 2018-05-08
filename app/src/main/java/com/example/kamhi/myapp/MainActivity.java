package com.example.kamhi.myapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainActivity extends Activity {
    DatabaseReference databaseReferencePhotos;
    GridView photosList;
    Button buttonGoToAccount;
    Button buttonSeeAllUsers;
    ArrayList<FirebasePhoto> photos = new ArrayList<>();
    static String currentUserUid;
    static int currentUserMoney;
    static int currentUserDeals;
    private MyService myService;
  //  private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(currentUserUid == null){
            startActivity(new Intent(MainActivity.this, OptionsActivity.class));
        }
        else{
            FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid).child("money").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentUserMoney = Integer.parseInt(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid).child("deals").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentUserDeals = Integer.parseInt(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        databaseReferencePhotos = FirebaseDatabase.getInstance().getReference().child("photos");
        //databaseReferencePhotos.keepSynced(true);
        databaseReferencePhotos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot photoSnapshot: dataSnapshot.getChildren()) {
                    FirebasePhoto photo = photoSnapshot.getValue(FirebasePhoto.class);
                    photos.add(photo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        });

        photosList = (GridView) findViewById(R.id.photos);
        photosList.setAdapter(new ImageAdapter(MainActivity.this, photos));
        photosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent photoDetailes = new Intent(MainActivity.this, PhotoDetails.class);
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

        buttonGoToAccount = (Button) findViewById(R.id.buttonGoToAccount);
        buttonGoToAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAccountActivity = new Intent(MainActivity.this, AccountActivity.class);
                goToAccountActivity.putExtra("userUid", currentUserUid);
                startActivity(goToAccountActivity);
            }
        });
        buttonSeeAllUsers = (Button) findViewById(R.id.buttonSeeAllUsers);
        buttonSeeAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UsersActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
/*        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                myService = ((MyService.MyBinder)iBinder).getService();
                myService.hideNotification();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
*/
        Intent intent = new Intent(MainActivity.this, MyService.class);
        if (!isMyServiceRunning(MyService.class)){
            startService(intent);
        }
        else{
            stopService(intent);
        }
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(myService!=null){
            myService.showNotification();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add){
            startActivity(new Intent(MainActivity.this, AddPhoto.class));
        }

        if(item.getItemId() == R.id.action_set_up_account){
            startActivity(new Intent(MainActivity.this, SetUpAccountActivity.class));
        }

        if(item.getItemId() == R.id.action_sign_out){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Sign out")
                    .setMessage("Do you want to sign out?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, OptionsActivity.class));
                        }
                    }).create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void dialog(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Sign out")
                .setMessage("Do you want to sign out?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }

    private boolean isMyServiceRunning(Class<?> service){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
