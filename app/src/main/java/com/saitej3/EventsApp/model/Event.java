package com.saitej3.EventsApp.model;

/**
 * Created by Sai Teja on 10/22/2015.
 */
public class Event {

    int id;
    String eventName;
    String eventTime;
    String eventDesc;
    String imagePath;

    public Event()
    {

    }

    public Event(int id, String eventName, String eventTime, String eventDesc, String imagePath)
    {
        this.id=id;
        this.eventName=eventName;
        this.eventTime=eventTime;
        this.eventDesc=eventDesc;
        this.imagePath=imagePath;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int num)
    {
        this.id=num;
    }

    public String getEventName()
    {
        return eventName;
    }

    public void setEventName(String name)
    {
        this.eventName=name;
    }
    public String getEventTime()
    {
        return eventTime;
    }

    public void setEventTime(String time)
    {
        this.eventTime=time;
    }
    public String getEventDesc()
    {
        return eventDesc;
    }

    public void setEventDesc(String name)
    {
        this.eventDesc=name;
    }

    public String getImagePath(){return imagePath;}

    public void setImagePath(String name)
    {
        this.imagePath=name;
    }

}
