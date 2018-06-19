package com.example.exam.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(address)
                .addHeader("Authorization", "APPCODE " + "fb56eac416a14ec4b8564a794da0561d")
                .build();
        client.newCall(request).enqueue(callback);
    }
}
