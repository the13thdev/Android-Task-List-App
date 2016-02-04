package me.vijitdhingra.tasklist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * This activity uses the same layout and menu as the create_task activity
 */
public class EditTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private int taskId;
    private String title,description;
    private Priority priority;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private Spinner spinnerPriorityOptions;
    private CardView cardView;
    private DataManager dataManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        getDataFromIntent();
        dataManager = new DataManager(this);
        cardView = (CardView) findViewById(R.id.cardViewTask);
        editTextTitle = (EditText) findViewById(R.id.editTextTaskTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextTaskDescription);
        spinnerPriorityOptions = (Spinner) this.findViewById(R.id.spinnerPriorityOptions);
        populateSpinnerPriorityOptions();
        displayOrignalData();
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
            if(userHasChangedData())
            {
                if(isInputValid())
                {
                    /*Only updates data in xml file when data has actually been changed*/
                    updateTask();
                    Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show();
                }
            }
            startDisplayTaskActivityWithTask();
            return true;
        }
        else if(id==android.R.id.home)
        {
            /*
             * Case when the user presses the back button on the action bar to go to the parent activity.
             * By overriding the default behavior in this case amd manually launching the parent activity DisplayTask
             * by passing data with the intent removes the error due to system automatically launching the parent activity without passing data with the intent.
             */
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                cardView.setCardBackgroundColor(ContextCompat.getColor(this,R.color.priority_urgent_color));
                break;
            case 1:
                cardView.setCardBackgroundColor(ContextCompat.getColor(this,R.color.priority_high_color));
                break;
            case 2:
                cardView.setCardBackgroundColor(ContextCompat.getColor(this,R.color.priority_medium_color));
                break;
            default:
                cardView.setCardBackgroundColor(ContextCompat.getColor(this,R.color.priority_low_color));
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    /**
     * Gets the Task id,title,description and priority from the intent passed to this activity
     */
    public void getDataFromIntent()
    {
        Intent intent = getIntent();
        taskId = intent.getIntExtra(DataManager.XMLTEXT_ID, -1);
        title = intent.getStringExtra(DataManager.XMLTEXT_TITLE);
        description = intent.getStringExtra(DataManager.XMLTEXT_DESCRIPTION);
        String priorityString = intent.getStringExtra(DataManager.XMLTEXT_PRIORITY);
        if(priorityString.equals(DataManager.XMLTEXT_PRIORITY_URGENT))
        {
            priority = Priority.URGENT;
        }
        else if(priorityString.equals(DataManager.XMLTEXT_PRIORITY_HIGH))
        {
            priority = Priority.HIGH;
        }
        else if(priorityString.equals(DataManager.XMLTEXT_PRIORITY_MEDIUM))
        {
            priority = Priority.MEDIUM;
        }
        else
        {
            priority = Priority.LOW;
        }
    }


    /**
     * Populates the spinner priority_options with priority values mentioned in an string-array in strings.xml
     */
    public void populateSpinnerPriorityOptions() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.task_priority_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriorityOptions.setAdapter(adapter);
        spinnerPriorityOptions.setOnItemSelectedListener(this);
    }

    /**
     * validates the input entered by the user. Returns true if input is valid, else false.
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
     * Displays the orignal task data into the appropriate fields so that theuser can edit them
     */
    public void displayOrignalData()
    {
        editTextTitle.setText(title);
        editTextDescription.setText(description);
        switch(priority)
        {
            case URGENT:
                spinnerPriorityOptions.setSelection(0);
                break;
            case HIGH:
                spinnerPriorityOptions.setSelection(1);
                break;
            case MEDIUM:
                spinnerPriorityOptions.setSelection(2);
                break;
            default:
                spinnerPriorityOptions.setSelection(3);
        }
    }

    /**
     * Returns true if the user has modified the orignal task data, else returns false.
     * @return
     */
    public boolean userHasChangedData()
    {
        String newTitle=editTextTitle.getText().toString();
        String newDescription=editTextDescription.getText().toString();
        Priority newPriority;
        switch(spinnerPriorityOptions.getSelectedItemPosition())
        {
            case 0:
                newPriority=Priority.URGENT;
                break;
            case 1:
                newPriority=Priority.HIGH;
                break;
            case 2:
                newPriority=Priority.MEDIUM;
                break;
            default:
                newPriority=Priority.LOW;
                break;
        }
        if(newTitle.equals(title) && newDescription.equals(description) && newPriority==priority)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Updates data about the task in the xml file with the new data
     */
    public void updateTask()
    {
        String newTitle=editTextTitle.getText().toString();
        String newDescription=editTextDescription.getText().toString();
        Priority newPriority;
        switch(spinnerPriorityOptions.getSelectedItemPosition())
        {
            case 0:
                newPriority=Priority.URGENT;
                break;
            case 1:
                newPriority=Priority.HIGH;
                break;
            case 2:
                newPriority=Priority.MEDIUM;
                break;
            default:
                newPriority=Priority.LOW;
                break;
        }
        title=newTitle;
        description=newDescription;
        priority=newPriority;
        dataManager.updateTaskWithId(taskId, title, description, priority);
    }

    public void startDisplayTaskActivityWithTask()
    {
        Intent intent = new Intent(this, DisplayTaskActivity.class);
        intent.putExtra(DataManager.XMLTEXT_ID, taskId);
        intent.putExtra(DataManager.XMLTEXT_TITLE, title);
        intent.putExtra(DataManager.XMLTEXT_DESCRIPTION, description);
        switch (priority) {
            case URGENT:
                intent.putExtra(DataManager.XMLTEXT_PRIORITY, DataManager.XMLTEXT_PRIORITY_URGENT);
                break;
            case HIGH:
                intent.putExtra(DataManager.XMLTEXT_PRIORITY, DataManager.XMLTEXT_PRIORITY_HIGH);
                break;
            case MEDIUM:
                intent.putExtra(DataManager.XMLTEXT_PRIORITY, DataManager.XMLTEXT_PRIORITY_MEDIUM);
                break;
            default:
                intent.putExtra(DataManager.XMLTEXT_PRIORITY, DataManager.XMLTEXT_PRIORITY_LOW);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        if(userHasChangedData())
        {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setTitle("Exit?")
                    .setMessage("You have not saved the changes you made. Are you sure you to go back?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startDisplayTaskActivityWithTask();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        else
        {
            startDisplayTaskActivityWithTask();
        }
    }

}

