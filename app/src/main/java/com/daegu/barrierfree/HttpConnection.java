package com.daegu.barrierfree;

import android.util.Log;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpConnection {

    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();
    public static HttpConnection getInstance() {
        return instance;
    }

    private HttpConnection(){ this.client = new OkHttpClient(); }


    /** 웹 서버로 요청을 한다. */
    public void requestStation(String key, double x, double y, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .build();
        Request request = new Request.Builder()
                .url("http://swopenAPI.seoul.go.kr/api/subway/" + key + "/json/nearBy/0/1/" + x + "/" + y)
                .build();
        client.newCall(request).enqueue(callback);
        Log.i("tqtqtq", ""+ "http://swopenAPI.seoul.go.kr/api/subway/" + key + "/json/nearBy/0/1/" + x + "/" + y);
    }

}