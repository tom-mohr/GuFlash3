package com.selbstfindung.guflash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UsernameActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_TAG = "USERNAME_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        final EditText usernameEingabefeld = (EditText) findViewById(R.id.edittext_username);
        Button okButton = (Button) findViewById(R.id.username_confirm_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String username = usernameEingabefeld.getText().toString();

                Intent intent = new Intent(UsernameActivity.this, BoringActivity.class);

                intent.putExtra(EXTRA_MESSAGE_TAG, username);

                startActivity(intent);

            }
        });
    }
}
