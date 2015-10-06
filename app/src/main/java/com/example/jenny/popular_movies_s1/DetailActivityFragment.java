package com.example.jenny.popular_movies_s1;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    ImageView imageView;
    TextView titleView;
    TextView overViewView;
    TextView voteView;
    TextView dateView;
    List<Movie> movies;

    public DetailActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        String path = intent.getStringExtra("PATH");

        String title = intent.getStringExtra("TITLE");
        String overview = intent.getStringExtra("OVERVIEW");
        String date = intent.getStringExtra("DATE");
        Double vote = intent.getDoubleExtra("VOTE", -1);




        String link = path;
        DownloadSingleImage asyncDownload = new DownloadSingleImage((ImageView) rootView.findViewById(R.id.detail_image));
        //Log.v("LINK", link.toString());
        asyncDownload.execute(link);
        titleView = (TextView) rootView.findViewById(R.id.detail_title);
        overViewView = (TextView) rootView.findViewById(R.id.detail_overview);
        dateView = (TextView) rootView.findViewById(R.id.detail_date);
        voteView = (TextView) rootView.findViewById(R.id.detail_vote);
   // }


        titleView.setText(title);
        overViewView.setText(overview);
        dateView.setText(date);
        voteView.setText(String.valueOf(vote));
        //}

        return rootView;
    }

    public class DownloadSingleImage extends AsyncTask<String,Void,Bitmap> {
        private ImageView btmImage;
private String url;
        public DownloadSingleImage(ImageView btmImage){
            this.btmImage = btmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            url = urls[0];
            Bitmap image = null;
            //Log.v("URL for iamge ", url);
            try{
                InputStream in = new URL(url).openStream();
                image = BitmapFactory.decodeStream(in);

            }catch(Exception e){
                e.printStackTrace();

            }

            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                btmImage.setImageBitmap(bitmap);
            }else{
                btmImage.setLayoutParams(new LinearLayout.LayoutParams(342, 460));
                btmImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                btmImage.setPadding(15, 15, 15, 15);
                Picasso.with(getContext()).load(url.toString()).into(btmImage);
            }
        }
    }


}
