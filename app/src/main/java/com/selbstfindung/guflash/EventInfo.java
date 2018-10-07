package com.selbstfindung.guflash;

import android.location.Location;
import android.provider.ContactsContract;
import android.text.Html;
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
    public double placeLat,
            placeLng;
    public int timeYear,
            timeMonth,
            timeDay,
            timeHour,
            timeMinute,
            minMembers,
            maxMembers;
    ArrayList<String> userIds;
    
    // runtime variables:
    public float distance = -1;// in meters
    
    public EventInfo(DataSnapshot ds) throws NullPointerException, DatabaseException {
        
        id = ds.getKey();
        name = ds.child("name").getValue(String.class);
        
        description = ds.child("description").getValue(String.class);
        placeName = ds.child("place").child("name").getValue(String.class);
        placeAddress = ds.child("place").child("address").getValue(String.class);
        placeLat = ds.child("place").child("latitude").getValue(Double.class);
        placeLng = ds.child("place").child("longitude").getValue(Double.class);
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
                
                return timeDay + "." + timeMonth + "." + timeYear;
            
            } else {
                
                int days = (int) Math.floor(getDaysTillEvent());
                if (days > 1) {
                
                    return "in " + String.valueOf(days) + " Tagen";
                
                } else {
    
                    int hours = (int) Math.floor(getHoursTillEvent());
                    if(hours<1)
                    {
                        return "weniger als 1 Stunde";
                    }
                    if(hours==1)
                    {
                        return "nur noch 1 Stunde";
                    }

                    return "in " + String.valueOf(hours) + " Stunden";
                }
            }
        }
    }

    public boolean containsUserId(String userId) {
        for(String ID: userIds) {
            if(ID.equals(userId)) {
                return true;
            }
        }
        return false;
    }
    
    public String getMembersInfoString() {
        // Unterschiedlicher Output je nach Teilnehmerzahl
        /*
        - immer: wie viele sind schon?
        - ist das event schon gestartet?
        -> wenn nein: wie viele braucht es noch? | "braucht noch ..."
        -> wenn ja: wie viele dürfen noch | "noch 3 frei"
         */
        int n = userIds.size();
        String start = n + "  (";
        String end = ")";
        if (n < minMembers) {
            // Event noch nicht geöffnet
            int diff = minMembers - n;// wie viele braucht es noch bis zur mindestanzahl?
            return start + "braucht noch " + diff + end;
        } else if (n < maxMembers) {
            // Event schon eröffnet und noch Plätze frei
            int diff = maxMembers - n;// wie viele sind noch frei bis zur höchstanzahl?
            return start + "noch " + diff + " frei" + end;
        } else {
            // Event schon voll
            return start + "voll" + end;
        }
    }
    
    public void calcDistanceTo(Location location) {
        Location eventLocation = new Location("dummyprovider");
        eventLocation.setLatitude(placeLat);
        eventLocation.setLongitude(placeLng);
        
        distance = location.distanceTo(eventLocation);// in meters
    }
    
    public String getDistanceInfoString() {
        
        if (distance < 0) {
            
            return "";
            
        } else if (distance < 1000) {
            // Meter auf 10 Meter gerunded anzeigen
            int metersRounded = Math.round(distance / 10) * 10;
            
            return String.valueOf(metersRounded) + " Meter";
            
        } else {
    
            double kilometers = distance / 1000;
            
            if (kilometers < 10) {
                // auf 1 Stelle runden
                double kilometersRounded = ((double) Math.round(kilometers * 10)) / 10;
                return String.valueOf(kilometersRounded) + " km";
                
            } else {
                // auf ganze Zahl runden
                long kilometersRounded = Math.round(kilometers);
                return String.valueOf(kilometersRounded) + " km";
            }
        }
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
