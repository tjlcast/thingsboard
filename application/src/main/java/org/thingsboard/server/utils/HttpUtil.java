package org.thingsboard.server.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/10.
 */
public class HttpUtil {
    static final private String baseUrl = "http://10.108.218.64:8080/service-repository";
    static final private OkHttpClient httpClient = new OkHttpClient();
     static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static JsonObject getDeviceShadowDoc(String manufactory,String deviceType,String model){
        String url = baseUrl+"/"+manufactory+"/"+deviceType+"/"+model+".json";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = httpClient.newCall(request);
        try {
            Response response = call.execute();
            return new JsonParser().parse(response.body().toString()).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonObject();
    }

    public static Response  sendGet(){
        return null;
    }

    public static Response  sendPost(String url,String json){

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try{
            Response response = httpClient.newCall(request).execute();
            return response;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
