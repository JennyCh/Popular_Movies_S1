package com.example.jenny.popular_movies_s1;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jenny on 10/29/2015.
 */
public class ReviewAdapter extends BaseAdapter {

    private List<Review> reviews;
    private LayoutInflater inflater;
    private Activity activity;

    public ReviewAdapter(Activity activity, List<Review> reviews){
        this.reviews = reviews;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount()
    {
        return reviews.size();
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
            view = inflater.inflate(R.layout.review_item, null);

            TextView authorView = (TextView) view.findViewById(R.id.reviewAuthor);
            TextView contentView = (TextView) view.findViewById(R.id.reviewContent);

            authorView.setText(reviews.get(position).getAuthor());
            contentView.setText(reviews.get(position).getContent());
        }
            return view;

    }
}
