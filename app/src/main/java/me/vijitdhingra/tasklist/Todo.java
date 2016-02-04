package me.vijitdhingra.tasklist;

import java.util.Calendar;

/**
 * Created by vaio on 10-09-2015.
 * Always use getIdCounter  function of DataManager to assign ID to Todo.
 */
public class Todo {

    //Class Variables
    private int Id;
    private String Title;
    private String Description;
    private Calendar CreationDateTime;

    //Constructor
    public Todo(int id)
    {
        this.Id=id;
    }

    public Todo(int id, String title, String description, Calendar creationDateTime)
    {
        this.Id=id;
        this.Title=title;
        this.Description=description;
        this.CreationDateTime=creationDateTime;
    }

    //Setters
    public void setCreationDateTime(Calendar dateTime)
    {
        this.CreationDateTime=dateTime;
    }
    public void setTitle(String title)
    {
        this.Title=title;
    }
    public  void setDescription(String description)
    {
        this.Description=description;
    }

    //Getters
    public Calendar getCreationDateTime()
    {
        return this.CreationDateTime;
    }
    public String getDescription()
    {
        return this.Description;
    }
    public String getTitle()
    {
        return this.Title;
    }
    public int getId()
    {
        return  this.Id;
    }
}
