package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button buttonInfo = findViewById(R.id.info);
        Button buttonStartStop = findViewById(R.id.start);

        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "info", Toast.LENGTH_SHORT).show();
                buttonInfo.setEnabled(false);
            }
        });

        buttonStartStop.setOnClickListener(new View.OnClickListener() {
            boolean isServiceRunning = false;

            @Override
            public void onClick(View v) {

                if (isServiceRunning) {
                    // STOP foregroundService
                    Toast.makeText(HomeActivity.this, "stop - "+isServiceRunning, Toast.LENGTH_SHORT).show();
                    stopService(new Intent(HomeActivity.this, MyForegroundService.class));
                    buttonStartStop.setText("START");
                } else {
                    // START foregroundService
                    Toast.makeText(HomeActivity.this, "start - "+isServiceRunning, Toast.LENGTH_SHORT).show();
                    Intent foregroundServiceIntent = new Intent(HomeActivity.this, MyForegroundService.class);
                    ContextCompat.startForegroundService(HomeActivity.this, foregroundServiceIntent);
                    buttonStartStop.setText("STOP");
                }
                isServiceRunning = !isServiceRunning;
            }
        });
    }
}


