package me.vijitdhingra.tasklist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayTaskActivity extends AppCompatActivity {

    private int taskId;
    private String title,description;
    private Priority priority;
    private DataManager dataManager;
    CardView cardViewTask;
    TextView textViewTitle;
    TextView textViewDescription;
    TextView textViewPriority;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_task);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        getViews();
        DisplayHelper.adjustCardViewDisplay(cardViewTask);
        dataManager = new DataManager(this);
        getDataFromIntent();
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
            Intent intent = new Intent(this,EditTaskActivity.class);
            intent.putExtra(DataManager.XMLTEXT_ID,taskId);
            intent.putExtra(DataManager.XMLTEXT_TITLE,title);
            intent.putExtra(DataManager.XMLTEXT_DESCRIPTION,description);
            switch (priority)
            {
                case URGENT:
                    intent.putExtra(DataManager.XMLTEXT_PRIORITY,DataManager.XMLTEXT_PRIORITY_URGENT);
                    break;
                case HIGH:
                    intent.putExtra(DataManager.XMLTEXT_PRIORITY,DataManager.XMLTEXT_PRIORITY_HIGH);
                    break;
                case MEDIUM:
                    intent.putExtra(DataManager.XMLTEXT_PRIORITY,DataManager.XMLTEXT_PRIORITY_MEDIUM);
                    break;
                default:
                    intent.putExtra(DataManager.XMLTEXT_PRIORITY,DataManager.XMLTEXT_PRIORITY_LOW);
            }
            startActivity(intent);
            return true;
        }
        else if(id==R.id.action_delete)
        {

            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete?")
                    .setMessage("Are you sure you want to delete this Task?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dataManager.deleteTaskWithID(taskId);
                            Toast.makeText(DisplayTaskActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DisplayTaskActivity.this,TasklistActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getViews()
    {
        cardViewTask = (CardView) findViewById(R.id.cardViewDisplayTask);
        textViewTitle = (TextView) findViewById(R.id.textViewDisplayTaskTitle);
        textViewDescription = (TextView) findViewById(R.id.textViewDisplayTaskDescription);
        textViewPriority = (TextView) findViewById(R.id.textViewDisplayTaskPriority);
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
     * Displays task data into the appropriate text views in layout.
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
            textViewDescription.setText("No description has been set for this task.");
        }
        switch(priority)
        {
            case URGENT:
                textViewPriority.setText("Urgent");
                cardViewTask.setCardBackgroundColor(ContextCompat.getColor(this,R.color.priority_urgent_color));
                textViewPriority.setTextColor(ContextCompat.getColor(this,R.color.priority_urgent_color));
                break;
            case HIGH:
                textViewPriority.setText("High");
                cardViewTask.setCardBackgroundColor(ContextCompat.getColor(this, R.color.priority_high_color));
                textViewPriority.setTextColor(ContextCompat.getColor(this, R.color.priority_high_color));
                break;
            case MEDIUM:
                textViewPriority.setText("Medium");
                cardViewTask.setCardBackgroundColor(ContextCompat.getColor(this, R.color.priority_medium_color));
                textViewPriority.setTextColor(ContextCompat.getColor(this, R.color.priority_medium_color));
                break;
            default:
                textViewPriority.setText("Low");
                cardViewTask.setCardBackgroundColor(ContextCompat.getColor(this, R.color.priority_low_color));
                textViewPriority.setTextColor(ContextCompat.getColor(this, R.color.priority_low_color));
        }

    }


}
