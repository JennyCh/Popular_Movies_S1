package com.example.jenny.popular_movies_s1;

/**
 * Created by Jenny on 11/1/2015.
 */
public class Trailer {
    private String name;
    private String type;
    private String size;
    private String key;

    public Trailer(String name, String type, String size, String key) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "Trailer{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", size='" + size + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
