package com.example.tuchatrcsmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeScreen extends AppCompatActivity {
    Button signInButton;
    TextView disclaimerTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        //Create action listener for sign in button
        signInButton = findViewById(R.id.get_started);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call sign-in activity
                Intent callSignInActivity = new Intent(WelcomeScreen.this, SignInActivity.class);
                startActivity(callSignInActivity);

                //Animate transition into called activity
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //The code below makes the links on the Privacy policy and TOS sections of the disclaimer clickable links
        disclaimerTextview = findViewById(R.id.disclaimer_line);
        disclaimerTextview.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
