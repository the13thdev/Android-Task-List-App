package me.vijitdhingra.tasklist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class TasklistActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabsPagerAdapter tabsPagerAdapter;
    private TabLayout tabLayout;
    private TasksFragment taskFragment;
    private EventsFragment eventsFragment;
    private DataManager.SORT_ORDER tasksSortOrder,eventsSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        taskFragment = new TasksFragment();
        eventsFragment = new EventsFragment();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        setCurrentTab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tasklist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.action_create_new)
        {
            Intent intent;
            if(tabLayout.getSelectedTabPosition()==0)
            {
                /*If tasks tab selected then create task*/
                intent = new Intent(this,CreateTaskActivity.class);
            }
            else
            {
                /*If events tab selected then create event*/
                intent = new Intent(this,CreateEventActivity.class);
            }
            startActivity(intent);
        }
        else if(id==R.id.action_sort)
        {
            if(tabLayout.getSelectedTabPosition()==0)
            {
                /*If tasks tab selected*/
                new SortTasksDialogFragment().show(getFragmentManager(),"SortTasksDialogFragment");
            }
            else
            {
                /*If events tab selected*/
                new SortEventsDialogFragment().show(getFragmentManager(),"SortEventsDialogFragment");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void setCurrentTab()
    {
        Intent intent = getIntent();
        int tabPosition = intent.getIntExtra(DataManager.INTENT_EXTRA_TAB_POSITION,0);
        tabLayout.getTabAt(tabPosition).select();
    }

    /**
     * Adapter for ViewPager in TaskList. Supplies two fragments to the pager for displaying Tasks and Events respectively.
     */
    private class TabsPagerAdapter extends FragmentPagerAdapter{

        public TabsPagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int index)
        {
            switch(index)
            {
                case 0:
                    return taskFragment;
                case 1:
                    return eventsFragment;
            }
            return null;
        }

        @Override
        public int getCount()
        {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int pos)
        {
            if(pos==0)
            {
                return "Tasks";
            }
            else
            {
                return "Events";
            }
        }

    }

    public static class SortTasksDialogFragment extends DialogFragment
    {
        DataManager dataManager;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceStater)
        {
            dataManager = new DataManager(getActivity());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Sort Order");
            builder.setSingleChoiceItems(R.array.sort_order_tasks, getCurrentTasksSortOrderPos(dataManager.getTasksSortOrder()), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Following line of code is okay because this fragment is only usd in TaskListActivity
                    TasklistActivity currentActivity = (TasklistActivity) getActivity();
                    //Todo Error Prone  next line, Null Pointer Exception Sometimes
                    dataManager.updateTasksSortOrder(getSortOrderFromSelectedPos(which));
                    currentActivity.taskFragment.updateTaskList(dataManager.getListOfTasks());
                    SortTasksDialogFragment.this.dismiss();
                }
            });
            return builder.create();
        }

        private int getCurrentTasksSortOrderPos(DataManager.SORT_ORDER sort_order)
        {
            switch (sort_order)
            {
                case TASK_PRIORITY_DSC:
                    return 0;
                default:
                    return 1;
            }
        }

        private DataManager.SORT_ORDER getSortOrderFromSelectedPos(int pos)
        {
            switch (pos)
            {
                case 0:
                    return DataManager.SORT_ORDER.TASK_PRIORITY_DSC;
                default:
                    return DataManager.SORT_ORDER.TASK_PRIORITY_ASC;
            }
        }
    }

    public static class SortEventsDialogFragment extends DialogFragment
    {
        DataManager dataManager;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceStater)
        {
            dataManager = new DataManager(getActivity());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Sort Order");
            builder.setSingleChoiceItems(R.array.sort_order_events, getCurrentTasksSortOrderPos(dataManager.getEventsSortOrder()), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Following line of code is okay because this fragment is only usd in TaskListActivity
                    TasklistActivity currentActivity = (TasklistActivity) getActivity();
                    dataManager.updateEventsSortOrder(getSortOrderFromSelectedPos(which));
                    currentActivity.eventsFragment.updateEventList(dataManager.getListOfEvents());
                    SortEventsDialogFragment.this.dismiss();
                }
            });
            return builder.create();
        }

        private int getCurrentTasksSortOrderPos(DataManager.SORT_ORDER sort_order)
        {
            switch (sort_order)
            {
                case EVENT_DATETIME_DSC:
                    return 0;
                default:
                    return 1;
            }
        }

        private DataManager.SORT_ORDER getSortOrderFromSelectedPos(int pos)
        {
            switch (pos)
            {
                case 0:
                    return DataManager.SORT_ORDER.EVENT_DATETIME_DSC;
                default:
                    return DataManager.SORT_ORDER.EVENT_DATETIME_ASC;
            }
        }
    }
}
