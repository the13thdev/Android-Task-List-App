package me.vijitdhingra.tasklist;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class EditEventActivity extends AppCompatActivity implements  DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

    private int eventId;
    private String title,description;
    private Calendar eventDateTime,originalEventDateTime;
    private DataManager dataManager;
    private CardView cardViewEvent;
    private EditText editTextTitle,editTextDescription;
    private Button buttonDate,buttonTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        getViews();
        DisplayHelper.adjustCardViewDisplay(cardViewEvent);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        eventDateTime = Calendar.getInstance();
        originalEventDateTime = Calendar.getInstance();
        dataManager = new DataManager(this);
        getDataFromIntent();
        displayOriginalData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done)
        {
            if(hasUserChangedData())
            {
                if(isInputValid())
                {
                    updateEvent();
                    Toast.makeText(this, "Event Updated", Toast.LENGTH_SHORT).show();
                    startActivityDisplayEvent(eventDateTime);
                }
            }
            else
            {
                startActivityDisplayEvent(originalEventDateTime);
            }
            return true;
        }
        else if(id==android.R.id.home)
        {
            /*
             * Case when the user presses the back button on the action bar to go to the parent activity.
             * Will prompt a dialog to confirm exit if the user has entered some data and not saved the task.
             */
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getViews()
    {
        editTextTitle = (EditText) findViewById(R.id.editTextEventTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextEventDescription);
        buttonDate = (Button) findViewById(R.id.button_setEventDate);
        buttonTime = (Button) findViewById(R.id.button_setEventTime);
        cardViewEvent = (CardView) findViewById(R.id.cardViewEvent)        ;
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
        eventDateTime.set(Calendar.DAY_OF_MONTH,intent.getIntExtra(DataManager.XMLTEXT_DAY,-1));
        eventDateTime.set(Calendar.MONTH,intent.getIntExtra(DataManager.XMLTEXT_MONTH,-1));
        eventDateTime.set(Calendar.YEAR,intent.getIntExtra(DataManager.XMLTEXT_YEAR,-1));
        eventDateTime.set(Calendar.HOUR_OF_DAY, intent.getIntExtra(DataManager.XMLTEXT_HOUR_OF_DAY, -1));
        eventDateTime.set(Calendar.MINUTE, intent.getIntExtra(DataManager.XMLTEXT_MINUTES, -1));
        eventDateTime.set(Calendar.SECOND, intent.getIntExtra(DataManager.XMLTEXT_SECONDS, -1));
        originalEventDateTime.set(Calendar.DAY_OF_MONTH,intent.getIntExtra(DataManager.XMLTEXT_DAY,-1));
        originalEventDateTime.set(Calendar.MONTH,intent.getIntExtra(DataManager.XMLTEXT_MONTH,-1));
        originalEventDateTime.set(Calendar.YEAR,intent.getIntExtra(DataManager.XMLTEXT_YEAR,-1));
        originalEventDateTime.set(Calendar.HOUR_OF_DAY, intent.getIntExtra(DataManager.XMLTEXT_HOUR_OF_DAY, -1));
        originalEventDateTime.set(Calendar.MINUTE, intent.getIntExtra(DataManager.XMLTEXT_MINUTES, -1));
        originalEventDateTime.set(Calendar.SECOND, intent.getIntExtra(DataManager.XMLTEXT_SECONDS, -1));
    }


    /**
     * Displays original event data into the appropriate views in layout so that it can be edited by the user.
     */
    public void displayOriginalData()
    {
        /*if(eventDateTime.compareTo(Calendar.getInstance())<0)
        {
            //case when event has passed
            cardViewEvent.setCardBackgroundColor(ContextCompat.getColor(this, R.color.event_passed_color));
        }
        else
        {
            cardViewEvent.setCardBackgroundColor(ContextCompat.getColor(this, R.color.event_color));
        }*/
        editTextTitle.setText(title);
        editTextDescription.setText(description);
        buttonDate.setText(EventHelper.getEventDateDisplayString(eventDateTime));
        buttonTime.setText(EventHelper.getEventTimeDisplayString(eventDateTime,this));
    }

    /**
     * Updates data about the event in the xml file with the new data
     */
    public void updateEvent()
    {
        String newTitle=editTextTitle.getText().toString();
        String newDescription=editTextDescription.getText().toString();
        dataManager.updateEventWithId(eventId,newTitle,newDescription,eventDateTime);
        title=newTitle;
        description=newDescription;
    }

    @Override
    public void onBackPressed()
    {
        if(hasUserChangedData())
        {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setTitle("Exit?")
                    .setMessage("You have not saved the changes you made. Are you sure you want to go back?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityDisplayEvent(originalEventDateTime);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        else
        {
            startActivityDisplayEvent(originalEventDateTime);
        }
    }

    public boolean hasUserChangedData()
    {
        String newTitle = editTextTitle.getText().toString();
        String newDescription = editTextDescription.getText().toString();
        if(newTitle.equals(title) && newDescription.equals(description) && EventHelper.compareToDoDateTimeEqual(originalEventDateTime, eventDateTime))
        {
            return  false;
        }
        else
        {
            return  true;
        }
    }

    /**
     * validates the input entered by the user. Returns true if input is valid, else false.
     * Also makes appropriate toasts to guide user to enter the right input if the input entered is not valid
     * @return
     */
    public boolean isInputValid()
    {
        int titleLength = editTextTitle.getText().length();
        if(titleLength==0)
        {
            Toast.makeText(this, "You must enter a title", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(eventDateTime.before(Calendar.getInstance()))
        {
            Toast.makeText(this, "Date and time mentioned must be after the current date and time", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Starts activity displayEvent to display event by passing the necessary event data through intent.
     * @param
     */
    public void startActivityDisplayEvent(Calendar dateTime)
    {
        Intent intent = new Intent(this,DisplayEventActivity.class);
        intent.putExtra(DataManager.XMLTEXT_ID, eventId);
        intent.putExtra(DataManager.XMLTEXT_TITLE,title);
        intent.putExtra(DataManager.XMLTEXT_DESCRIPTION,description);
        //Adding Event Date Time data to intent extras.
        intent.putExtra(DataManager.XMLTEXT_DAY,dateTime.get(Calendar.DAY_OF_MONTH));
        intent.putExtra(DataManager.XMLTEXT_MONTH,dateTime.get(Calendar.MONTH));
        intent.putExtra(DataManager.XMLTEXT_YEAR,dateTime.get(Calendar.YEAR));
        intent.putExtra(DataManager.XMLTEXT_HOUR_OF_DAY,dateTime.get(Calendar.HOUR_OF_DAY));
        intent.putExtra(DataManager.XMLTEXT_MINUTES,dateTime.get(Calendar.MINUTE));
        intent.putExtra(DataManager.XMLTEXT_SECONDS, dateTime.get(Calendar.SECOND));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Called every time the user sets Date in the DatePicker Dialog.
     * Updates event_dateTime with the values entered by the user every time he sets the date using the dialog.
     * Hence the event_dateTime will always contain information that ws last entered by the user.
     * @param datePicker
     * @param year
     * @param month
     * @param day
     */
    public void onDateSet(DatePicker datePicker,int year, int month, int day )
    {
        eventDateTime.set(Calendar.YEAR,year);
        eventDateTime.set(Calendar.MONTH,month);
        eventDateTime.set(Calendar.DAY_OF_MONTH,day);
        buttonDate.setText(EventHelper.getEventDateDisplayString(eventDateTime));
    }

    /**
     * Called every time the user sets Time in the TimePicker Dialog.
     * Updates event_dateTime with the values entered by the user every time he sets the date using the dialog.
     * Hence the event_dateTime will always contain information that ws last entered by the user.
     * @param timePicker
     * @param hour
     * @param minute
     */
    public void onTimeSet(TimePicker timePicker,int hour, int minute)
    {
        eventDateTime.set(Calendar.HOUR_OF_DAY,hour);
        eventDateTime.set(Calendar.MINUTE,minute);
        //eventDateTime values must not be changed further in method, only accessed
        buttonTime.setText(EventHelper.getEventTimeDisplayString(eventDateTime,this));
    }

    /**
     * To show the TimePickerDialog on click of the button_time
     * @param view
     */
    public void showTimePicker(View view)
    {
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.show(getFragmentManager(),"timePicker");
    }

    /**
     * To show the DatePickerDialog on click of the button_date
     * @param view
     */
    public void showDatePicker(View view)
    {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getFragmentManager(),"datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            //Since this fragment is only used in the Create Event Activity, hence the following line of code will do.
            EditEventActivity currentActivity = (EditEventActivity) getActivity();
            //eventDateTime contains the original event date and time.
            Calendar c = currentActivity.eventDateTime;
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(),(DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);
        }

    }

    public static class TimePickerFragment extends DialogFragment
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            //Since this fragment is only used in the Create Event Activity, hence the following line of code will do.
            EditEventActivity currentActivity = (EditEventActivity) getActivity();
            //eventDateTime contains the original event date and time.
            Calendar c = currentActivity.eventDateTime;
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(),(TimePickerDialog.OnTimeSetListener) getActivity(),hour,minute, DateFormat.is24HourFormat(getActivity()));
        }
    }
}
