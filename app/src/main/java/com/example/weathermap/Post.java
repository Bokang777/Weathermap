package com.example.weathermap;

import com.google.gson.annotations.SerializedName;

public class Post {

    private int icon;

    private int id;

    private String main;

    @SerializedName("description")
    private String text;

    public int getIcon() {
        return icon;
    }

    public int getId() {
        return id;
    }

    public String getMain() {
        return main;
    }

    public String getText() {
        return text;
    }
}
