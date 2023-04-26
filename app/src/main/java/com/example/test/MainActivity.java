package com.example.test;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent foregroundServiceIntent = new Intent(this, MyForegroundService.class);
        ContextCompat.startForegroundService(this, foregroundServiceIntent);
    }
}