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
        serviceName = payLoad.get("serviceName")!=null?payLoad.get("serviceName").getAsString():null;
        serviceDescription = payLoad.get("serviceDescription")!=null?payLoad.get("serviceDescription").getAsString():null;
        serviceType = payLoad.get("serviceType")!=null?payLoad.get("serviceType").getAsString():null;
        protocol = payLoad.get("protocol")!=null?payLoad.get("protocol").getAsString():null;
        otherInfo = payLoad.get("otherInfo")!=null?payLoad.get("otherInfo").getAsJsonObject():null;
        url = payLoad.get("url")!=null?payLoad.get("url").getAsString():null;
        requireResponce = payLoad.get("requireResponce")!=null?payLoad.get("requireResponce").getAsBoolean():null;
        serviceBody = payLoad.get("serviceBody")!=null?payLoad.get("serviceBody").getAsJsonObject():null;
    }


}
