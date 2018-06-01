package com.example.kamhi.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends Activity {
//Shows users list

    DatabaseReference databaseReferenceUserMessages;
    ListView messagesList;
    FirebaseListAdapter<MessagesActivity.Message> firebaseListAdapter;
    Button buttonGoToMainPage;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        textView = (TextView)findViewById(R.id.textView);
        textView.setText("My Messages");

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseReferenceUserMessages = FirebaseDatabase.getInstance().getReference().child("usersMessages").child(MainActivity.currentUserUid);
        databaseReferenceUserMessages.keepSynced(true);
        messagesList = (ListView) findViewById(R.id.usersList);

        firebaseListAdapter = new FirebaseListAdapter<MessagesActivity.Message>(
                MessagesActivity.this,
                MessagesActivity.Message.class,
                R.layout.users_layout,
                databaseReferenceUserMessages
        ) {
            @Override
            protected void populateView(View v, Message model, int position) {
                Glide.with(MessagesActivity.this).load(model.getImage()).into(((CircleImageView)v.findViewById(R.id.userPic)));
                ((TextView)v.findViewById(R.id.userEmail)).setText(model.getMessage());
                ((TextView)v.findViewById(R.id.userName)).setText("");
            }
        };
        messagesList.setAdapter(firebaseListAdapter);

        buttonGoToMainPage = (Button) findViewById(R.id.buttonGoToMainPage);

        buttonGoToMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    //user class, shows details of user
    public static class Message {

        String message;
        String image;

        public Message(String message, String image) {
            this.message = message;
            this.image = image;
        }

        public Message(){
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

    }

}
