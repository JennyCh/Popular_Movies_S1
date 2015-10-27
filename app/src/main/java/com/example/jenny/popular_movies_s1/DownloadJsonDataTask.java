package com.example.jenny.popular_movies_s1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Jenny on 10/21/2015.
 */
public class DownloadJsonDataTask extends AsyncTask<String,Void,Void> {
    private final Context mContext;
    private String sortType;

    public DownloadJsonDataTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;

        sortType = params[0];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://api.themoviedb.org/3/discover/movie?sort_by=");
        stringBuilder.append(sortType);
        stringBuilder.append(".desc&api_key=25fce2cd7e460dfabda689d0ebfcf69f");


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
            movieJsonStr = buffer.toString();

            Log.d("DownloadJsonDataTask", "BEGIN INSERT");
            Log.v("DownloadJsonDataTask", movieJsonStr);
            getMovieDataFromJson(movieJsonStr);

        }catch(IOException e){
            Log.e("MainFragment ", "Error", e);
            return null;
        }catch(JSONException e){
            Log.e("DownloadJsonDataTask", e.getMessage(),e);
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
                    Log.e("MainFragment ", "Error closing stream", e);
                }
            }
        }
        return null;
    }

    private void getMovieDataFromJson (String movieJsonString) throws JSONException{

        final String RESULTS = "results";
        final String ID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String OBJECT = "object";
        final String PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String VOTE_COUNT = "vote_count";

        //movies = new ArrayList<Movie>();

        /*
        Temp solution so not to run into PK constraint
         *//*
        mContext.getContentResolver().delete(MovieContract.Trailer.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MovieContract.Review.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MovieContract.Movie.CONTENT_URI, null, null);*/

        Cursor cursor = mContext.getContentResolver().query(MovieContract.Movie.CONTENT_URI, new String[]{MovieContract.Movie._ID}, null,null,MovieContract.Movie._ID);

        int idColumn = cursor.getColumnIndex(MovieContract.Movie._ID);
        List<Integer> idList = new ArrayList<>();
        while(cursor.moveToNext()){
            idList.add(cursor.getInt(idColumn));
        }

        boolean exists = false;




            try {
                JSONObject moviesObj = new JSONObject(movieJsonString);
                JSONArray jsonArray = moviesObj.getJSONArray(RESULTS);
                //

                Vector<ContentValues> movieVector = new Vector<>(jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {

                    ContentValues movieValues = new ContentValues();

                    JSONObject object = jsonArray.getJSONObject(i);
                    int id = object.getInt(ID);

                    for (int j = 0; j < idList.size(); j++) {
                        if (idList.get(j) == id) {
                            exists = true;
                            idList.remove(j);
                        }
                    }
                    if (!exists) {
                        String title = object.getString(ORIGINAL_TITLE);
                        String path = object.getString(PATH);
                        String overview = object.getString(OVERVIEW);
                        double vote = object.getDouble(VOTE_AVERAGE);
                        String releaseDate = object.getString(RELEASE_DATE);
                        int voteCount = object.getInt(VOTE_COUNT);
                        int sort;

                        if ("popularity".equals(sortType)) {
                            sort = 1;
                        } else if ("vote_average".equals(sortType)) {
                            sort = 2;
                        } else {
                            sort = -1;
                        }


                        movieValues.put(MovieContract.Movie._ID, id);
                        movieValues.put(MovieContract.Movie.TITLE, title);
                        movieValues.put(MovieContract.Movie.POSTER_PATH, path);
                        movieValues.put(MovieContract.Movie.OVERVIEW, overview);
                        movieValues.put(MovieContract.Movie.VOTE_AVERAGE, vote);
                        movieValues.put(MovieContract.Movie.RELEASE_DATE, releaseDate);
                        movieValues.put(MovieContract.Movie.VOTE_COUNT, voteCount);
                        movieValues.put(MovieContract.Movie.SORT_TYPE, sort);
                        movieVector.add(movieValues);

                        /*
                        Load Reviews and Trailers for each movie
                         */
                        DownloadJsonReviewTask downloadReview = new DownloadJsonReviewTask(mContext);
                        downloadReview.execute(String.valueOf(id));

                        DownloadJsonTrailerTask trailerReview = new DownloadJsonTrailerTask(mContext);
                        trailerReview.execute(String.valueOf(id));


                        Log.d("DownloadJsonDataTask", "BEFORE INSERT");
                        int inserted = 0;
                        if (movieVector.size() > 0) {
                            ContentValues[] movieArray = new ContentValues[movieVector.size()];
                            movieVector.toArray(movieArray);
                            inserted = mContext.getContentResolver().bulkInsert(MovieContract.Movie.CONTENT_URI, movieArray);
                        }


                        Log.d("DownloadJsonDataTask", "Complete " + inserted + " inserted");
                    }
                }
            } catch (JSONException e) {
                Log.e("DownloadJsonDataTask", e.getMessage(), e);
                e.printStackTrace();
            }

    }
}
