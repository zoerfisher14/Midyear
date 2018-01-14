package com.example.zoe.midyear;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Profile extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private Button logout, save, toEvents, toChat;
    private DatabaseReference databaseReference;
    private EditText getName, getPhone, getLocation, getArtist;
    private FirebaseUser user;
    private UserInfo currentUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, StartActivity.class));
        }

        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getName = findViewById(R.id.getName);
        getPhone = findViewById(R.id.getPhoneNumber);
        getLocation = findViewById(R.id.getLocation);
        getArtist = findViewById(R.id.getArtist);

        save = findViewById(R.id.save);
        save.setOnClickListener(this);

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);

        toEvents = findViewById(R.id.toFindEvents);
        toEvents.setOnClickListener(this);

        toChat = findViewById(R.id.toChat);
        toChat.setOnClickListener(this);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 07);
                calendar.set(Calendar.MINUTE, 00);
                calendar.set(Calendar.SECOND, 30);
                Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUserInfo = dataSnapshot.child(user.getUid()).getValue(UserInfo.class);
                updateProfileScreen();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateProfileScreen(){
        if(currentUserInfo!=null){
            getName.setText(currentUserInfo.name);
            getPhone.setText(currentUserInfo.phone);
            getLocation.setText(currentUserInfo.location);
            getArtist.setText(currentUserInfo.artist);
        }
    }

    private void saveUserInfo(){
        String name = getName.getText().toString();
        String phone = getPhone.getText().toString();
        String location = getLocation.getText().toString();
        String artist = getArtist.getText().toString();
        UserInfo userInfo;
        if(artist.length()==0){
            userInfo = new UserInfo(name, phone, location);
        }
        else{
            userInfo = new UserInfo(name, phone, location, artist);
        }
        databaseReference.child(user.getUid()).setValue(userInfo);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, StartActivity.class));
                break;
            case R.id.save:
                saveUserInfo();
                break;
            case R.id.toFindEvents:
                startActivity(new Intent(this, Events.class));
                break;
            case R.id.toChat:
                startActivity(new Intent(this, Chat.class));
                break;
        }

    }
}
