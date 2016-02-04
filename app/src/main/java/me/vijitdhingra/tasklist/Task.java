package me.vijitdhingra.tasklist;

import java.util.Calendar;

/**
 * Created by vaio on 10-09-2015.
 * Always use getIdCounter  function of DataManager to assign ID to tasks.
 */
public class Task extends Todo{

    //Class Variables
    private Priority Priority;

    //constructor
    public Task(int id, String title, String description, Priority priority, Calendar creationDateTime)
    {
        super(id,title,description,creationDateTime);
        this.Priority=priority;
    }

    //Setters
    public void setPriority(Priority priority)
    {
        this.Priority=priority;
    }

    //Getters
    public Priority getPriority()
    {
        return this.Priority;
    }

}
