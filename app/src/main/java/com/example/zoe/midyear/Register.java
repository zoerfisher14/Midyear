package com.example.zoe.midyear;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity implements View.OnClickListener{

    private EditText getEmail, getPassword;
    private String email, password;
    private Button register;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        getEmail = findViewById(R.id.email);
        getPassword = findViewById(R.id.password);

        register = findViewById(R.id.registerForReal);
        register.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v){
        email = getEmail.getText().toString();
        password = getPassword.getText().toString();

        if(email.length()>0&&password.length()>0){

            progressDialog.setMessage("Registering...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        progressDialog.setMessage("Logging in...");

                        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task1) {
                                if(task1.isSuccessful()){
                                    startActivity(new Intent(getApplicationContext(), Profile.class));
                                }
                            }
                        });

                        finish();

                        Intent i = new Intent(Register.this, Profile.class);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(Register.this, "Please try again", Toast.LENGTH_SHORT).show();
                    }

                    progressDialog.dismiss();

                }
            });
        }
        else{
            Toast.makeText(this, "Email and password must be entered", Toast.LENGTH_SHORT).show();
        }
    }
}
