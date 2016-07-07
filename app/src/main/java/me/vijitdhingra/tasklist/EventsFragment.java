package me.vijitdhingra.tasklist;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
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

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class EventsFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener{

    private View rootView;
    private ArrayList<Event> listEvents;
    private DataManager dataManager;
    private ListView listViewEvents;
    private EventListAdapter listViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        rootView= getView();
        dataManager = new DataManager(getActivity());
        listEvents = dataManager.getListOfEvents();
        //Collections.sort(listEvents,new DataManager.ComparatorEventDateTime());
        listViewEvents = (ListView) rootView.findViewById(R.id.listViewEvents);
        listViewEvents.setOnItemClickListener(this);
        populateListView();
    }

    public void updateEventList(ArrayList<Event> updatedList)
    {
        listEvents.clear();
        listEvents.addAll(updatedList);
        listViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id)
    {
        Event  event = (Event) listViewAdapter.getItem(position);
        startActivityDisplayEvent(event);
    }

    /**
     * Starts activity displayEvent to display event by passing the necessary event data through intent.
     * @param event
     */
    public void startActivityDisplayEvent(Event event)
    {
        Intent intent = new Intent(this.getActivity(),DisplayEventActivity.class);
        EventHelper.addEventDataToIntentExtras(event,intent);
        startActivity(intent);
    }

    /**
     * Assigns the adapter to the listView to populate it, but if listTasks is empty then sets the empty view for ListView.
     */
    public void populateListView()
    {
        if(listEvents.isEmpty())
        {
            TextView emptyTextView = (TextView) rootView.findViewById(R.id.textViewEmptyText);
            emptyTextView.setVisibility(View.VISIBLE);
            listViewEvents.setEmptyView(emptyTextView);
        }
        else
        {
            listViewAdapter = new EventListAdapter(listEvents);
            listViewEvents.setAdapter(listViewAdapter);
        }
    }


    public class EventListAdapter extends BaseAdapter
    {
        List<Event> listOfEvents;
        public EventListAdapter(List<Event> list)
        {
            listOfEvents=list;
        }
        @Override
        public int getCount()
        {
            return listOfEvents.size();
        }
        @Override
        public Object getItem(int position)
        {
            return listOfEvents.get(position);
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
                LayoutInflater layoutInflater = EventsFragment.this.getActivity().getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.task_list_item,parent,false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.listItem_textView);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.listItem_imageView);
            String imageText;
            Event event= listOfEvents.get(position);

            textView.setText(event.getTitle());
            TextDrawable textDrawable;
            imageText="E";
            if(event.getEventDateTime().compareTo(Calendar.getInstance())<0)
            {
                //case when event has passed
                textDrawable=TextDrawable.builder().beginConfig().bold().endConfig().buildRound(imageText, ContextCompat.getColor(EventsFragment.this.getActivity(), R.color.event_passed_color));
            }
            else
            {
                textDrawable = TextDrawable.builder().beginConfig().bold().endConfig().buildRound(imageText, ContextCompat.getColor(EventsFragment.this.getActivity(), R.color.event_color));
            }
            imageView.setImageDrawable(textDrawable);
            return  convertView;
        }
    }
}
