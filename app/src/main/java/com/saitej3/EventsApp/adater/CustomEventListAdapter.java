package com.saitej3.EventsApp.adater;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.saitej3.EventsApp.R;
import com.saitej3.EventsApp.app.AppController;
import com.saitej3.EventsApp.model.Event;

import java.util.List;

/**
 * Created by Sai Teja on 10/22/2015.
 */
public class CustomEventListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Event> eventItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomEventListAdapter(Activity activity, List<Event> eventItems)
    {
        this.activity=activity;
        this.eventItems=eventItems;
    }


    @Override
    public int getCount() {
        return eventItems.size();
    }


    @Override
    public Object getItem(int location) {
        return eventItems.get(location);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_event_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();


        TextView eventName = (TextView) convertView.findViewById(R.id.eventName);
        TextView eventTime = (TextView) convertView.findViewById(R.id.eventTime);
        TextView eventDesc = (TextView) convertView.findViewById(R.id.eventDesc);
        NetworkImageView imageIcon= (NetworkImageView) convertView.findViewById(R.id.imageIcon);


        // getting movie data for the row
        Event e = eventItems.get(position);



        eventName.setText(e.getEventName());
        eventDesc.setText(e.getEventDesc());
        eventTime.setText(e.getEventTime());
        imageIcon.setImageUrl(e.getImagePath(),imageLoader);
        return convertView;
    }
}
