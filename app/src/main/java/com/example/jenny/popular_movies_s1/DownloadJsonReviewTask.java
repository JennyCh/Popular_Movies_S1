package com.example.jenny.popular_movies_s1;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.jenny.popular_movies_s1.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Jenny on 10/25/2015.
 */
public class DownloadJsonReviewTask extends AsyncTask <String, Void, Void> {

    private final Context mContext;
    private String reviewID;

    private final static String LOG_TAG = "DownloadJsonReviewTask";

    public DownloadJsonReviewTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String reviewJsonStr = null;

        reviewID = params[0];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://api.themoviedb.org/3/movie/");
        stringBuilder.append(reviewID);
        stringBuilder.append("/reviews?api_key=---------------------------------");


        try{
            URL url = new URL(stringBuilder.toString());

            urlConnection  =  (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect(); // cannot be done on the main thread

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null){
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }
            if(buffer.length() == 0){
                return null;
            }
            reviewJsonStr = buffer.toString();

            Log.d("DownloadJsonDataTask", "BEGIN INSERT");
            Log.v("DownloadJsonDataTask", reviewJsonStr);
            getReviewDataFromJson(reviewJsonStr);

        }catch(IOException e){
            Log.e(LOG_TAG, "Error", e);
            return null;
        }catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(),e);
            e.printStackTrace();
        }
        finally {
            if(urlConnection  != null){
                urlConnection.disconnect();
            }
            if(reader  != null){
                try{
                    reader.close();
                }catch(final IOException e){
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    private void getReviewDataFromJson (String movieJsonString) throws JSONException{
        final String OBJECT = "object";
        final String RESULTS = "results";

        final String UNIQUE_ID = "id";
        final String AUTHOR = "author";
        final String CONTENT = "content";


        //movies = new ArrayList<Movie>();
        try {
            JSONObject moviesObj = new JSONObject(movieJsonString);
            JSONArray jsonArray = moviesObj.getJSONArray(RESULTS);
            //

            Vector<ContentValues> reviewVector = new Vector<>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {

                ContentValues reviewValues = new ContentValues();

                JSONObject object = jsonArray.getJSONObject(i);

                String uniqueID = object.getString(UNIQUE_ID);
                String author = object.getString(AUTHOR);
                String content = object.getString(CONTENT);


                reviewValues.put(MovieContract.Review._ID, reviewID);
                reviewValues.put(MovieContract.Review.UNIQUE_REVIEW_ID, uniqueID);
                reviewValues.put(MovieContract.Review.AUTHOR, author);
                reviewValues.put(MovieContract.Review.CONTENT, content);

                reviewVector.add(reviewValues);

            }
            Log.d(LOG_TAG, "BEFORE INSERT");
            int inserted = 0;
            if (reviewVector.size() > 0) {
                ContentValues[] reviewArray = new ContentValues[reviewVector.size()];
                reviewVector.toArray(reviewArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.Review.CONTENT_URI, reviewArray);
            }
            Log.d(LOG_TAG, "Complete " + inserted + " inserted");
        }catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }
}
