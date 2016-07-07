package me.vijitdhingra.tasklist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class CreateTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Spinner spinnerPriorityOptions;
    private CardView cardViewTask;
    private DataManager dataManagerTasklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getViews();
        DisplayHelper.adjustCardViewDisplay(cardViewTask);
        dataManagerTasklist = new DataManager(this);
        populateSpinnerPriorityOptions();
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
        if (id == R.id.action_done) {
            if(isInputValid())
            {
                saveTask();
                Toast.makeText(this, "Task Created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,TasklistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
        }
        else if(id==android.R.id.home) {
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
                    .setMessage("You have not saved the new task. Are you sure you want to go back?")
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
        cardViewTask = (CardView) findViewById(R.id.cardViewTask);
        editTextTitle = (EditText) findViewById(R.id.editTextTaskTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextTaskDescription);
        spinnerPriorityOptions = (Spinner) this.findViewById(R.id.spinnerPriorityOptions);
    }

    public boolean hasUserEnteredData()
    {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        if(title.length()>0 || description.length()>0 || spinnerPriorityOptions.getSelectedItemPosition()>0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Populates the spinner priority_options with priority values mentioned in an string-array in strings.xml
     */
    public void populateSpinnerPriorityOptions()
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.task_priority_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriorityOptions.setAdapter(adapter);
        spinnerPriorityOptions.setOnItemSelectedListener(this);
    }

    /**
     *
     * @param parent
     * @param view
     * @param pos
     * @param id
     * This method is called when an option is selected in the spinner named spinnerPriorityOptions
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        switch(pos)
        {
            case 0:
                cardViewTask.setCardBackgroundColor(ContextCompat.getColor(this,R.color.priority_urgent_color));
                break;
            case 1:
                cardViewTask.setCardBackgroundColor(ContextCompat.getColor(this,R.color.priority_high_color));
                break;
            case 2:
                cardViewTask.setCardBackgroundColor(ContextCompat.getColor(this,R.color.priority_medium_color));
                break;
            default:
                cardViewTask.setCardBackgroundColor(ContextCompat.getColor(this,R.color.priority_low_color));
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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
        else
        {
            return true;
        }
    }

    /**
     * Saves data to xml file using dataManager
     */
    public void saveTask()
    {
        int id=dataManagerTasklist.getIdCounter();
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        Priority taskPriority;
        int spinnerPos = spinnerPriorityOptions.getSelectedItemPosition();
        switch (spinnerPos)
        {
            case 0:
                taskPriority = Priority.URGENT;
                break;
            case 1:
                taskPriority = Priority.HIGH;
                break;
            case 2:
                taskPriority = Priority.MEDIUM;
                break;
            default:
                taskPriority = Priority.LOW;
        }
        dataManagerTasklist.saveTask(new Task(id,title,description,taskPriority, Calendar.getInstance()));
    }

}
