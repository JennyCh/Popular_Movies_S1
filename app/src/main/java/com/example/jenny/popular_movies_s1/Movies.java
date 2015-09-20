package com.example.jenny.popular_movies_s1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jenny on 9/11/2015.
 */
public class Movies {

    private List<Movie> movies;

    public Movies (List<Movie> movies){
        for (Movie m: movies){
            this.movies.add(m);
        }
    }

    public List<Movie> getMoviesArray(){

        List <Movie> deepCopy = new ArrayList<Movie>();

        for(Movie m: movies){
            deepCopy.add(m);
        }

        return deepCopy;
    }
}
