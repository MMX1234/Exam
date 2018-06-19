package com.example.exam.gson;

import com.google.gson.annotations.SerializedName;

public class QAList {
    @SerializedName("question")
    public String question;

    @SerializedName("option1")
    public String option1;

    @SerializedName("option2")
    public String option2;

    @SerializedName("option3")
    public String option3;

    @SerializedName("option4")
    public String option4;

    @SerializedName("answer")
    public String answer;

    @SerializedName("explain")
    public String explain;

    @SerializedName("pic")
    public String pic;

    @SerializedName("type")
    public String type;

    @SerializedName("chapter")
    public String chapter;
}
