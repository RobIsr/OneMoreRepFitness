package com.example.israelsson.onemorerepfitness.model;

/**
 * Created by israe on 2017-09-20.
 */

public class Results {
    String time;
    String date;

    public Results() {

    }

    public Results(String time, String date) {
        this.time = time;
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}
