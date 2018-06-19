package com.example.exam.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {
    @SerializedName("list")
    public List<QAList> qaLists;
}
