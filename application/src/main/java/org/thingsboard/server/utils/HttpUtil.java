package org.thingsboard.server.utils;

import com.google.gson.JsonElement;
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

     public static void main(String[] args){
        System.out.println(System.currentTimeMillis());
        try{
            Thread.sleep(1000);
        }catch(Exception e){

        }
         System.out.println(System.currentTimeMillis());

     }

    public static JsonObject getDeviceShadowDoc(String manufacture,String deviceType,String model){
        String url = baseUrl+"/"+manufacture+"/"+deviceType+"/"+model+".json";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = httpClient.newCall(request);
        try {
            Response response = call.execute();

            //System.out.println(response.body().string());
            return new JsonParser().parse(response.body().string()).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonObject();
    }

    public static Response  sendGet(){
        return null;
    }

    public static Response  sendPost(String url,JsonObject headers,String json){

        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder buider = new Request.Builder()
                .url(url)
                .post(body);

        for(Map.Entry<String,JsonElement> entry:headers.entrySet()){
            buider.header(entry.getKey(),entry.getValue().getAsString());
        }

        Request request = buider.build();

        try{
            Response response = httpClient.newCall(request).execute();
            return response;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
