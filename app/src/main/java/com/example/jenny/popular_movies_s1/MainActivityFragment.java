package com.example.jenny.popular_movies_s1;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.os.Handler;
import java.util.logging.LogRecord;
import java.util.prefs.PreferenceChangeEvent;
import java.lang.Runnable;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    //ArrayAdapter<String> arrayAdapter;
    GridView  gridView;
    ImageAdapter imageAdapter;
    public List <Movie> movies;

    private String sortTypeSaved;
    private ProgressBar progressBar;

    public MainActivityFragment() {
        this.sortTypeSaved = "";



    }
    @Override
    public void onAttach(Context context) {
        Log.v("Activity", "Attached");
        super.onAttach(context);
    }


    String [] array1 = new String[20];

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", (ArrayList<Movie>) movies);
        super.onSaveInstanceState(outState);


        Log.v("onSaveInstanceState ", "SAVING STATE");
        Log.v("Saving State", outState.toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("MainFragment ", "RESUMED");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("MainFragment ", "STOPPED");
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("MainFragment ", "DESTROYED");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.fragment_main, null);
        this.progressBar = (ProgressBar)v.findViewById(R.id.progress_bar);

        Log.v("PROGRESS_BAR", String.valueOf(progressBar.hashCode()));

        if (savedInstanceState != null){
            Log.v("onCreate ", "EXISTING STATE");
            this.movies = savedInstanceState.getParcelableArrayList("movies");
        }else{
            Log.v("onCreate ", "EMPTY STATE");
        }

        setHasOptionsMenu(true);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /* int id = item.getItemId();
        if(id == R.id.action_refresh){
            DownloadJsonDataTask jsonDataTask = new DownloadJsonDataTask();
            jsonDataTask.execute();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView =  (GridView) rootView.findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(getActivity());
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = movies.get(position).getTitle();
                String overview = movies.get(position).getOverview();
                String date = movies.get(position).getReleaseDate();
                String path = movies.get(position).getPath();
                double vote = movies.get(position).getVote();

                if (title == "null") {
                    title = "Title is not available";
                }
                if (overview == "null") {
                    overview = "Overview is not available";
                }
                if (date == "null") {
                    date = "Release date is not available";
                }


                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TITLE", title);
                extras.putString("OVERVIEW", overview);
                extras.putString("DATE", date);
                extras.putDouble("VOTE", vote);
                extras.putString("PATH", path);
                /*
                Is it a good idea to pass ArrayList this way so only it can be saved on InstanceState?
                 */
                //extras.putParcelableArrayList("movies", (ArrayList<Movie>) movies);

                intent.putExtras(extras);
                startActivity(intent);
            }
        });

      /*  View rootView = inflater.inflate(R.layout.fragment_main, container,false);
         Integer[] array = {
                R.drawable.sample_1, R.drawable.sample_2,
                R.drawable.sample_3, R.drawable.sample_4,

        };
        List<Integer> al = new ArrayList<Integer>(Arrays.asList(array));

        arrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.item_layout, R.id.image_item,al);
        GridView grid = (GridView) rootView.findViewById(R.id.gridview);
        grid.setAdapter(arrayAdapter);

*/
        return rootView;
    }

    public class ImageAdapter extends BaseAdapter{
        private Context mContext;
        private StringBuilder url;


        public ImageAdapter (Context c){
            mContext = c;
            url = new StringBuilder();
        }
        @Override
        public int getCount() {
            return array1.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null){
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 300));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(2, 2, 2, 2);
            }else{
                imageView = (ImageView) convertView;
            }
            final float scale = getResources().getDisplayMetrics().density;


            if (position < array1.length) {
                //Log.v("Array Position ", "http://image.tmdb.org/t/p/w185/"+String.valueOf(array1[position]));

                if (array1[position] != null) {
//Log.v("Position ", array1[position]);
                    String base = "http://image.tmdb.org/t/p/w185/";
                    String link = "";
                    url.delete(0, url.length());
                    if (array1[position] == null){

                        url.append("android.resource://com.example.jenny.popular_movies_s1/");
                        url.append(R.drawable.no_poster);
                        Log.v("LINK ", url.toString());
                    }else{

                        link = array1[position];
                        url.append(base);
                        url.append(link);
                        Log.v("LINK ", url.toString());
                    }

                    Picasso.with(mContext).load(url.toString()).into(imageView);
                    //imageView.setImageResource(array[position]);

                }
            }
            return imageView;
        }
    }


    private void update(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

        if (sortType.equals(sortTypeSaved)){
            Log.v("Equals", "YAY");
            Log.v(sortType.toString(), sortTypeSaved.toString());

            if (movies == null || movies.isEmpty()){

                Log.v("movies ARRAY", "EMPTY");
                DownloadJsonDataTask asyncDownload = new DownloadJsonDataTask();
                asyncDownload.execute(sortType);
            }else{
                Log.v("Equals", "NOPE");
                Log.v("movies ARRAY", " not EMPTY");
                updateUI();
            }
        }else{
            Log.v(sortType.toString(), sortTypeSaved.toString());
           // if (movies == null || movies.isEmpty()){
            DownloadJsonDataTask asyncDownload = new DownloadJsonDataTask();
                asyncDownload.execute(sortType);
          //  }else{
           // }
        }

      /*  if (movies == null || movies.isEmpty()){
            asyncDownload.execute(sortType);
        }else{
            updateUI();
        }*/
        sortTypeSaved = sortType;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("onStart ", "START");
        //Log.v("Movies Size ", String.valueOf(movies.size()));
                update();
    }

    public class DownloadJsonDataTask extends AsyncTask <String,Void,List<Movie>>{



        @Override
        protected List<Movie> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String rawJsonStr = null;
            Log.v("AsyncTask JSON ", "GETTING JSON");
            String sortType = params[0];
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://api.themoviedb.org/3/discover/movie?sort_by=");
            stringBuilder.append(sortType);
            stringBuilder.append(".desc&api_key=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");


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
                rawJsonStr = buffer.toString();

            }catch(IOException e){
                Log.e("MainFragment ", "Error", e);
                return null;
            }finally {
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
            try{
                return getJsonDataArray(rawJsonStr);
            }catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(List<Movie> movies) {
            updateUI();
        }
    }
    public void updateUI(){
       if(movies != null) {
           for (int i = 0; i < array1.length; i++) {
               //Log.v("PATH in array1 " , movies.get(i).getPath());
               array1[i] = (movies.get(i).getPath());
           }

           gridView.setAdapter(imageAdapter);


       }else{
           Log.v("MOVIE ARRAY", "- I HAVE NO DATA");
       }

        Log.v("HIDE", "BEFORE");
        Log.v("PROGRESS_BAR", String.valueOf(progressBar.hashCode()));
        progressBar.setVisibility(View.GONE);

        Log.v("HIDE", "AFTER");

    }
    private List<Movie> getJsonDataArray(String rawJsonStr) throws JSONException{
        /*  original title
            movie poster image thumbnail
            A plot synopsis (called overview in the api)
            user rating (called vote_average in the api)
            release date
        */

        final String RESULTS = "results";
        final String ORIGINAL_TITLE = "original_title";
        final String OBJECT = "object";
        final String PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";

        movies = new ArrayList<Movie>();

        JSONObject moviesObj = new JSONObject(rawJsonStr);
        JSONArray jsonArray = moviesObj.getJSONArray(RESULTS);
        //Log.v("ArrayLength " , String.valueOf(jsonArray.length()));

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String title = object.getString(ORIGINAL_TITLE);
            String path = object.getString(PATH);
            String overview = object.getString(OVERVIEW);
            double vote = object.getDouble(VOTE_AVERAGE);
            String releaseDate = object.getString(RELEASE_DATE);
            movies.add(new Movie(title, overview, vote, releaseDate, path));

        }

        return movies;
    }
}