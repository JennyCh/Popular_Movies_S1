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
public class DownloadJsonTrailerTask extends AsyncTask<String, Void, Void> {
    private final Context mContext;
    private String movieID;

    private final static String LOG_TAG = "DownloadJsonTrailerTask";

    public DownloadJsonTrailerTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String reviewJsonStr = null;

        movieID = params[0];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://api.themoviedb.org/3/movie/");
        stringBuilder.append(movieID);
        stringBuilder.append("/videos?api_key=25fce2cd7e460dfabda689d0ebfcf69f");


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
        final String KEY = "key";
        final String NAME = "name";
        final String SIZE = "size";
        final String TYPE = "type";


        //movies = new ArrayList<Movie>();
        try {
            JSONObject moviesObj = new JSONObject(movieJsonString);
            JSONArray jsonArray = moviesObj.getJSONArray(RESULTS);
            //

            Vector<ContentValues> trailerVector = new Vector<>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {

                ContentValues trailerValues = new ContentValues();

                JSONObject object = jsonArray.getJSONObject(i);

                int uniqueID = object.getInt(UNIQUE_ID);
                String key = object.getString(KEY);
                String name = object.getString(NAME);
                String size = object.getString(SIZE);
                String type = object.getString(TYPE);


                trailerValues.put(MovieContract.Trailer._ID, movieID);
                trailerValues.put(MovieContract.Trailer.UNIQUE_TRAILER_ID, uniqueID);
                trailerValues.put(MovieContract.Trailer.KEY, key);
                trailerValues.put(MovieContract.Trailer.NAME, name);
                trailerValues.put(MovieContract.Trailer.SIZE, size);
                trailerValues.put(MovieContract.Trailer.TYPE, type);

                trailerVector.add(trailerValues);

            }
            Log.d(LOG_TAG, "BEFORE INSERT");
            int inserted = 0;
            if (trailerVector.size() > 0) {
                ContentValues[] movieArray = new ContentValues[trailerVector.size()];
                trailerVector.toArray(movieArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.Movie.CONTENT_URI, movieArray);
            }
            Log.d(LOG_TAG, "Complete " + inserted + " inserted");
        }catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }
}
