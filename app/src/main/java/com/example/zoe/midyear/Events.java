package com.example.zoe.midyear;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class Events extends AppCompatActivity implements View.OnClickListener {

    private EventInfo e;
    private UserInfo u;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    private TextView event;
    private EditText num;
    private Button toProfile, toChat, findEvent, toSongkick, send;

    //private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;


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

        send = (Button) findViewById(R.id.buttonSend);
        send.setOnClickListener(this);

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
                Intent intent = new Intent(getApplicationContext(),GroupChatActivity.class);
                intent.putExtra("name",(e.getDisplayName()));
                startActivity(intent);
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
        try{
            apiRequest = new APIRequest(u.location, u.artist, getApplicationContext());
        }catch(NullPointerException n){
            apiRequest = new APIRequest(u.location, getApplicationContext());
        }
        new JSONTask().execute(apiRequest.APIRequestURL());
    }

    public void sendSMS(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Events.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);}
        String message = u.name + " though you might be interested in going to "+e.getDisplayName()+" with them.  Here is a link for more information! "+e.getLink();
        String phoneNumber = num.getText().toString();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Sending SMS failed.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /*public void newGroupChat() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(e.getDisplayName(), "");

        root.updateChildren(map);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()) {
                    set.add(((DataSnapshot) i.next()).getKey());
                }

                GroupChatActivity.getListOfRooms().clear();
                GroupChatActivity.getListOfRooms().addAll(set);

                //GroupChatActivity.getArrayAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/


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
            return getString(R.string.defaultJson);
        }

        @Override
        protected void onPostExecute(String result) {
                String def = "{\"resultsPage\":{\"page\":1,\"totalEntries\":2,\"perPage\":50,\"results\":{\"event\":[{\"displayName\":\"VampireWeekendatO2AcademyBrixton(February16,2010)\",\"type\":\"Concert\",\"uri\":\"http://www.songkick.com/concerts/3037536-vampire-weekend-at-o2-academy-brixton?utm_medium=partner&amp;utm_source=PARTNER_ID\",\"venue\":{\"lng\":-0.1187418,\"displayName\":\"O2AcademyBrixton\",\"lat\":51.4681089,\"id\":17522},\"location\":{\"lng\":-0.1187418,\"city\":\"London,UK\",\"lat\":51.4681089},\"start\":{\"time\":\"19:30:00\",\"date\":\"2010-02-16\",\"datetime\":\"2010-02-16T19:30:00+0000\"},\"performance\":[{\"artist\":{\"uri\":\"http://www.songkick.com/artists/288696-vampire-weekend\",\"displayName\":\"VampireWeekend\",\"id\":288696,\"identifier\":[{\"mbid\":\"af37c51c-0790-4a29-b995-456f98a6b8c9\"}]},\"displayName\":\"VampireWeekend\",\"billingIndex\":1,\"id\":5380281,\"billing\":\"headline\"}],\"id\":3037536},{\"displayName\":\"VampireWeekendatO2AcademyBrixton(February17,2010)\",\"type\":\"Concert\",\"uri\":\"http://www.songkick.com/concerts/3078766-vampire-weekend-at-o2-academy-brixton?utm_medium=partner&amp;utm_source=PARTNER_ID\",\"venue\":{\"lng\":-0.1187418,\"displayName\":\"O2AcademyBrixton\",\"lat\":51.4681089,\"id\":17522},\"location\":{\"lng\":-0.1187418,\"city\":\"London,UK\",\"lat\":51.4681089},\"start\":{\"time\":\"19:30:00\",\"date\":\"2010-02-17\",\"datetime\":\"2010-02-17T19:30:00+0000\"},\"performance\":[{\"artist\":{\"uri\":\"http://www.songkick.com/artists/288696-vampire-weekend\",\"displayName\":\"VampireWeekend\",\"id\":288696,\"identifier\":[{\"mbid\":\"af37c51c-0790-4a29-b995-456f98a6b8c9\"}]},\"displayName\":\"VampireWeekend\",\"billingIndex\":1,\"id\":5468321,\"billing\":\"headline\"}],\"id\":3078766}]}}}";
                e = new EventInfo(def);
                event.setText(e.getDisplayName());
                //newGroupChat();
        }
    }
}
