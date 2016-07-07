package me.vijitdhingra.tasklist;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class CreateEventActivity extends AppCompatActivity implements  DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

    private CardView cardViewEvent;
    private EditText editTextTitle,editTextDescription;
    private Button buttonDate,buttonTime;
    private boolean dateMentioned,timeMentioned;
    private Calendar eventDateTime;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getViews();
        DisplayHelper.adjustCardViewDisplay(cardViewEvent);
        dateMentioned=false;
        timeMentioned=false;
        eventDateTime = Calendar.getInstance();
        dataManager = new DataManager(this);
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
            if(isInputValid())
            {
                saveEvent();
                Toast.makeText(this, "Event Created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,TasklistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(DataManager.INTENT_EXTRA_TAB_POSITION,1);
                startActivity(intent);
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

    @Override
    public void onBackPressed()
    {
        if(hasUserEnteredData())
        {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setTitle("Exit?")
                    .setMessage("You have not saved the new event. Are you sure you want to go back?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        else
        {
            finish();
        }
    }

    public void getViews()
    {
        editTextTitle = (EditText) findViewById(R.id.editTextEventTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextEventDescription);
        buttonDate = (Button) findViewById(R.id.button_setEventDate);
        buttonTime = (Button) findViewById(R.id.button_setEventTime);
        cardViewEvent = (CardView) findViewById(R.id.cardViewEvent);
    }

    public boolean hasUserEnteredData()
    {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        if(title.length()>0 || description.length()>0 || dateMentioned || timeMentioned)
        {
            return  true;
        }
        else
        {
            return  false;
        }
    }

    /**
     * Saves data to xml file using dataManager
     */
    public void saveEvent()
    {
        int id=dataManager.getIdCounter();
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        dataManager.saveEvent(new Event(id,title,description,eventDateTime,Calendar.getInstance()));
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
        else if(dateMentioned==false || timeMentioned==false)
        {
            Toast.makeText(this, "You must mention the date and time", Toast.LENGTH_SHORT).show();
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
        dateMentioned=true;
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
    public void onTimeSet(TimePicker timePicker,int hour, int minute) {
        timeMentioned=true;
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
        dialogFragment.show(getFragmentManager(), "timePicker");
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
            Calendar c;
            //Since this fragment is only used in the Create Event Activity, hence the following line of code will do.
            CreateEventActivity currentActivity = (CreateEventActivity) getActivity();
            if(currentActivity.dateMentioned)
            {
                //If date has already been set once by the user.
                c=currentActivity.eventDateTime;
            }
            else
            {
                //If date  has not yet been set by the user
                c=Calendar.getInstance();
            }
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
            Calendar c;
            //Since this fragment is only used in the Create Event Activity, hence the following line of code will do.
            CreateEventActivity currentActivity = (CreateEventActivity) getActivity();
            if(currentActivity.timeMentioned)
            {
                //If date has already been set once by the user.
                c=currentActivity.eventDateTime;
            }
            else
            {
                //If date  has not yet been set by the user
                c=Calendar.getInstance();
            }
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(),(TimePickerDialog.OnTimeSetListener) getActivity(),hour,minute, DateFormat.is24HourFormat(getActivity()));
        }
    }
}
