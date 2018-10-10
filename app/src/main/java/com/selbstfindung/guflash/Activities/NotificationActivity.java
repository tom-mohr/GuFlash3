package com.selbstfindung.guflash.Activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.selbstfindung.guflash.R;

public class NotificationActivity extends AppCompatActivity
{
    private static final String TAG = "MONTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Einstellungen werden erstellt");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init()
    {
        setTitle("Einstellungen");

        Button useless = (Button) findViewById(R.id.useless_button);

        useless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NotificationActivity.this, "Useless", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
