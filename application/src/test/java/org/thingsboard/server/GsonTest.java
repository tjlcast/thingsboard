package org.thingsboard.server;

import com.google.gson.JsonObject;

/**
 * Created by Administrator on 2017/12/11.
 */
public class GsonTest {
public static void main(String[] args){
    JsonObject json = new JsonObject();
    json.addProperty("haha",2);
    System.out.println(json.get("haha"));

    JsonObject json1 = new JsonObject();
    json1.add("json",json);
    json1.getAsJsonObject("json").addProperty("haha",3);
}
}
