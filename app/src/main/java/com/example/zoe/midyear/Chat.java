package com.example.zoe.midyear;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Chat extends AppCompatActivity {

    private FloatingActionButton send;
    private TextView listOfMessages;
    private EditText input;
    private String roomName, tempKey, chatUser, chatMsg;
    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listOfMessages = (TextView)findViewById(R.id.list_of_messages);
        send = (FloatingActionButton)findViewById(R.id.fab);
        input = (EditText)findViewById(R.id.input);
        roomName = getIntent().getExtras().get("name").toString();

        setTitle("Room - " + roomName);

        root = FirebaseDatabase.getInstance().getReference().getRoot().child(roomName);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map = new HashMap <String, Object>();
                tempKey = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference messageRoot = root.child(tempKey);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("name",FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                map2.put("msg", input.getText().toString());
                input.setText("");
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendChatMessages(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendChatMessages(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}

    private void appendChatMessages(DataSnapshot dataSnap){
        Iterator i = dataSnap.getChildren().iterator();
        while (i.hasNext()){
            chatMsg = (String) ((DataSnapshot)i.next()).getValue();
            chatUser= (String) ((DataSnapshot)i.next()).getValue();

            listOfMessages.append(chatUser +" : "+chatMsg +" \n");
        }
    }
}