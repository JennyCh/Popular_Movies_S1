package com.example.jenny.popular_movies_s1;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jenny on 11/1/2015.
 */
public class TrailerAdapter extends BaseAdapter {

    private List<Trailer> trailer;
    private LayoutInflater inflater;
    private Activity activity;

    public TrailerAdapter(Activity activity, List<Trailer> trailer){
        this.trailer = trailer;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount()
    {
        return trailer.size();
    }

    public Object getItem(int position)
    {
        return position;
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View view = convertView;

        if (convertView == null){
            view = inflater.inflate(R.layout.trailer_item, null);

            TextView nameauthorView = (TextView) view.findViewById(R.id.trailerName);
            TextView qualityView = (TextView) view.findViewById(R.id.trailerQuality);

            nameauthorView.setText(trailer.get(position).getName());
            qualityView.setText(trailer.get(position).getSize());
        }
        return view;

    }
}
