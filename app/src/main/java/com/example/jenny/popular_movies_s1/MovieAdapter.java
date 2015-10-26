package com.example.jenny.popular_movies_s1;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jenny.popular_movies_s1.data.MovieContract;

/**
 * Created by Jenny on 10/25/2015.
 */
public class MovieAdapter extends CursorAdapter {

    private static final String LOG_TAG = "MovieAdapter";

    public MovieAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
        Log.v(LOG_TAG, "constructor");

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(LOG_TAG, "new view");
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(LOG_TAG, "bind view");
        TextView textView = (TextView) view;
        String data = formatData(cursor);
        Log.v(LOG_TAG, data);
        textView.setText(formatData(cursor));
    }

    private String formatData(Cursor cursor){
        int poster_path = cursor.getColumnIndex(MovieContract.Movie.POSTER_PATH);
        int title = cursor.getColumnIndex(MovieContract.Movie.TITLE);
        return cursor.getString(title);
    }
}
