package me.vijitdhingra.tasklist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class DisplayEventActivity extends AppCompatActivity {

    private int eventId;
    private String title,description;
    private Calendar eventDateTime;
    private DataManager dataManager;
    private Handler handler;
    private Runnable runnableCode;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewEventDateTime;
    private TextView textViewEventDaysLeft;
    private TextView textViewEventTimeLeft;
    private TextView textViewEventTimeHintLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_event);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        textViewTitle = (TextView) findViewById(R.id.textViewDisplayEventTitle);
        textViewDescription = (TextView) findViewById(R.id.textViewDisplayEventDescription);
        textViewEventDateTime = (TextView) findViewById(R.id.textViewDisplayEventDateTime);
        textViewEventDaysLeft = (TextView) findViewById(R.id.textViewDisplayEventDaysLeft);
        textViewEventTimeLeft = (TextView) findViewById(R.id.textViewDisplayEventTimeLeft);
        textViewEventTimeHintLabel = (TextView) findViewById(R.id.textViewDisplayEventTimeHintLabel);
        dataManager = new DataManager(this);
        getDataFromIntent();
        handler = new Handler();
        runnableCode = new Runnable() {
            @Override
            public void run() {
                updateEventTimeLeft();
                if(handler!=null)
                {
                    updateEventTimeLeft();
                    handler.postDelayed(runnableCode, 1000);

                }
            }
        };
        displayData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            startActivityEditEvent();
            return true;
        }
        else if(id==R.id.action_delete)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete?")
                    .setMessage("Are you sure you want to delete this Event?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dataManager.deleteEventWithID(eventId);
                            Toast.makeText(DisplayEventActivity.this, "Event Deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DisplayEventActivity.this,TasklistActivity.class);
                            intent.putExtra(DataManager.INTENT_EXTRA_TAB_POSITION,1);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        else if(id==android.R.id.home)
        {
            /*
             * Case when the user presses the back button on the action bar to go to the parent activity.
             * This way the events tab remains selected while going pack to the task list activity.
             */
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        stopUpdatingEventTimeLeft();
        handler=null;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        handler=new Handler();
        startUpdatingEventTimeLeft();
    }

    /**
     * Gets the necessary event data from the intent passed to this activity
     */
    public void getDataFromIntent()
    {
        Intent intent = getIntent();
        eventId = intent.getIntExtra(DataManager.XMLTEXT_ID, -1);
        title = intent.getStringExtra(DataManager.XMLTEXT_TITLE);
        description = intent.getStringExtra(DataManager.XMLTEXT_DESCRIPTION);
        eventDateTime = Calendar.getInstance();
        eventDateTime.set(Calendar.DAY_OF_MONTH,intent.getIntExtra(DataManager.XMLTEXT_DAY,-1));
        eventDateTime.set(Calendar.MONTH,intent.getIntExtra(DataManager.XMLTEXT_MONTH,-1));
        eventDateTime.set(Calendar.YEAR,intent.getIntExtra(DataManager.XMLTEXT_YEAR,-1));
        eventDateTime.set(Calendar.HOUR_OF_DAY, intent.getIntExtra(DataManager.XMLTEXT_HOUR_OF_DAY, -1));
        eventDateTime.set(Calendar.MINUTE, intent.getIntExtra(DataManager.XMLTEXT_MINUTES, -1));
        eventDateTime.set(Calendar.SECOND, intent.getIntExtra(DataManager.XMLTEXT_SECONDS, -1));
    }


    /**
     * Displays event data into the appropriate text views in layout.
     */
    public void displayData()
    {
        textViewTitle.setText(title);
        if(description.length()>0)
        {
            textViewDescription.setText(description);
        }
        else
        {
            textViewDescription.setText("No description has been set for this event.");
        }
        textViewEventDateTime.setText(EventHelper.getEventDateDisplayString(eventDateTime) + " at " + EventHelper.getEventTimeDisplayString(eventDateTime, this));
        startUpdatingEventTimeLeft();
    }

    public void updateEventTimeLeft()
    {
        Calendar currentTime = Calendar.getInstance();
        if(currentTime.before(eventDateTime))
        {
            long x = (eventDateTime.getTimeInMillis() - currentTime.getTimeInMillis()) / 1000;
            long secLeft, minLeft, hrLeft, daysLeft;
            secLeft = x % 60;
            x /= 60;
            minLeft = x % 60;
            x /= 60;
            hrLeft = x % 24;
            x /= 24;
            daysLeft = x;
            String daysLeftString, timeLeftString = "";
            if (daysLeft == 1) {
                daysLeftString = "1 day";
            } else {
                daysLeftString = daysLeft + " days";
            }
            if (hrLeft < 10) {
                timeLeftString += "0" + hrLeft + ":";
            } else {
                timeLeftString += hrLeft + ":";
            }
            if (minLeft < 10) {
                timeLeftString += "0" + minLeft + ":";
            } else {
                timeLeftString += minLeft + ":";
            }
            if (secLeft < 10) {
                timeLeftString += "0" + secLeft;
            } else {
                timeLeftString += secLeft;
            }
            textViewEventDaysLeft.setText(daysLeftString);
            textViewEventTimeLeft.setText(timeLeftString);
        }
        else
        {
            textViewEventDaysLeft.setText("Event");
            textViewEventTimeLeft.setText("Occurred");
            textViewEventTimeHintLabel.setVisibility(View.INVISIBLE);
            stopUpdatingEventTimeLeft();
            handler=null;
        }
    }


    /**
     * Starts activity editEvent to display event by passing the necessary event data through intent.
     * @param
     */
    public void startActivityEditEvent()
    {
        Intent intent = new Intent(this,EditEventActivity.class);
        intent.putExtra(DataManager.XMLTEXT_ID, eventId);
        intent.putExtra(DataManager.XMLTEXT_TITLE,title);
        intent.putExtra(DataManager.XMLTEXT_DESCRIPTION,description);
        //Adding Event Date Time data to intent extras.
        intent.putExtra(DataManager.XMLTEXT_DAY,eventDateTime.get(Calendar.DAY_OF_MONTH));
        intent.putExtra(DataManager.XMLTEXT_MONTH,eventDateTime.get(Calendar.MONTH));
        intent.putExtra(DataManager.XMLTEXT_YEAR,eventDateTime.get(Calendar.YEAR));
        intent.putExtra(DataManager.XMLTEXT_HOUR_OF_DAY,eventDateTime.get(Calendar.HOUR_OF_DAY));
        intent.putExtra(DataManager.XMLTEXT_MINUTES,eventDateTime.get(Calendar.MINUTE));
        intent.putExtra(DataManager.XMLTEXT_SECONDS,eventDateTime.get(Calendar.SECOND));
        startActivity(intent);
    }

    public void startUpdatingEventTimeLeft()
    {
        runnableCode.run();
    }
    public void stopUpdatingEventTimeLeft()
    {
        if(handler!=null)
        {
            handler.removeCallbacks(runnableCode);
        }
    }
}
