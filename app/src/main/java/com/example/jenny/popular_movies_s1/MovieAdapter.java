package com.example.jenny.popular_movies_s1;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jenny.popular_movies_s1.data.MovieContract;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;


/**
 * Created by Jenny on 10/25/2015.
 */
public class MovieAdapter extends CursorAdapter {

    private static final String LOG_TAG = "MovieAdapter";
    private Context context;

   //private static final int VIEW_TYPE_IMAGE = 0;
    //private static final int VIEW_TYPE_REVIEW = 1;

    //int layoutID;

    public MovieAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
        this.context = context;
        //Log.v(LOG_TAG, "constructor");

    }

    @Override
    public int getViewTypeCount() {
        //return super.getViewTypeCount();
        return 1;
    }



    @Override
    public int getItemViewType(int position) {

        //return super.getItemViewType(position);
        return 1;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Log.v(LOG_TAG, "new view");
        //int viewType = getItemViewType(cursor.getPosition());
        //layoutID = -1;




        //View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);

        //return view;

        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent,false);
        ViewHolder viewHolder = new ViewHolder(view, 0);
        view.setTag(viewHolder);
        return view;
    }

    public static class ViewHolder{
        public ImageView imageItemView;

        public TextView reviewAuthorView;
        public TextView reviewContentView;

        public ViewHolder(View view, int layoutID){
                imageItemView = (ImageView) view.findViewById(R.id.image_item);

        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();




            //Log.v(LOG_TAG, "bind view");
            // TextView textView = (TextView) view;
            //ImageView imageView = (ImageView) view;
            String data = formatData(cursor);
            // Log.v(LOG_TAG, data);


            String baseUrl = "http://image.tmdb.org/t/p/w342";
            String url = baseUrl + data;

        Picasso.with(context).setIndicatorsEnabled(true);
        if (isNetworkConnected()) {
            Log.v("MovieAdapter" , "YES INTERNET");
            Picasso.with(context).load(url.toString()).networkPolicy(NetworkPolicy.NO_CACHE).into(viewHolder.imageItemView);
        }else {
            Log.v("MovieAdapter" , "NO INTERNET");
            Picasso.with(context)
                    .load(url.toString())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(viewHolder.imageItemView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            //Try again online if cache failed
                      /*  Picasso.with(context)
                                .load(posts.get(position).getImageUrl())
                                .error(R.drawable.header)
                                .into(viewHolder.imageItemView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso", "Could not fetch image");
                                    }
                                });*/
                        }
                    });


        }


        //textView.setText(formatData(cursor));
    }

   /* public class Global extends Application {
        @Override
        public void onCreate() {
            super.onCreate();

            Picasso.Builder builder = new Picasso.Builder(this);
            builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
            Picasso built = builder.build();
            built.setIndicatorsEnabled(true);
            built.setLoggingEnabled(true);
            Picasso.setSingletonInstance(built);

        }
    }*/

    private String formatData(Cursor cursor){
        int poster_path = cursor.getColumnIndex(MovieContract.Movie.POSTER_PATH);
        int title = cursor.getColumnIndex(MovieContract.Movie.TITLE);
        return cursor.getString(poster_path);
    }
}
