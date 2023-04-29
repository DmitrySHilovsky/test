package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private PasswordManager passwordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passwordManager = new PasswordManager(this);

        Button submitButton = findViewById(R.id.submit_button);
        EditText passwordEditText = findViewById(R.id.password_edit_text);

        if (!passwordManager.isPasswordSet()) {
            Intent intent = new Intent(MainActivity.this, PasswordSetActivity.class);
            startActivity(intent);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                if (!passwordManager.isPasswordSet()) {
                    Intent intent = new Intent(MainActivity.this, PasswordSetActivity.class);
                    startActivity(intent);
                } else if (passwordManager.verifyPassword(password)) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoadingPage.class);
                    startActivity(intent);
                }
            }
        });
    }
}
