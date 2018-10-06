package com.selbstfindung.guflash;

import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
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
        
        id = ds.getKey();
        name = ds.child("name").getValue(String.class);
    
        Log.d(TAG, "EventInfo: " + name + " has " + ds.getChildrenCount() + " children");
        
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
    
    public long getMillisTillEvent() {
        // returns time from now to event in milliseconds
        
        Calendar now = Calendar.getInstance();
        Calendar eventTime = getEventTime();
        
        return eventTime.getTime().getTime() - now.getTime().getTime();
    }
    
    public Calendar getEventTime() {
        Calendar eventTime = new GregorianCalendar();
        eventTime.set(timeYear, timeMonth, timeDay, timeHour, timeMinute);
        return eventTime;
    }
    
    public double getHoursTillEvent() {
        return getMillisTillEvent() / (1000 * 60 * 60);// so viele Millisekunden hat eine Stunde
    }
    
    public double getDaysTillEvent() {
        return getMillisTillEvent() / (1000 * 60 * 60 * 24);// so viele Millisekunden hat ein Tag
    }
    
    public double getWeeksTillEvent() {
        return getMillisTillEvent() / (1000 * 60 * 60 * 24 * 7);// so viele Millisekunden hat eine Woche
    }
    
    public String getDeltaTimeInfoString() {
        if (getMillisTillEvent() < 0) {
            
            return "vorbei";
            
        } else {
            long weeks = Math.round(getWeeksTillEvent());
            if (weeks > 2) {
                
                return "in ca. " + String.valueOf(weeks) + " Wochen";
            
            } else {
                
                int days = (int) Math.floor(getDaysTillEvent());
                if (days > 0) {
                
                    return "in " + String.valueOf(days) + " Tagen";
                
                } else {
    
                    int hours = (int) Math.floor(getHoursTillEvent());
                    return "in " + String.valueOf(hours) + " Stunden";
                }
            }
        }
    }

    public boolean containsUserID(String userID)
    {
        for(String ID: userIds)
        {
            if(ID.equals(userID))
            {
                return true;
            }
        }
        return false;
    }
    
    public String getMembersInfoString() {
        //Unterschiedlicher Output je nach Teilnehmerzahl
        int diff = maxMembers - userIds.size();
        int diff2 = minMembers - userIds.size();
        String nMembersInfoString;
        if(diff2>0) {
            nMembersInfoString = "Noch " + diff2 + " Teilnehmer benötigt bis zur Eröffnung des Chats";
        } else {
            if(diff == 1)
            {
                nMembersInfoString = "Nur noch " + diff + " Platz frei";
            }
            else if (diff > 3)
                nMembersInfoString = "Noch " + diff + " Plätze frei";
            else if(diff > 0)
            {
                nMembersInfoString = "Nur noch " + diff + " Plätze frei";
            }
            else
                nMembersInfoString = "alle " + maxMembers + " Plätze belegt";
        }
        return nMembersInfoString;
    }
    
    public static int findIndexWithSameId(List<EventInfo> list, EventInfo eventInfo) {
        // returns -1 if no eventInfo with same ID was found in the given list
        int index = 0;
        for (EventInfo otherEventInfo : list) {
            if (eventInfo.id.equals(otherEventInfo.id)) {
                return index;
            }
            index++;
        }
        return -1;
    }
}
