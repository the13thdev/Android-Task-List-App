package me.vijitdhingra.tasklist;

import android.content.Context;
import android.text.format.DateFormat;

import java.util.Calendar;

public final class EventHelper {


    /**
     * Returns a string representing the time in format 'HH:MM' or 'HH:MM AM/PM' depending upon whether the activity is 24-hour or not.
     * @param eventTime
     * @return
     */
    public static String getEventTimeDisplayString(Calendar eventTime,Context context)
    {
        String timeDisplayString="";
        int hour=eventTime.get(Calendar.HOUR_OF_DAY);
        int minute=eventTime.get(Calendar.MINUTE);
        String minText,hrText;
        if(minute<10)
        {
            minText="0"+minute;
        }
        else
        {
            minText=""+minute;
        }
        if(DateFormat.is24HourFormat(context))
        {
            if(hour<10)
            {
                hrText="0"+hour;
            }
            else
            {
                hrText=""+hour;
            }
            timeDisplayString+=hrText+":"+minText;
        }
        else
        {
            String am_pm="";
            if(eventTime.get(Calendar.AM_PM)==Calendar.AM)
            {
                am_pm="AM";
            }
            else if(eventTime.get(Calendar.AM_PM)==Calendar.PM)
            {
                am_pm="PM";
            }
            hour=eventTime.get(Calendar.HOUR);
            if(hour==0)
            {
                hrText="12";
            }
            else if(hour<10)
            {
                hrText="0"+hour;
            }
            else
            {
                hrText=""+hour;
            }
            timeDisplayString+=hrText+":"+minText+" "+am_pm;
        }
        return timeDisplayString;
    }

    /**
     * Retuns a string representing the Date in the following format 'Day Month_Name Year'
     * @param eventDate
     * @return
     */
    public static String getEventDateDisplayString(Calendar eventDate)
    {

        int day,month,year;
        day = eventDate.get(Calendar.DAY_OF_MONTH);
        month = eventDate.get(Calendar.MONTH);
        year = eventDate.get(Calendar.YEAR);
        String dayText="",monthName="";
        switch(month)
        {
            case 0:
                monthName="Jan";
                break;
            case 1:
                monthName="Feb";
                break;
            case 2:
                monthName="Mar";
                break;
            case 3:
                monthName="Apr";
                break;
            case 4:
                monthName="May";
                break;
            case 5:
                monthName="Jun";
                break;
            case 6:
                monthName="Jul";
                break;
            case 7:
                monthName="Aug";
                break;
            case 8:
                monthName="Sep";
                break;
            case 9:
                monthName="Oct";
                break;
            case 10:
                monthName="Nov";
                break;
            case 11:
                monthName="Dec";
                break;
        }
        if(day<10)
        {
            dayText="0"+day;
        }
        else
        {
            dayText=""+day;
        }
        return (dayText+" "+monthName+" "+year);
    }

    public static boolean compareToDoDateTimeEqual(Calendar eventDateTiime1, Calendar eventDateTime2)
    {
        if(eventDateTiime1.get(Calendar.YEAR)!=eventDateTime2.get(Calendar.YEAR))
        {
            return false;
        }
        else if(eventDateTiime1.get(Calendar.MONTH)!=eventDateTime2.get(Calendar.MONTH))
        {
            return  false;
        }
        else if(eventDateTiime1.get(Calendar.DAY_OF_MONTH)!=eventDateTime2.get(Calendar.DAY_OF_MONTH))
        {
            return  false;
        }
        else if(eventDateTiime1.get(Calendar.HOUR)!=eventDateTime2.get(Calendar.HOUR))
        {
            return  false;
        }
        else if(eventDateTiime1.get(Calendar.MINUTE)!=eventDateTime2.get(Calendar.MINUTE))
        {
            return  false;
        }
        else
        {
            return  true;
        }
    }

}
