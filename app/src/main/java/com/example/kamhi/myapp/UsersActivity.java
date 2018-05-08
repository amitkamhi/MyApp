package com.example.kamhi.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends Activity {

    DatabaseReference databaseReferenceUsers;
    ListView usersList;
    FirebaseListAdapter<User> firebaseListAdapter;
    Button buttonGoToMainPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReferenceUsers.keepSynced(true);
        usersList = (ListView) findViewById(R.id.usersList);

        firebaseListAdapter = new FirebaseListAdapter<User>(
                UsersActivity.this,
                User.class,
                R.layout.users_layout,
                databaseReferenceUsers
        ) {

         @Override
            protected void populateView(View v, User model, int position) {
                Glide.with(UsersActivity.this).load(model.getImage()).into(((CircleImageView)v.findViewById(R.id.userPic)));
                ((TextView)v.findViewById(R.id.userEmail)).setText(model.getEmail());
                ((TextView)v.findViewById(R.id.userName)).setText(model.getName());
            }
        };
        usersList.setAdapter(firebaseListAdapter);

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                User user = firebaseListAdapter.getItem(position);
                Intent goToAccountActivity = new Intent(UsersActivity.this, AccountActivity.class);
                goToAccountActivity.putExtra("userUid", user.getUid().toString());
                startActivity(goToAccountActivity);

            }
        });

        buttonGoToMainPage = (Button) findViewById(R.id.buttonGoToMainPage);

        buttonGoToMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    public static class User {

        String email;
        String image;
        String name;
        String uid;

        public User(String email, String image, String name, String uid) {
            this.email = email;
            this.image = image;
            this.name = name;
            this.uid = uid;
        }

        public User(){
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }
}
