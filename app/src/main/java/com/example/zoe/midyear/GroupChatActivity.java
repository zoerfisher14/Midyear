package com.example.zoe.midyear;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupChatActivity extends AppCompatActivity {

    public static ArrayList<String> listOfRooms = new ArrayList();
    public static ArrayAdapter<String> arrayAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        listView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfRooms);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(),Chat.class);
                intent.putExtra("name",((TextView)view).getText().toString() );
                startActivity(intent);
            }
        });
    }

    public static ArrayList<String> getListOfRooms(){
        return listOfRooms;
    }

    public static ArrayAdapter<String> getArrayAdapter(){
        return arrayAdapter;
    }
}
