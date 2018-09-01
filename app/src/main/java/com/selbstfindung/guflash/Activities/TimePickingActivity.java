package com.selbstfindung.guflash.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.selbstfindung.guflash.R;

public class TimePickingActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picking);
        
        setTitle("Zeit wählen");
    }
}
