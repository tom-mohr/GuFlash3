package com.selbstfindung.guflash.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.selbstfindung.guflash.R;

public class TimePickingActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";
    public static final String EXTRA_MESSAGE_DATE = "DATE";

    Button confirmTime;
    TimePicker chosenTime;

    String time;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picking);
        
        setTitle("Zeit wählen");

        confirmTime = (Button) findViewById(R.id.confirm_time);
        chosenTime = (TimePicker) findViewById(R.id.pick_time);

        confirmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                time = chosenTime.getCurrentHour()+":"+chosenTime.getCurrentMinute();

                Log.d(TAG, "Zeit wird übergeben " +time);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", time);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}
