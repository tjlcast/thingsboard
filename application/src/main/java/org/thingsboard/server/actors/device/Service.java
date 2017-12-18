package org.thingsboard.server.actors.device;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2017/12/18.
 */
public class Service {
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

    public Service(JsonObject payLoad){
        serviceName = payLoad.get("serviceName").getAsString();
        serviceDescription = payLoad.get("serviceDescription").getAsString();
        serviceType = payLoad.get("serviceType").getAsString();
        protocol = payLoad.get("protocol").getAsString();
        otherInfo = payLoad.get("otherInfo").getAsJsonObject();
        url = payLoad.get("url").getAsString();
        requireResponce = payLoad.get("requireResponce").getAsBoolean();
        serviceBody = payLoad.get("serviceBody").getAsJsonObject();
    }


}
