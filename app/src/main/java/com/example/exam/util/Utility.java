package com.example.exam.util;

import com.example.exam.gson.Result;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    public static Result handleResultResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONObject resultJObject = jsonObject.getJSONObject("result");
            String resultContent = resultJObject.toString();

            return new Gson().fromJson(resultContent, Result.class);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
