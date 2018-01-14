package com.example.zoe.midyear;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Events extends AppCompatActivity implements View.OnClickListener{

    private EventInfo e;
    private UserInfo u;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    private TextView a, l, v, d, t;
    private Button toProfile, toChat, findEvent, toSongkick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, StartActivity.class));
        }

        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        toProfile = (Button) findViewById(R.id.toProfile1);
        toProfile.setOnClickListener(this);

        toChat= (Button) findViewById(R.id.toChat1);
        toChat.setOnClickListener(this);

        toSongkick = (Button) findViewById(R.id.toSongkick);
        toSongkick.setOnClickListener(this);

        findEvent = (Button) findViewById(R.id.newEvent);
        findEvent.setOnClickListener(this);

        a = (TextView) findViewById(R.id.textViewArtist);
        l = (TextView) findViewById(R.id.textViewLocation);
        d = (TextView) findViewById(R.id.textViewDate);
        t = (TextView) findViewById(R.id.textViewTime);
        v = (TextView) findViewById(R.id.textViewVenue);



    }

    @Override
    protected void onStart(){
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                u = dataSnapshot.child(user.getUid()).getValue(UserInfo.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.toProfile1:
                startActivity(new Intent(this, Profile.class));
                break;
            case R.id.toChat1:
                startActivity(new Intent(this, Chat.class));
                break;
            case R.id.toSongkick:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(e.getLink()));
                startActivity(browserIntent);
                break;
            case R.id.newEvent:
                getNewEvent();
                break;
        }
    }

    public void getNewEvent(){
        APIRequest apiRequest;
        if(u.artist==null){
            apiRequest = new APIRequest(u.location, getApplicationContext());
        }
        else{
            apiRequest = new APIRequest(u.location, u.artist, getApplicationContext());
        }

        e = new EventInfo(apiRequest);

        a.setText("ARTIST: "+e.getArtist());
        l.setText("LOCATION: "+e.getLocation());
        d.setText("DATE: "+e.getDate());
        t.setText("TIME: "+e.getTime());
        v.setText("VENUE: "+e.getVenue());
    }
}
