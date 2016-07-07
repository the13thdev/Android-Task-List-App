package me.vijitdhingra.tasklist;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by vaio on 11-09-2015.
 */
public class DataManager {

    //Static Constants
    public enum SORT_ORDER{
        TASK_PRIORITY_ASC,
        TASK_PRIORITY_DSC,
        EVENT_DATETIME_ASC,
        EVENT_DATETIME_DSC
    }
    public final static String EXCEPTIONTAG_TASKLIST_DATAMANAGER="TASKLIST_DATAMANAGER";
    //XML Element Names
    public final static String XMLTEXT_TASKLIST="TASKLIST";
    //XML Element Names (Settings)
    public final static String XMLTEXT_SETTINGS="SETTINGS";
    public final static String XMLTEXT_IDCOUNTER="IDCOUNTER";
    public final static String XMLTEXT_TASKS_SORT_ORDER="TASKS_SORT_ORDER";
    public final static String XMLTEXT_EVENTS_SORT_ORDER="EVENTS_SORT_ORDER";
    public final static String XMLTEXT_SORT_ORDER_TASK_PRIORITY_ASC="SORT_ORDER_TASK_PRIORITY_ASC";
    public final static String XMLTEXT_SORT_ORDER_TASK_PRIORITY_DSC="SORT_ORDER_TASK_PRIORITY_DSC";
    public final static String XMLTEXT_SORT_ORDER_EVENT_DATETIME_ASC="SORT_ORDER_EVENT_DATETIME_ASC";
    public final static String XMLTEXT_SORT_ORDER_EVENT_DATETIME_DSC="SORT_ORDER_EVENT_DATETIME_DSC";

    //XML File Attributes Names (Task and Event)
    public final static String XMLTEXT_ID="ID";
    //XML Element Names (Task and Event)
    public final static String XMLTEXT_TASK="TASK";
    public final static String XMLTEXT_EVENT="EVENT";
    public final static String XMLTEXT_TITLE="TITLE";
    public final static String XMLTEXT_DESCRIPTION="DESCRIPTION";
    public final static String XMLTEXT_CREATION_DATETIME="CREATION_DATETIME";
    public final static String XMLTEXT_DATE="DATE";
    public final static String XMLTEXT_TIME="TIME";
    public final static String XMLTEXT_YEAR="YEAR";
    public final static String XMLTEXT_MONTH="MONTH";
    public final static String XMLTEXT_DAY="DAY";
    public final static String XMLTEXT_HOUR_OF_DAY="HOUR_OF_DAY";
    public final static String XMLTEXT_MINUTES="MINUTES";
    public final static String XMLTEXT_SECONDS="SECONDS";
    //XML Element Names (Task)
    public final static String XMLTEXT_PRIORITY="PRIORITY";
    public final static String XMLTEXT_PRIORITY_URGENT="URGENT";
    public final static String XMLTEXT_PRIORITY_HIGH="HIGH";
    public final static String XMLTEXT_PRIORITY_MEDIUM="MEDIUM";
    public final static String XMLTEXT_PRIORITY_LOW="LOW";
    //XML Element Names (Event)
    public final static String XMLTEXT_EVENT_DATETIME="EVENT_DATETIME";
    //Activity Intent Store Extra Names
    public final static String INTENT_EXTRA_TAB_POSITION="TAB_POSITION";

    //Private Variables
    private final static String FILENAME="datalist.xml";
    private File dataFile;
    private Context context;
    private int idCounter;
    private SORT_ORDER tasksSortOrder,eventsSortOrder;
    private ArrayList<Task> listTasks;
    private ArrayList<Event> listEvents;

    //Constructor
    public DataManager(Context context)
    {
        this.context=context;
        listTasks = new ArrayList<>();
        listEvents = new ArrayList<>();
        initializeDataFile();
        getData();
    }

    //Functions

    /**
     * Instantiates dataFile and idCounter
     * If the file doesn't already exist, then creates a xml file with root element'TASKLIST' as shown:  <TASKLIST> <SETTINGS><IDCOUNTER>0</IDCOUNTER></SETTINGS></TASKLIST>
     */
    private void initializeDataFile()
    {
        try {
            dataFile = new File(context.getFilesDir(), FILENAME);
            if (!dataFile.exists()) {
                idCounter=1;
                tasksSortOrder= SORT_ORDER.TASK_PRIORITY_DSC;
                eventsSortOrder = SORT_ORDER.EVENT_DATETIME_DSC;
                dataFile.createNewFile();
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                Element rootElement = document.createElement(XMLTEXT_TASKLIST);
                document.appendChild(rootElement);
                //Adding Settings element in rootElement
                Element settingsElement = document.createElement(XMLTEXT_SETTINGS);
                rootElement.appendChild(settingsElement);
                //Adding idCounter element in settingsElement
                Element idCounterElement = document.createElement(XMLTEXT_IDCOUNTER);
                settingsElement.appendChild(idCounterElement);
                idCounterElement.appendChild(document.createTextNode(String.valueOf(idCounter)));
                //Adding tasksSortOrder element in settings element
                Element tasksSortOrderElement = document.createElement(XMLTEXT_TASKS_SORT_ORDER);
                settingsElement.appendChild(tasksSortOrderElement);
                tasksSortOrderElement.appendChild(document.createTextNode(getStringFromSortOrder(tasksSortOrder)));
                //Adding eventsSortOrder element in settings element
                Element eventsSortOrderElement = document.createElement(XMLTEXT_EVENTS_SORT_ORDER);
                settingsElement.appendChild(eventsSortOrderElement);
                eventsSortOrderElement.appendChild(document.createTextNode(getStringFromSortOrder(eventsSortOrder)));
                TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(dataFile));
            }
        }
        catch(Exception e)
        {
            Log.e(EXCEPTIONTAG_TASKLIST_DATAMANAGER, "createDataFile :"+e.getMessage());
        }
    }

    private String getStringFromSortOrder(SORT_ORDER sort_order)
    {
        String sortOrderString="";
        switch (sort_order)
        {
            case TASK_PRIORITY_ASC:
                sortOrderString =  XMLTEXT_SORT_ORDER_TASK_PRIORITY_ASC;
                break;
            case TASK_PRIORITY_DSC:
                sortOrderString = XMLTEXT_SORT_ORDER_TASK_PRIORITY_DSC;
                break;
            case EVENT_DATETIME_ASC:
                sortOrderString = XMLTEXT_SORT_ORDER_EVENT_DATETIME_ASC;
                break;
            case EVENT_DATETIME_DSC:
                sortOrderString = XMLTEXT_SORT_ORDER_EVENT_DATETIME_DSC;
        }
        return sortOrderString;
    }

    private SORT_ORDER getSortOrderFromString(String sortOrderString)
    {
        if(sortOrderString.equals(XMLTEXT_SORT_ORDER_TASK_PRIORITY_ASC))
            return SORT_ORDER.TASK_PRIORITY_ASC;
        else if(sortOrderString.equals(XMLTEXT_SORT_ORDER_TASK_PRIORITY_DSC))
            return SORT_ORDER.TASK_PRIORITY_DSC;
        else if(sortOrderString.equals(XMLTEXT_SORT_ORDER_EVENT_DATETIME_ASC))
            return SORT_ORDER.EVENT_DATETIME_ASC;
        else
            return SORT_ORDER.EVENT_DATETIME_DSC;
    }

    /**
     * Populates listTasks and listEvents by extracting Tasks and Events to create their respective ArrayLists from the xml data file.
     */
    private void getData()
    {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dataFile);
            Element rootElement = document.getDocumentElement();
            rootElement.normalize();
            //Initializing Settings
            Element settingsElement = (Element) rootElement.getElementsByTagName(XMLTEXT_SETTINGS).item(0);
            //Initializing idCounter
            Element idCounterElement = (Element) settingsElement.getElementsByTagName(XMLTEXT_IDCOUNTER).item(0);
            idCounter = Integer.valueOf(idCounterElement.getTextContent());
            //Initializing tasksSortOrder
            Element tasksSortOrderElement = (Element) settingsElement.getElementsByTagName(XMLTEXT_TASKS_SORT_ORDER).item(0);
            tasksSortOrder = getSortOrderFromString(tasksSortOrderElement.getTextContent());
            Element eventsSortOrderElement = (Element) settingsElement.getElementsByTagName(XMLTEXT_EVENTS_SORT_ORDER).item(0);
            eventsSortOrder = getSortOrderFromString(eventsSortOrderElement.getTextContent());
            //Initializing eventsSortOrder
            //Populating listTasks
            NodeList nodeList = rootElement.getElementsByTagName(XMLTEXT_TASK);
            for(int i=0;i<nodeList.getLength();i++)
            {
                Node node = nodeList.item(i);
                if(node.getNodeType()==Node.ELEMENT_NODE)
                {
                    Element element = (Element) node;
                    listTasks.add(getTaskfromXMLElement(element));
                }
            }
            //Sorting listTasks according to specified Sort_Order
            sortListTasksBySortOrder();
            //Populating listEvents
            nodeList = rootElement.getElementsByTagName(XMLTEXT_EVENT);
            for(int i=0;i<nodeList.getLength();i++)
            {
                Node node = nodeList.item(i);
                if(node.getNodeType()==Node.ELEMENT_NODE)
                {
                    Element element = (Element) node;
                    listEvents.add(getEventfromXMLElement(element));
                }
            }
            //Sorting listEvents according to specified Sort_Order
            sortListEventsBySortOrder();
        }
        catch(Exception e)
        {
            Log.e(EXCEPTIONTAG_TASKLIST_DATAMANAGER, "createDataFile :"+e.getMessage());
        }
    }

    private void sortListTasksBySortOrder()
    {
        switch(tasksSortOrder)
        {
            case TASK_PRIORITY_ASC:
                //Comparing tasks on the basis of their priority.
                Collections.sort(listTasks, new Comparator<Task>() {
                    @Override
                    public int compare(Task lhs, Task rhs) {
                        if(lhs.getPriority().compareTo(rhs.getPriority())!=0)
                        {
                            return lhs.getPriority().compareTo(rhs.getPriority());
                        }
                        else
                        {
                            //If Priority equal then further sorting on the basis of CreationDateTime
                            return rhs.getCreationDateTime().compareTo(lhs.getCreationDateTime());
                        }
                    }
                });
                break;
            case TASK_PRIORITY_DSC:
                //Comparing tasks on the basis of their priority in reverse.
                Collections.sort(listTasks, new Comparator<Task>() {
                    @Override
                    public int compare(Task lhs, Task rhs) {
                        if(lhs.getPriority().compareTo(rhs.getPriority())!=0)
                        {
                            return rhs.getPriority().compareTo(lhs.getPriority());
                        }
                        else
                        {
                            //If Priority equal then further sorting on the basis of CreationDateTime
                            return rhs.getCreationDateTime().compareTo(lhs.getCreationDateTime());
                        }
                    }
                });
                break;
        }
    }

    private void sortListEventsBySortOrder()
    {
        switch(eventsSortOrder)
        {
            case EVENT_DATETIME_ASC:
                //Comparing events on the basis of their eventDateTime.
                Collections.sort(listEvents, new Comparator<Event>() {
                    @Override
                    public int compare(Event lhs, Event rhs) {
                        if(lhs.getEventDateTime().compareTo(rhs.getEventDateTime())!=0)
                        {
                            Calendar currentTime = Calendar.getInstance();
                            if(lhs.getEventDateTime().compareTo(currentTime)>0 && rhs.getEventDateTime().compareTo(currentTime)>0)
                            {
                                //case when both lhs and rhs events are upcoming
                                long lhsMillisFromCurrentTime=lhs.getEventDateTime().getTimeInMillis()-currentTime.getTimeInMillis();
                                long rhsMillisFromCurrentTime=rhs.getEventDateTime().getTimeInMillis()-currentTime.getTimeInMillis();
                                if(lhsMillisFromCurrentTime<rhsMillisFromCurrentTime)
                                    return -1;
                                else
                                    return 1;
                            }
                            else if(lhs.getEventDateTime().compareTo(currentTime)<0 && rhs.getEventDateTime().compareTo(currentTime)<0)
                            {
                                //case when both lhs and rhs events are passed
                                long lhsMillisFromCurrentTime=currentTime.getTimeInMillis()-lhs.getEventDateTime().getTimeInMillis();
                                long rhsMillisFromCurrentTime=currentTime.getTimeInMillis()-rhs.getEventDateTime().getTimeInMillis();
                                if(lhsMillisFromCurrentTime<rhsMillisFromCurrentTime)
                                    return -1;
                                else
                                    return 1;
                            }
                            else
                            {
                                //if returns negative value, order remains as it is, if it returns positive then order changed
                                return lhs.getEventDateTime().compareTo(rhs.getEventDateTime());
                            }
                        }
                        else
                        {
                            //If EventDatetime equal then further sorting on the basis of CreationDateTime
                            return rhs.getCreationDateTime().compareTo(lhs.getCreationDateTime());
                        }
                    }
                });
                break;
            case EVENT_DATETIME_DSC:
                //Comparing events on the basis of their eventDateTime in reverse.
                Collections.sort(listEvents, new Comparator<Event>() {
                    @Override
                    public int compare(Event lhs, Event rhs) {
                        if(lhs.getEventDateTime().compareTo(rhs.getEventDateTime())!=0)
                        {

                            Calendar currentTime = Calendar.getInstance();
                            if(lhs.getEventDateTime().compareTo(currentTime)>0 && rhs.getEventDateTime().compareTo(currentTime)>0)
                            {
                                //case when both lhs and rhs events are upcoming
                                long lhsMillisFromCurrentTime=lhs.getEventDateTime().getTimeInMillis()-currentTime.getTimeInMillis();
                                long rhsMillisFromCurrentTime=rhs.getEventDateTime().getTimeInMillis()-currentTime.getTimeInMillis();
                                if(lhsMillisFromCurrentTime<rhsMillisFromCurrentTime)
                                    return -1;
                                else
                                    return 1;
                            }
                            else if(lhs.getEventDateTime().compareTo(currentTime)<0 && rhs.getEventDateTime().compareTo(currentTime)<0)
                            {
                                //case when both lhs and rhs events are passed
                                long lhsMillisFromCurrentTime=currentTime.getTimeInMillis()-lhs.getEventDateTime().getTimeInMillis();
                                long rhsMillisFromCurrentTime=currentTime.getTimeInMillis()-rhs.getEventDateTime().getTimeInMillis();
                                if(lhsMillisFromCurrentTime<rhsMillisFromCurrentTime)
                                    return -1;
                                else
                                    return 1;
                            }
                            else
                            {
                                //if returns negative value, order remains as it is, if it returns positive then order changed.
                                return rhs.getEventDateTime().compareTo(lhs.getEventDateTime());
                            }

                        }
                        else
                        {
                            //If EventDatetime equal then further sorting on the basis of CreationDateTime
                            return rhs.getCreationDateTime().compareTo(lhs.getCreationDateTime());
                        }
                    }
                });
                break;
        }
    }


    /**
     * Creates a Task object from the xml element object representing a <TASK> element in the xml file.
     * @param elementTask the element representing <TASK></Task> element in xml file.
     * @return
     */
    private Task getTaskfromXMLElement(Element elementTask)
    {
        Task task;
        String title,description,priorityText,id;
        Priority priority;
        id = elementTask.getAttribute(XMLTEXT_ID);
        title = elementTask.getElementsByTagName(XMLTEXT_TITLE).item(0).getTextContent();
        description = elementTask.getElementsByTagName(XMLTEXT_DESCRIPTION).item(0).getTextContent();
        priorityText = elementTask.getElementsByTagName(XMLTEXT_PRIORITY).item(0).getTextContent();
        if(priorityText.equals(XMLTEXT_PRIORITY_URGENT))
        {
            priority = Priority.URGENT;
        }
        else if(priorityText.equals(XMLTEXT_PRIORITY_HIGH))
        {
            priority = Priority.HIGH;
        }
        else if(priorityText.equals(XMLTEXT_PRIORITY_MEDIUM))
        {
            priority = Priority.MEDIUM;
        }
        else
        {
            priority = Priority.LOW;
        }
        Calendar creationDateTime = getDateTimeFromXMLElement((Element) elementTask.getElementsByTagName(XMLTEXT_CREATION_DATETIME).item(0));
        task = new Task(Integer.valueOf(id),title,description,priority,creationDateTime);
        return task;
    }

    /**
     * Creates a Event object from the xml element object representing an <EVENT> element in the xml file.
     * @param elementEvent the element representing <EVENT></EVENT> element in xml file.
     * @return
     */
    private Event getEventfromXMLElement(Element elementEvent)
    {
        Event event;
        String id,title,description;
        id = elementEvent.getAttribute(XMLTEXT_ID);
        title = elementEvent.getElementsByTagName(XMLTEXT_TITLE).item(0).getTextContent();
        description = elementEvent.getElementsByTagName(XMLTEXT_DESCRIPTION).item(0).getTextContent();
        Calendar eventDateTime = getDateTimeFromXMLElement((Element) elementEvent.getElementsByTagName(XMLTEXT_EVENT_DATETIME).item(0));
        Calendar creationDateTime = getDateTimeFromXMLElement((Element) elementEvent.getElementsByTagName(XMLTEXT_CREATION_DATETIME).item(0));
        event = new Event(Integer.valueOf(id),title,description,eventDateTime,creationDateTime);
        return  event;
    }

    /**
     * Creates a Calendar object (for storing Date Time) from the xml element object representing a type of <DATETIME> element in the xml file.
     * @param elementDateTime the element representing a type of <DATETIME></DATETIME> element in xml file.
     * @return
     */
    private Calendar getDateTimeFromXMLElement(Element elementDateTime)
    {
        Calendar dateTime = Calendar.getInstance();
        String year,month,day,hour,minutes,seconds;
        Element date = (Element) elementDateTime.getElementsByTagName(XMLTEXT_DATE).item(0);
        //Date element contains year,month,day
        year = date.getElementsByTagName(XMLTEXT_YEAR).item(0).getTextContent();
        month = date.getElementsByTagName(XMLTEXT_MONTH).item(0).getTextContent();
        day = date.getElementsByTagName(XMLTEXT_DAY).item(0).getTextContent();
        Element time = (Element) elementDateTime.getElementsByTagName(XMLTEXT_TIME).item(0);
        //Time element contains hour_of_day,minutes,seconds
        hour = time.getElementsByTagName(XMLTEXT_HOUR_OF_DAY).item(0).getTextContent();
        minutes = time.getElementsByTagName(XMLTEXT_MINUTES).item(0).getTextContent();
        seconds = time.getElementsByTagName(XMLTEXT_SECONDS).item(0).getTextContent();
        dateTime.set(Calendar.YEAR,Integer.valueOf(year));
        dateTime.set(Calendar.MONTH,Integer.valueOf(month));
        dateTime.set(Calendar.DAY_OF_MONTH,Integer.valueOf(day));
        dateTime.set(Calendar.HOUR_OF_DAY,Integer.valueOf(hour));
        dateTime.set(Calendar.MINUTE,Integer.valueOf(minutes));
        dateTime.set(Calendar.SECOND,Integer.valueOf(seconds));
        return dateTime;
    }

    public void saveTask(Task task)
    {
        try{
            idCounter++;
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dataFile);
            Element rootElement = document.getDocumentElement();
            rootElement.normalize();
            //Updating idCounter in xml file
            Element settingsElement = (Element) rootElement.getElementsByTagName(XMLTEXT_SETTINGS).item(0);
            Element idCounterElement = (Element) settingsElement.getElementsByTagName(XMLTEXT_IDCOUNTER).item(0);
            //setTextContent replaces any child nodes of that the element already has with a single text node containing the string passed into this function.
            idCounterElement.setTextContent(String.valueOf(idCounter));
            //Adding new Task element in file
            Element newTaskElement = document.createElement(XMLTEXT_TASK);
            rootElement.appendChild(newTaskElement);
            //Adding attribute id
            Attr idAttr = document.createAttribute(XMLTEXT_ID);
            idAttr.setValue(String.valueOf(task.getId()));
            newTaskElement.setAttributeNode(idAttr);
            //Adding Title
            Element title = document.createElement(XMLTEXT_TITLE);
            newTaskElement.appendChild(title);
            title.appendChild(document.createTextNode(task.getTitle()));
            //Adding Description
            Element description = document.createElement(XMLTEXT_DESCRIPTION);
            newTaskElement.appendChild(description);
            description.appendChild(document.createTextNode(task.getDescription()));
            //Adding Priority
            Element priority = document.createElement(XMLTEXT_PRIORITY);
            newTaskElement.appendChild(priority);
            if(task.getPriority()==Priority.URGENT)
            {
                priority.appendChild(document.createTextNode(XMLTEXT_PRIORITY_URGENT));
            }
            else if(task.getPriority()==Priority.HIGH)
            {
                priority.appendChild(document.createTextNode(XMLTEXT_PRIORITY_HIGH));
            }
            else if(task.getPriority()==Priority.MEDIUM)
            {
                priority.appendChild(document.createTextNode(XMLTEXT_PRIORITY_MEDIUM));
            }
            else
            {
                priority.appendChild(document.createTextNode(XMLTEXT_PRIORITY_LOW));
            }
            //Adding CreationDate
            Element creationDateTimeElement = document.createElement(XMLTEXT_CREATION_DATETIME);
            newTaskElement.appendChild(creationDateTimeElement);
            addDateTimeToXMLElement(creationDateTimeElement, document, task.getCreationDateTime());
            //Updating XML File as per new DOM
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(dataFile));
            //Adding the task in the listTasks variable
            listTasks.add(task);
        }
        catch(Exception e)
        {
            Log.e(EXCEPTIONTAG_TASKLIST_DATAMANAGER, "saveTask :"+e.getMessage());
        }
    }

    public void saveEvent(Event event)
    {
        try{
            idCounter++;
            event.getEventDateTime().set(Calendar.SECOND, 0);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dataFile);
            Element rootElement = document.getDocumentElement();
            rootElement.normalize();
            //Updating idCounter in xml file
            Element settingsElement = (Element) rootElement.getElementsByTagName(XMLTEXT_SETTINGS).item(0);
            Element idCounterElement = (Element) settingsElement.getElementsByTagName(XMLTEXT_IDCOUNTER).item(0);
            //setTextContent replaces any child nodes of that the element already has with a single text node containing the string passed into this function.
            idCounterElement.setTextContent(String.valueOf(idCounter));
            Element newEventElement = document.createElement(XMLTEXT_EVENT);
            rootElement.appendChild(newEventElement);
            //Adding attribute id
            Attr idAttr = document.createAttribute(XMLTEXT_ID);
            idAttr.setValue(String.valueOf(event.getId()));
            newEventElement.setAttributeNode(idAttr);
            //Adding Title
            Element title = document.createElement(XMLTEXT_TITLE);
            newEventElement.appendChild(title);
            title.appendChild(document.createTextNode(event.getTitle()));
            //Adding Description
            Element description = document.createElement(XMLTEXT_DESCRIPTION);
            newEventElement.appendChild(description);
            description.appendChild(document.createTextNode(event.getDescription()));
            //Adding EventDateTime
            Element eventDateTimeElement = document.createElement(XMLTEXT_EVENT_DATETIME);
            newEventElement.appendChild(eventDateTimeElement);
            addDateTimeToXMLElement(eventDateTimeElement, document, event.getEventDateTime());
            //Adding CreationDateTime
            Element creationDateTimeElement = document.createElement(XMLTEXT_CREATION_DATETIME);
            newEventElement.appendChild(creationDateTimeElement);
            addDateTimeToXMLElement(creationDateTimeElement, document, event.getCreationDateTime());
            //Updating XML File as per new DOM
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(dataFile));
            //Adding the task in the listTasks variable
            listEvents.add(event);

            //Adding an alarm for this event using Alarm Manager
            setEventAlarm(event);
        }
        catch(Exception e)
        {
            Log.e(EXCEPTIONTAG_TASKLIST_DATAMANAGER, "saveEvent :"+e.getMessage());
        }
    }

    /**
     * Sets an alarm for the event using Alarm Manager.
     * @param event
     */
    private void setEventAlarm(Event event)
    {
        Intent notificationIntent = new Intent(context,NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.INTENT_EXTRA_NOTIFICATION,getEventNotification(event));
        notificationIntent.putExtra(NotificationPublisher.INTENT_EXTRA_NOTIFICATION_ID,event.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,event.getId(),notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //setting alarm for 30 minutes before event time
        alarmManager.set(AlarmManager.RTC, event.getEventDateTime().getTimeInMillis() - 1000 * 60 * 30, pendingIntent);
    }

    /**
     * Cancels the alarm set for this event using Alarm Manager.
     * @param event
     */
    private void cancelEventAlarm(Event event)
    {
        Intent notificationIntent = new Intent(context,NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.INTENT_EXTRA_NOTIFICATION,getEventNotification(event));
        notificationIntent.putExtra(NotificationPublisher.INTENT_EXTRA_NOTIFICATION_ID,event.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,event.getId(),notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //cancelling alarm for this pending intent
        alarmManager.cancel(pendingIntent);
    }

    /**
     * Builds and returns the notification to show when the alarm for an event goes off.
     * @param event
     * @return
     */
    private Notification getEventNotification(Event event)
    {
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(context);
        builder.setContentTitle(event.getTitle());
        builder.setContentText(EventHelper.getEventTimeDisplayString(event.getEventDateTime(),context));
        builder.setSmallIcon(R.drawable.ic_stat_notification_event_available);
        builder.setCategory(Notification.CATEGORY_EVENT);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setVibrate(new long[]{500, 1000});
        builder.setLights(Color.GREEN, 2000, 2000);
        builder.setColor(ContextCompat.getColor(context,R.color.event_color));

        //setting notification content intent
        Intent intent = new Intent(context,DisplayEventActivity.class);
        //Adding event data to intent extras
        EventHelper.addEventDataToIntentExtras(event,intent);
        //Setting up parent stack
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(DisplayEventActivity.class);
        taskStackBuilder.addNextIntent(intent);
        //Setting Tab position in the TaskListActivityIntent to display events tab
        taskStackBuilder.editIntentAt(0).putExtra(DataManager.INTENT_EXTRA_TAB_POSITION, 1);
        builder.setContentIntent(taskStackBuilder.getPendingIntent(event.getId(), PendingIntent.FLAG_UPDATE_CURRENT));
        return builder.build();
    }

    private void addDateTimeToXMLElement(Element dateTimeElement,Document document,Calendar dateTime)
    {
        //Adding Date
        Element date = document.createElement(XMLTEXT_DATE);
        dateTimeElement.appendChild(date);
        //Adding day in date
        Element day = document.createElement(XMLTEXT_DAY);
        date.appendChild(day);
        day.appendChild(document.createTextNode(String.valueOf(dateTime.get(Calendar.DAY_OF_MONTH))));
        //Adding month in date
        Element month = document.createElement(XMLTEXT_MONTH);
        date.appendChild(month);
        month.appendChild(document.createTextNode(String.valueOf(dateTime.get(Calendar.MONTH))));
        //Adding year in date
        Element year = document.createElement(XMLTEXT_YEAR);
        date.appendChild(year);
        year.appendChild(document.createTextNode(String.valueOf(dateTime.get(Calendar.YEAR))));
        //Adding Time
        Element time = document.createElement(XMLTEXT_TIME);
        dateTimeElement.appendChild(time);
        //Adding hour in time
        Element hour = document.createElement(XMLTEXT_HOUR_OF_DAY);
        time.appendChild(hour);
        hour.appendChild(document.createTextNode(String.valueOf(dateTime.get(Calendar.HOUR_OF_DAY))));
        //Adding minute in time
        Element minute = document.createElement(XMLTEXT_MINUTES);
        time.appendChild(minute);
        minute.appendChild(document.createTextNode(String.valueOf(dateTime.get(Calendar.MINUTE))));
        //Adding second in time
        Element  seconds = document.createElement(XMLTEXT_SECONDS);
        time.appendChild(seconds);
        seconds.appendChild(document.createTextNode(String.valueOf(dateTime.get(Calendar.SECOND))));
    }

    public void deleteTaskWithID(int id)
    {
        try {
            listTasks = new ArrayList<>();
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dataFile);
            Element rootElement = document.getDocumentElement();
            rootElement.normalize();
            //Checking nodelist of elements <TASK> to find and delete the require <TASK> element.
            NodeList nodeList = rootElement.getElementsByTagName(XMLTEXT_TASK);
            for(int i=0;i<nodeList.getLength();i++)
            {
                Node node = nodeList.item(i);
                if(node.getNodeType()==Node.ELEMENT_NODE)
                {
                    Element element = (Element) node;
                    Task task = getTaskfromXMLElement(element);
                    if(task.getId()==id)
                    {
                        element.getParentNode().removeChild(element);
                        break;
                    }
                    else
                    {
                        listTasks.add(task);
                    }
                }
            }
            //Updating XML File as per new DOM
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(dataFile));
        }
        catch(Exception e)
        {
            Log.e(EXCEPTIONTAG_TASKLIST_DATAMANAGER, "deleteTaskWithID :"+e.getMessage());
        }
    }

    public void deleteEventWithID(int id)
    {
        try {
            listEvents = new ArrayList<>();
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dataFile);
            Element rootElement = document.getDocumentElement();
            rootElement.normalize();
            //Checking nodelist of elements <EVENT> to find and delete the require <EVENT> element.
            NodeList nodeList = rootElement.getElementsByTagName(XMLTEXT_EVENT);
            for(int i=0;i<nodeList.getLength();i++)
            {
                Node node = nodeList.item(i);
                if(node.getNodeType()==Node.ELEMENT_NODE)
                {
                    Element element = (Element) node;
                    Event event = getEventfromXMLElement(element);
                    if(event.getId()==id)
                    {
                        //Deleting alarm added for this event using Alarm Manager.
                        cancelEventAlarm(event);
                        //Deleting event from xml data file
                        element.getParentNode().removeChild(element);
                        break;
                    }
                    else
                    {
                        listEvents.add(event);
                    }
                }
            }
            //Updating XML File as per new DOM
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(dataFile));
        }
        catch(Exception e)
        {
            Log.e(EXCEPTIONTAG_TASKLIST_DATAMANAGER, "deleteEventWithID :"+e.getMessage());
        }
    }

    public void updateTaskWithId(int updateTaskId, String newTitle, String newDescription, Priority newPriority)
    {
        try{
            int elementTaskId;
            //Getting Root Element
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dataFile);
            Element rootElement = document.getDocumentElement();
            rootElement.normalize();
            //Searching  nodelist of Taskelement to find the task element to update.
            NodeList nodeList = rootElement.getElementsByTagName(XMLTEXT_TASK);
            for(int i=0;i<nodeList.getLength();i++)
            {
                Node node = nodeList.item(i);
                if(node.getNodeType()==Node.ELEMENT_NODE)
                {
                    Element elementTask = (Element) node;
                    elementTaskId = Integer.valueOf(elementTask.getAttribute(XMLTEXT_ID));
                    if(elementTaskId==updateTaskId)
                    {
                        //setTextContent replaces any child nodes that the element already has with a single text node containing the string passed into this function.
                        Element elementTitle = (Element) elementTask.getElementsByTagName(XMLTEXT_TITLE).item(0);
                        elementTitle.setTextContent(newTitle);
                        Element elementDescription = (Element) elementTask.getElementsByTagName(XMLTEXT_DESCRIPTION).item(0);
                        elementDescription.setTextContent(newDescription);
                        Element elementPriority = (Element) elementTask.getElementsByTagName(XMLTEXT_PRIORITY).item(0);
                        switch (newPriority)
                        {
                            case URGENT:
                                elementPriority.setTextContent(XMLTEXT_PRIORITY_URGENT);
                                break;
                            case HIGH:
                                elementPriority.setTextContent(XMLTEXT_PRIORITY_HIGH);
                                break;
                            case MEDIUM:
                                elementPriority.setTextContent(XMLTEXT_PRIORITY_MEDIUM);
                                break;
                            default:
                                elementPriority.setTextContent(XMLTEXT_PRIORITY_LOW);

                        }
                    }
                }
            }
            //Updating XML File as per new DOM
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(dataFile));
        }
        catch(Exception e)
        {
            Log.e(EXCEPTIONTAG_TASKLIST_DATAMANAGER, "updateTaskWithID :"+e.getMessage());
        }
    }

    public void updateEventWithId(int updateEventId, String newTitle, String newDescription, Calendar newEventDatetime)
    {
        try{

            int elementEventId;
            //Getting Root Element
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dataFile);
            Element rootElement = document.getDocumentElement();
            rootElement.normalize();
            //Searching  node list of Event elements to find the task element to update.
            NodeList nodeList = rootElement.getElementsByTagName(XMLTEXT_EVENT);
            for(int i=0;i<nodeList.getLength();i++)
            {
                Node node = nodeList.item(i);
                if(node.getNodeType()==Node.ELEMENT_NODE)
                {
                    Element elementEvent = (Element) node;
                    elementEventId = Integer.valueOf(elementEvent.getAttribute(XMLTEXT_ID));
                    if(elementEventId==updateEventId)
                    {
                        //updating Event Element with new data
                        Element elementTitle = (Element) elementEvent.getElementsByTagName(XMLTEXT_TITLE).item(0);
                        elementTitle.setTextContent(newTitle);
                        Element elementDescription = (Element) elementEvent.getElementsByTagName(XMLTEXT_DESCRIPTION).item(0);
                        elementDescription.setTextContent(newDescription);
                        //Deleting the old eventDateTime xml element
                        Element elementEventDateTime = (Element) elementEvent.getElementsByTagName(XMLTEXT_EVENT_DATETIME).item(0);
                        elementEventDateTime.getParentNode().removeChild(elementEventDateTime);
                        //Adding new EventDateTime xml element with the updated data
                        Element elementNewEventDateTime = document.createElement(XMLTEXT_EVENT_DATETIME);
                        elementEvent.appendChild(elementNewEventDateTime);
                        addDateTimeToXMLElement(elementNewEventDateTime, document, newEventDatetime);

                        //Updating alarm for this event by setting it again
                        setEventAlarm(getEventfromXMLElement(elementEvent));
                    }
                }
            }
            //Updating XML File as per new DOM
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(dataFile));

        }
        catch(Exception e)
        {
            Log.e(EXCEPTIONTAG_TASKLIST_DATAMANAGER, "updateEventWithID :"+e.getMessage());
        }
    }

    public void updateTasksSortOrder(SORT_ORDER sortOrder)
    {
        try {
            tasksSortOrder = sortOrder;
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dataFile);
            Element rootElement = document.getDocumentElement();
            rootElement.normalize();
            //Updating tasksSortOrder in xml file
            Element settingsElement = (Element) rootElement.getElementsByTagName(XMLTEXT_SETTINGS).item(0);
            Element tasksSortOrderElement = (Element) settingsElement.getElementsByTagName(XMLTEXT_TASKS_SORT_ORDER).item(0);
            //setTextContent replaces any child nodes of that the element already has with a single text node containing the string passed into this function.
            tasksSortOrderElement.setTextContent(getStringFromSortOrder(tasksSortOrder));
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(dataFile));
            sortListTasksBySortOrder();
        }
        catch(Exception e)
        {
            Log.e(EXCEPTIONTAG_TASKLIST_DATAMANAGER, "updateTasksSortOrder :"+e.getMessage());
        }
    }

    public void updateEventsSortOrder(SORT_ORDER sortOrder)
    {
        try {
            eventsSortOrder = sortOrder;
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dataFile);
            Element rootElement = document.getDocumentElement();
            rootElement.normalize();
            //Updating eventsSortOrder in xml file
            Element settingsElement = (Element) rootElement.getElementsByTagName(XMLTEXT_SETTINGS).item(0);
            Element eventsSortOrderElement = (Element) settingsElement.getElementsByTagName(XMLTEXT_EVENTS_SORT_ORDER).item(0);
            //setTextContent replaces any child nodes of that the element already has with a single text node containing the string passed into this function.
            eventsSortOrderElement.setTextContent(getStringFromSortOrder(eventsSortOrder));
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(dataFile));
            sortListEventsBySortOrder();
        }
        catch(Exception e)
        {
            Log.e(EXCEPTIONTAG_TASKLIST_DATAMANAGER, "updateEventsSortOrder :"+e.getMessage());
        }
    }

    //Getters
    public ArrayList<Task> getListOfTasks()
    {
        return listTasks;
    }

    public ArrayList<Event> getListOfEvents()
    {
        return listEvents;
    }

    /**
     * Always use this function to assign ID to new tasks.
     * @return
     */
    public int getIdCounter()
    {
        return idCounter;
    }
    public SORT_ORDER getTasksSortOrder()
    {
        return tasksSortOrder;
    }
    public SORT_ORDER getEventsSortOrder()
    {
        return eventsSortOrder;
    }
}
