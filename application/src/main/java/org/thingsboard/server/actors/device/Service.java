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

import akka.actor.ActorContext;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.thingsboard.server.actors.ActorSystemContext;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.plugin.PluginMetaData;
import org.thingsboard.server.extensions.api.plugins.msg.ToDeviceRpcRequest;
import org.thingsboard.server.extensions.api.plugins.msg.ToDeviceRpcRequestBody;
import org.thingsboard.server.extensions.api.plugins.msg.ToDeviceRpcRequestPluginMsg;
import org.thingsboard.server.utils.HttpUtil;
import org.thingsboard.server.utils.MqttUtil;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Created by Administrator on 2017/12/18.
 */

@Slf4j
public class Service {
    public JsonObject payload ;
    @Getter @Setter
    private String serviceName;

    @Getter @Setter
    private String serviceDescription;

    @Getter @Setter
    private String serviceType;

    @Getter @Setter
    private String protocol;

    @Getter @Setter
    private  JsonObject otherInfo;

    @Getter @Setter
    private String url;


    @Getter @Setter
    private boolean requireResponce;

    @Getter @Setter
    private JsonObject serviceBody;

    private ActorSystemContext systemContext;
    private  Device device;

    public Service(JsonObject payLoad, ActorSystemContext actorSystemContext,Device device){
        this.payload = payLoad;
        serviceName = payLoad.get("serviceName")!=null?payLoad.get("serviceName").getAsString():null;
        serviceDescription = payLoad.get("serviceDescription")!=null?payLoad.get("serviceDescription").getAsString():null;
        serviceType = payLoad.get("serviceType")!=null?payLoad.get("serviceType").getAsString():null;
        protocol = payLoad.get("protocol")!=null?payLoad.get("protocol").getAsString():null;
        otherInfo = payLoad.get("otherInfo")!=null?payLoad.get("otherInfo").getAsJsonObject():null;
        url = payLoad.get("url")!=null?payLoad.get("url").getAsString():null;
        requireResponce = payLoad.get("requireResponce")!=null?payLoad.get("requireResponce").getAsBoolean():null;
        serviceBody = payLoad.get("serviceBody")!=null?payLoad.get("serviceBody").getAsJsonObject():null;

        this.systemContext = actorSystemContext;
        this.device = device;
    }

    public void serviceCall(DeviceShadowMsg msg,JsonObject params,DeviceActorMessageProcessor processor,
                            WeakHashMap<String, DeviceShadowMsg> rpcPendingMapFromDeviceShadow,ActorContext context) {
        if (serviceType.equals("platform")) {
            //TODO 改成向本地发送一个http请求似乎更合理
            ToDeviceRpcRequestPluginMsg msg1 = shadowRpc2Rpc(params);
            UUID id = msg1.getMsg().getId();
            if (msg1.getMsg().isOneway()) {
                msg.setResult("ok");
            } else {
                rpcPendingMapFromDeviceShadow.put(id.toString(), msg);
            }
            processor.processRpcRequest(context,msg1);
        }else if(serviceType.equals("thirdParty")){
            processToThirdPartyrpcMsg(this,msg);
        }else{

        }
    }

    private void processToThirdPartyrpcMsg(Service service,DeviceShadowMsg msg) {
        String protocol = service.getProtocol().toLowerCase();
        switch (protocol) {
            case "http":
                try {
                    Response res = HttpUtil.sendPost(service.getUrl(), service.getOtherInfo(), service.getServiceBody().toString());
                    msg.setResult(res.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "mqtt":
                MqttUtil.sendMsg(service.getUrl(), service.getOtherInfo(), service.getServiceBody().toString());
                msg.setResult("mqtt msg send out ok");
                break;
            default:
                System.out.println("unsupported protocol");
        }
    }

    private ToDeviceRpcRequestPluginMsg shadowRpc2Rpc(JsonObject params){
        PluginMetaData plugin = systemContext.getPluginService().findPluginByApiToken("rpc");
        ToDeviceRpcRequest toDeviceRpcRequest = new ToDeviceRpcRequest(UUID.randomUUID(),device.getTenantId()
                ,device.getId(),!requireResponce,System.currentTimeMillis()+1000l,
                new ToDeviceRpcRequestBody(serviceBody.get("methodName").getAsString(),params.toString()));
        ToDeviceRpcRequestPluginMsg res = new ToDeviceRpcRequestPluginMsg(plugin.getId(),plugin.getTenantId(),toDeviceRpcRequest);
        return res;
    }
}
