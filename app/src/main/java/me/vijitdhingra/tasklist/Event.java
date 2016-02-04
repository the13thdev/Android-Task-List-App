package me.vijitdhingra.tasklist;

import java.util.Calendar;

/**
 * Created by vaio on 10-09-2015.
 * Always use getIdCounter  function of DataManager to assign ID to events.
 */
public class Event extends Todo {

    //Class Variables
    private Calendar EventDateTime;

    //constructor
    public Event(int id, String title, String description,Calendar eventDateTime, Calendar creationDateTime)
    {
        super(id,title,description,creationDateTime);
        this.EventDateTime=eventDateTime;
    }

    //Setters
    public void setEventDateTime(Calendar dateTime)
    {
        this.EventDateTime=dateTime;
    }

    //Getters
    public Calendar getEventDateTime()
    {
        return this.EventDateTime;
    }

}
