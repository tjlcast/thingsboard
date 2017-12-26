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
