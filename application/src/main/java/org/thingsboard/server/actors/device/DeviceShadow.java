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

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.*;
import lombok.Getter;
import org.thingsboard.server.actors.ActorSystemContext;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.ServiceTable;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.transport.http.utils.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Administrator on 2017/12/12.
 */
public class DeviceShadow {
    private JsonObject payload;

    private ActorSystemContext systemContext;

    @Getter
    private Device device;

    private HashMap<String,JsonObject> attributes;
    private HashMap<String,JsonObject> telemetries;
    private HashMap<String,Service> services;

//    public DeviceShadow(ActorSystemContext systemContext, Device device){
//
//        this.systemContext = systemContext;
//        this.device = device;
//
//        payload = new JsonObject();
//        payload.add("attributes",new JsonArray());
////        payload.add("telemetries",new JsonArray());
//        payload.add("services",new JsonArray());
//        initAttrs(payload);
//        attributes = new HashMap<>();
//        telemetries = new HashMap<>();
//        services = new HashMap<>();
//    }

    public DeviceShadow(ActorSystemContext systemContext, Device device){

        attributes = new HashMap<>();
        telemetries = new HashMap<>();
        this.services = new HashMap<>();

        this.systemContext = systemContext;
        this.device = device;

        JsonObject pay = new JsonObject();
        pay.add("attrubutes",new JsonArray());
        pay.add("services",new JsonArray());
//        for(JsonElement json:payload.getAsJsonArray("attributes")){
//            attributes.put(((JsonObject)json).get("attributeName").getAsString(),(JsonObject) json);
//        }
//        for(JsonElement json:payload.getAsJsonArray("telemetries")){
//            telemetries.put(((JsonObject)json).get("telemetryName").getAsString(),(JsonObject) json);
//        }
//        for(JsonElement json:payload.getAsJsonArray("services")){
//            services.put(((JsonObject)json).get("serviceName").getAsString(),new Service(json.getAsJsonObject(),systemContext,device));
//        }
        this.payload = pay;
        initAttrs();
        initServices();
//            for(Map.Entry<String,JsonElement> entry:services.entrySet()){
//                this.services.put(entry.getKey(),new Service(entry.getValue().getAsJsonObject(),systemContext,device));
//                pay.get("services").getAsJsonArray().add(entry.getValue().getAsJsonObject());
//            }

    }

    public void initServices(){
        String manufacture = device.getManufacture();
        String deviceType = device.getDeviceType();
        String model = device.getModel();
        if(!StringUtil.checkNotNull(manufacture,deviceType,model)) return;
        Optional< ServiceTable > serviceTable =  systemContext.getServiceTableService().findServiceTableByCoordinate(
                manufacture+"%"+deviceType+"%"+model);
        if(!serviceTable.isPresent()) {
            System.err.println("cant find service named "+manufacture+"%"+deviceType+"%"+model);
            return ;
        }
        String des = serviceTable.get().getDescription();
        for(Map.Entry<String,JsonElement> entry:new JsonParser().parse(des).getAsJsonObject().entrySet()){
            Service service = new Service(entry.getValue().getAsJsonObject(),systemContext,device);
            updateServices(service.getServiceName(),service);
         }
    }

    public void updateServices(String serviceName,Service service){
        if(services.containsKey(serviceName)){
            services.put(serviceName,service);
            JsonArray array = payload.getAsJsonArray("services");
            for(JsonElement ele : array){
               if(ele.getAsJsonObject().get("serviceName").getAsString().equals(serviceName)){
                   array.remove(ele);
                   break;
               }
            }
            array.add(service.payload);
        }else{
            payload.getAsJsonArray("services").add(service.payload);
            services.put(serviceName,service);
        }
    }


    public void initAttrs(){
        ListenableFuture<List<AttributeKvEntry>>future =  systemContext.getAttributesService().findAll(device.getId(),"CLIENT_SCOPE");
        try{
            List<AttributeKvEntry> ls = future.get();
            for(AttributeKvEntry kv:ls){
                JsonObject attr = new JsonObject();
                attr.addProperty("attributeName",kv.getKey());
                attr.addProperty("attributeValue",kv.getValueAsString());
                attr.addProperty("timeStamp",kv.getLastUpdateTs());
                attr.addProperty("scope","client-side");
                this.updateAttribute(kv.getKey(),attr);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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

}
