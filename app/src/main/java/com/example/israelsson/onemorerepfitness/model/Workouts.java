package com.example.israelsson.onemorerepfitness.model;

/**
 * Created by israe on 2017-09-15.
 */

public class Workouts {
    private String description;
    private String exersize_1;
    private String exersize_2;
    private String exersize_3;
    private String exersize_4;

    public Workouts(){

    }

    public Workouts(String description, String exersize_1, String exersize_2, String exersize_3, String exersize_4) {
        this.description = description;
        this.exersize_1 = exersize_1;
        this.exersize_2 = exersize_2;
        this.exersize_3 = exersize_3;
        this.exersize_4 = exersize_4;
    }

    public String getDescription() {
        return this.description;
    }

    public String getExersize_1() {
        return this.exersize_1;
    }

    public String getExersize_2() {
        return this.exersize_2;
    }

    public String getExersize_3() {
        return this.exersize_3;
    }

    public String getExersize_4() {
        return this.exersize_4;
    }
}
