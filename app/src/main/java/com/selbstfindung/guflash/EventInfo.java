package com.selbstfindung.guflash;

import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class EventInfo {
    
    private static final String TAG = "MONTAG";
    
    public String id,
            name,
            description,
            placeName,
            placeAddress;
    public int placeLat,
            placeLng,
            timeYear,
            timeMonth,
            timeDay,
            timeHour,
            timeMinute,
            minMembers,
            maxMembers;
    ArrayList<String> userIds;
    
    public EventInfo(DataSnapshot ds) throws NullPointerException, DatabaseException {
        Log.d(TAG, "EventInfo: children count: "+ds.getChildrenCount());
        id = ds.getKey();
        name = ds.child("name").getValue(String.class);
        description = ds.child("description").getValue(String.class);
        placeName = ds.child("place").child("name").getValue(String.class);
        placeAddress = ds.child("place").child("address").getValue(String.class);
        placeLat = ds.child("place").child("latitude").getValue(Integer.class);
        placeLng = ds.child("place").child("longitude").getValue(Integer.class);
        timeYear = ds.child("time").child("year").getValue(Integer.class);
        timeMonth = ds.child("time").child("month").getValue(Integer.class);
        timeDay = ds.child("time").child("day").getValue(Integer.class);
        timeHour = ds.child("time").child("hour").getValue(Integer.class);
        timeMinute = ds.child("time").child("minute").getValue(Integer.class);
        minMembers = ds.child("min_members").getValue(Integer.class);
        maxMembers = ds.child("max_members").getValue(Integer.class);
        
        userIds = new ArrayList<>();
        
        for (DataSnapshot userChild: ds.child("users").getChildren()) {
            
            userIds.add(userChild.getValue(String.class));
        }
    }
    
    
    // Utility-Methoden:
    
    public int getMillisTillEvent() {
        // returns time from now to event in milliseconds
        
        Calendar now = Calendar.getInstance();
        Calendar eventTime = new GregorianCalendar();
        eventTime.set(timeYear, timeMonth, timeDay, timeHour, timeMinute);
        
        return eventTime.compareTo(now);// returns time from now to event in milliseconds
    }
    
    public int getHoursTillEvent() {
        return getMillisTillEvent() / (1000 * 60 * 60);// so viele millisekunden hat eine stunde
    }
}
