package me.vijitdhingra.tasklist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TasksFragment extends Fragment implements AdapterView.OnItemClickListener{

    private View rootView;
    private ArrayList<Task> listTasks;
    private DataManager dataManager;
    private ListView listViewTasks;
    private TaskListAdapter listViewAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        rootView= getView();
        dataManager = new DataManager(getActivity());
        listTasks = dataManager.getListOfTasks();
        listViewTasks = (ListView) rootView.findViewById(R.id.listViewTasks);
        listViewTasks.setOnItemClickListener(this);
        populateListView();
    }

    public void updateTaskList(ArrayList<Task> updatedList)
    {
        listTasks.clear();
        listTasks.addAll(updatedList);
        listViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id)
    {
        Task  task = (Task) listViewAdapter.getItem(position);
        Intent intent = new Intent(this.getActivity(),DisplayTaskActivity.class);
        intent.putExtra(DataManager.XMLTEXT_ID,task.getId());
        intent.putExtra(DataManager.XMLTEXT_TITLE,task.getTitle());
        intent.putExtra(DataManager.XMLTEXT_DESCRIPTION,task.getDescription());
        Priority taskPriority = task.getPriority();
        if(taskPriority==Priority.URGENT)
        {
            intent.putExtra(DataManager.XMLTEXT_PRIORITY,DataManager.XMLTEXT_PRIORITY_URGENT);
        }
        else if(taskPriority==Priority.HIGH)
        {
            intent.putExtra(DataManager.XMLTEXT_PRIORITY,DataManager.XMLTEXT_PRIORITY_HIGH);
        }
        else if(taskPriority==Priority.MEDIUM)
        {
            intent.putExtra(DataManager.XMLTEXT_PRIORITY,DataManager.XMLTEXT_PRIORITY_MEDIUM);
        }
        else
        {
            intent.putExtra(DataManager.XMLTEXT_PRIORITY,DataManager.XMLTEXT_PRIORITY_LOW);
        }
        startActivity(intent);
    }

    /**
     * Assigns the adapter to the listView to populate it, but if listTasks is empty then sets the empty view for ListView.
     */
    public void populateListView()
    {
        if(listTasks.isEmpty())
        {
            TextView emptyTextView = (TextView) rootView.findViewById(R.id.textViewEmptyText);
            emptyTextView.setVisibility(View.VISIBLE);
            listViewTasks.setEmptyView(emptyTextView);
        }
        else
        {
            //Toast.makeText(getActivity(), listTasks.size()+" tasks exist", Toast.LENGTH_SHORT).show();
            listViewAdapter = new TaskListAdapter(listTasks);
            listViewTasks.setAdapter(listViewAdapter);
        }
    }

    public class TaskListAdapter extends BaseAdapter
    {
        List<Task> listOfTasks;
        public TaskListAdapter(List<Task> list)
        {
            listOfTasks=list;
        }
        @Override
        public int getCount()
        {
            return listOfTasks.size();
        }
        @Override
        public Object getItem(int position)
        {
            return listOfTasks.get(position);
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if(convertView==null)
            {
                LayoutInflater layoutInflater = TasksFragment.this.getActivity().getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.task_list_item,parent,false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.listItem_textView);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.listItem_imageView);
            String imageText;
            Task task = listOfTasks.get(position);

                textView.setText(task.getTitle());
                Priority taskPriority = task.getPriority();
                //imageText = String.valueOf(taskEvent.task.getTitle().charAt(0)).toUpperCase();
                TextDrawable textDrawable;
                if (taskPriority == Priority.URGENT)
                {
                    imageText="U";
                    textDrawable=TextDrawable.builder().beginConfig().bold().endConfig().buildRound(imageText, ContextCompat.getColor(TasksFragment.this.getActivity(),R.color.priority_urgent_color));
                    imageView.setImageDrawable(textDrawable);
                }
                else if (taskPriority == Priority.HIGH)
                {
                    imageText="H";
                    textDrawable=TextDrawable.builder().beginConfig().bold().endConfig().buildRound(imageText, ContextCompat.getColor(TasksFragment.this.getActivity(), R.color.priority_high_color));
                    imageView.setImageDrawable(textDrawable);
                }
                else if (taskPriority == Priority.MEDIUM)
                {
                    imageText="M";
                    textDrawable=TextDrawable.builder().beginConfig().bold().endConfig().buildRound(imageText, ContextCompat.getColor(TasksFragment.this.getActivity(),R.color.priority_medium_color));
                    imageView.setImageDrawable(textDrawable);
                }
                else
                {
                    imageText="L";
                    textDrawable=TextDrawable.builder().beginConfig().bold().endConfig().buildRound(imageText, ContextCompat.getColor(TasksFragment.this.getActivity(), R.color.priority_low_color));
                    imageView.setImageDrawable(textDrawable);
                }
            return  convertView;
        }
    }
}
