package com.vnr.smartcitytraveller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Parent extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        SharedPreferences sp = getSharedPreferences(Util.preferences, Context.MODE_PRIVATE);
        String username = sp.getString(Util.username,null);
        if(username==null)
        {
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
            finish();
        }
        else
        {
            Intent i = new Intent(this,PlaceFinder.class);
            startActivity(i);
            finish();
        }

    }

}
