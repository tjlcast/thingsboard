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
package org.thingsboard.server.utils;

import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import springfox.documentation.spring.web.json.Json;

/**
 * Created by Administrator on 2017/12/18.
 */
public class MqttUtil {

    public static void sendMsg(String url, JsonObject headers,String msg){
        try{
            int index = url.indexOf('$');
            String servver = url.substring(0,index);
            String topic = url.substring(index+1);
            MqttClient client = new MqttClient(servver,"rpcCallFromThingsboard", new MemoryPersistence());
            MqttConnectOptions option =  new MqttConnectOptions();
            option.setCleanSession(true);
            client.connect(option);
            client.publish(topic,msg.getBytes(),0,false);
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("发送mqtt消息失败");
        }

    }
}
