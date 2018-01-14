package com.example.zoe.midyear;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{

    Button login, register;
    FirebaseUser u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        login = findViewById(R.id.login);
        login.setOnClickListener(this);

        register = findViewById(R.id.register);
        register.setOnClickListener(this);

        u = FirebaseAuth.getInstance().getCurrentUser();

        if(u!=null){
            Intent i = new Intent(this, Profile.class);
            startActivity(i);
        }


    }

    @Override
    public void onClick(View v){
        if(v == login){
            Intent i = new Intent(this, Login.class);
            startActivity(i);
        }
        if(v == register){
            Intent i = new Intent(this, Register.class);
            startActivity(i);
        }
    }
}
