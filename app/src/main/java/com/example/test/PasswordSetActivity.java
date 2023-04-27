package com.example.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordSetActivity extends AppCompatActivity {

    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_set);

        passwordEditText = findViewById(R.id.password_edit_text);

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString().trim();

                if (password.isEmpty()) {
//                    Toast.makeText(PasswordSetActivity.this, R.string.password_empty_error, Toast.LENGTH_SHORT).show();
                } else {
                    // Save password to preferences or database
                    // Navigate to next activity
                }
            }
        });
    }
}

