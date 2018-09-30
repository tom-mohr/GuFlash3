package com.selbstfindung.guflash.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.selbstfindung.guflash.R;

public class DatePickingActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    private static final int ÜBERGANG_ZUM_TIME_PICKER = 3;

    Button confirmDate;
    DatePicker chosenDate;

    String result;
    String finalResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picking);

        setTitle("Datum wählen");

        confirmDate = (Button) findViewById(R.id.confirm_date);
        chosenDate = (DatePicker) findViewById(R.id.pick_date);

        confirmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = chosenDate.getDayOfMonth()+"."+chosenDate.getMonth()+"."+chosenDate.getYear();
                Log.d(TAG, result);

                startActivityForResult(new Intent(DatePickingActivity.this, TimePickingActivity.class),ÜBERGANG_ZUM_TIME_PICKER);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode ==ÜBERGANG_ZUM_TIME_PICKER)
        {
            if(resultCode == RESULT_OK)
            {
                finalResult = result+" "+data.getStringExtra("result");

                Log.d(TAG, finalResult);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", finalResult);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        }
    }
}
