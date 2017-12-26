/**
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.actors.device;

import com.google.gson.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/12.
 */
public class DeviceShadow {
    private JsonObject payload;

    private HashMap<String,JsonObject> attributes;
    private HashMap<String,JsonObject> telemetries;
    private HashMap<String,Service> services;

    public DeviceShadow(){
        payload = new JsonObject();
        payload.add("attributes",new JsonArray());
        payload.add("telemetries",new JsonArray());
        payload.add("services",new JsonArray());

        attributes = new HashMap<>();
        telemetries = new HashMap<>();
        services = new HashMap<>();
    }

    public DeviceShadow(JsonObject payload){
        this.payload = payload;

        attributes = new HashMap<>();
        telemetries = new HashMap<>();
        services = new HashMap<>();

        for(JsonElement json:payload.getAsJsonArray("attributes")){
            attributes.put(((JsonObject)json).get("attributeName").getAsString(),(JsonObject) json);
        }
        for(JsonElement json:payload.getAsJsonArray("telemetries")){
            telemetries.put(((JsonObject)json).get("telemetryName").getAsString(),(JsonObject) json);
        }
        for(JsonElement json:payload.getAsJsonArray("services")){
            services.put(((JsonObject)json).get("serviceName").getAsString(),new Service( json.getAsJsonObject()));
        }
    }

    public static boolean isValidDeviceShadow(JsonElement json){
        if(json==null||!(json instanceof  JsonObject)) return false;
        JsonObject payload =  json.getAsJsonObject();
        if(payload==null) return false;
        if(payload.get("attributes")==null||!(payload.get("attributes") instanceof JsonArray)) return false;
        if(payload.get("telemetries")==null||!(payload.get("telemetries") instanceof JsonArray)) return false;
        if(payload.get("services")==null||!(payload.get("services") instanceof JsonArray)) return false;

        for(JsonElement element:payload.getAsJsonArray("attributes")){
            if(!isValidAttribute(element)) return false;
        }

        for(JsonElement element:payload.getAsJsonArray("telemetries")){
            if(!isValidTelemetry(element)) return false;
        }

        for(JsonElement element:payload.getAsJsonArray("services")){
            if(!isValidService(element)) return false;
        }
        return true;
    }

    public static boolean isValidAttribute(JsonElement json){
        if(json==null||!(json instanceof  JsonObject)) return false;
        JsonObject payload =  json.getAsJsonObject();
        if(payload!=null&& payload.get("attributeName") instanceof JsonPrimitive && payload.get("attributeValue") instanceof JsonPrimitive)
            return true;
        return false;
    }

    public static boolean isValidTelemetry(JsonElement json){
        if(json==null||!(json instanceof  JsonObject)) return false;
        JsonObject payload =  json.getAsJsonObject();
        if(payload!=null&& payload.get("telemetryName") instanceof JsonPrimitive && payload.get("telemetryValue") instanceof JsonPrimitive)
            return true;
        return false;
    }

    public static boolean isValidService(JsonElement json){
        if(json==null||!(json instanceof  JsonObject)) return false;
        JsonObject payload =  json.getAsJsonObject();
        if(payload!=null&& payload.get("serviceName") instanceof JsonPrimitive && payload.get("serviceDescription") instanceof JsonPrimitive)
            return true;
        return false;
    }

    public void put(String key,String value){
        payload.addProperty(key,value);
    }

    public JsonObject getPayload(){
        return payload;
    }

    public void updateAttribute(String attributeName,JsonObject attribute){
        if(!isValidAttribute(attribute)){
            return ;
        }
        if(attributes.containsKey(attributeName)){
            JsonObject old = attributes.get(attributeName);
            for(Map.Entry<String,JsonElement> entry:attribute.entrySet()){
                old.add(entry.getKey(),entry.getValue());
            }
        }else{
            payload.getAsJsonArray("attributes").add(attribute);
            attributes.put(attributeName,attribute);
        }
    }

    public void updateTelemetries(String telematryName,JsonObject telemetry){
        if(telemetries.containsKey(telematryName)){
            JsonObject old = telemetries.get(telematryName);
            for(Map.Entry<String,JsonElement> entry:telemetry.entrySet()){
                old.add(entry.getKey(),entry.getValue());
            }
        }else{
            payload.getAsJsonArray("telemetries").add(telemetry);
            telemetries.put(telematryName,telemetry);
        }
    }

    public Service getServiceByName(String serviceName){
        return  services.get(serviceName);
    }
}
