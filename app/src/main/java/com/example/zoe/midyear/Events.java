package com.example.zoe.midyear;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Events extends AppCompatActivity implements View.OnClickListener {

    private EventInfo e;
    private UserInfo u;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    private TextView event;
    private EditText num;
    private Button toProfile, toChat, findEvent, toSongkick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, StartActivity.class));
        }

        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        toProfile = (Button) findViewById(R.id.toProfile1);
        toProfile.setOnClickListener(this);

        toChat = (Button) findViewById(R.id.toChat1);
        toChat.setOnClickListener(this);

        toSongkick = (Button) findViewById(R.id.toSongkick);
        toSongkick.setOnClickListener(this);

        findEvent = (Button) findViewById(R.id.newEvent);
        findEvent.setOnClickListener(this);

        event = (TextView) findViewById(R.id.textViewEvent);

        num = (EditText) findViewById(R.id.editText);
    }

    @Override
    protected void onStart() {
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
        switch (view.getId()) {
            case R.id.toProfile1:
                startActivity(new Intent(this, Profile.class));
                break;
            case R.id.toChat1:
                startActivity(new Intent(this, Chat.class));
                break;
            case R.id.toSongkick:
                try{
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(e.getLink()));
                startActivity(browserIntent);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Couldn't access songkick website", Toast.LENGTH_SHORT);
                }
                break;
            case R.id.newEvent:
                getNewEvent();
                break;
            case R.id.buttonSend:
                sendSMS();
                break;
        }
    }

    public void getNewEvent() {
        APIRequest apiRequest;
        if (u.artist == null) {
            apiRequest = new APIRequest(u.location, getApplicationContext());
        } else {
            apiRequest = new APIRequest(u.location, u.artist, getApplicationContext());
        }

        new JSONTask().execute(apiRequest.APIRequestURL());

    }

    public void sendSMS(){
        String message = u.name + " though you might be interested in going to "+e.getDisplayName()+" with them.  Here is a link for more information! "+e.getLink();
        String phoneNumber = num.getText().toString();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Sending SMS failed.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }


    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                return finalJson;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                e = new EventInfo(result);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            event.setText(e.getDisplayName());
        }
    }
}
