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
package org.thingsboard.server.transport.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.security.DeviceTokenCredentials;
import org.thingsboard.server.common.msg.core.*;
import org.thingsboard.server.common.msg.device.DeviceRecognitionMsg;
import org.thingsboard.server.common.msg.session.AdaptorToSessionActorMsg;
import org.thingsboard.server.common.msg.session.BasicAdaptorToSessionActorMsg;
import org.thingsboard.server.common.msg.session.BasicToDeviceActorSessionMsg;
import org.thingsboard.server.common.msg.session.FromDeviceMsg;
import org.thingsboard.server.common.transport.SessionMsgProcessor;
import org.thingsboard.server.common.transport.adaptor.JsonConverter;
import org.thingsboard.server.common.transport.auth.DeviceAuthService;
import org.thingsboard.server.transport.http.session.HttpSessionCtx;
import org.thingsboard.server.transport.http.utils.StringUtil;
import org.thingsboard.server.dao.device.DeviceService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Shvayka
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class DeviceApiController {

    @Value("${http.request_timeout}")
    private long defaultTimeout;

    @Autowired(required = false)
    private SessionMsgProcessor processor;

    @Autowired(required = false)
    private DeviceAuthService authService;

    @Autowired(required = false)
    private DeviceService deviceService ;

    @RequestMapping(value = "/{deviceToken}/attributes", method = RequestMethod.GET, produces = "application/json")
    public DeferredResult<ResponseEntity> getDeviceAttributes(@PathVariable("deviceToken") String deviceToken,
                                                              @RequestParam(value = "clientKeys", required = false, defaultValue = "") String clientKeys,
                                                              @RequestParam(value = "sharedKeys", required = false, defaultValue = "") String sharedKeys) {
        DeferredResult<ResponseEntity> responseWriter = new DeferredResult<ResponseEntity>();
        HttpSessionCtx ctx = getHttpSessionCtx(responseWriter);
        if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
            GetAttributesRequest request;
            if (StringUtils.isEmpty(clientKeys) && StringUtils.isEmpty(sharedKeys)) {
                request = new BasicGetAttributesRequest(0);
            } else {
                Set<String> clientKeySet = !StringUtils.isEmpty(clientKeys) ? new HashSet<>(Arrays.asList(clientKeys.split(","))) : null;
                Set<String> sharedKeySet = !StringUtils.isEmpty(sharedKeys) ? new HashSet<>(Arrays.asList(sharedKeys.split(","))) : null;
                request = new BasicGetAttributesRequest(0, clientKeySet, sharedKeySet);
            }
            process(ctx, request);
        } else {
            responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        }

        return responseWriter;
    }

    @RequestMapping(value = "/{deviceToken}/attributes", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity> postDeviceAttributes(@PathVariable("deviceToken") String deviceToken,
                                                               @RequestBody String json) {
        DeferredResult<ResponseEntity> responseWriter = new DeferredResult<ResponseEntity>();
        HttpSessionCtx ctx = getHttpSessionCtx(responseWriter);
        if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
            try {
                process(ctx, JsonConverter.convertToAttributes(new JsonParser().parse(json)));
            } catch (IllegalStateException | JsonSyntaxException ex) {
                responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            }
        } else {
            responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        }
        return responseWriter;
    }

    @RequestMapping(value = "/{deviceToken}/telemetry", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity> postTelemetry(@PathVariable("deviceToken") String deviceToken,
                                                        @RequestBody String json) {
        DeferredResult<ResponseEntity> responseWriter = new DeferredResult<ResponseEntity>();
        HttpSessionCtx ctx = getHttpSessionCtx(responseWriter);
        if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
            try {
                process(ctx, JsonConverter.convertToTelemetry(new JsonParser().parse(json)));
            } catch (IllegalStateException | JsonSyntaxException ex) {
                responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            }
        } else {
            responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        }
        return responseWriter;
    }

    @RequestMapping(value = "/{deviceToken}/rpc", method = RequestMethod.GET, produces = "application/json")
    public DeferredResult<ResponseEntity> subscribeToCommands(@PathVariable("deviceToken") String deviceToken,
                                                              @RequestParam(value = "timeout", required = false, defaultValue = "0") long timeout) {
        return subscribe(deviceToken, timeout, new RpcSubscribeMsg());
    }

    @RequestMapping(value = "/{deviceToken}/rpc/{requestId}", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity> replyToCommand(@PathVariable("deviceToken") String deviceToken,
                                                         @PathVariable("requestId") Integer requestId,
                                                         @RequestBody String json) {
        DeferredResult<ResponseEntity> responseWriter = new DeferredResult<ResponseEntity>();
        HttpSessionCtx ctx = getHttpSessionCtx(responseWriter);
        if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
            try {
                JsonObject response = new JsonParser().parse(json).getAsJsonObject();
                process(ctx, new ToDeviceRpcResponseMsg(requestId, response.toString()));
            } catch (IllegalStateException | JsonSyntaxException ex) {
                responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            }
        } else {
            responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        }
        return responseWriter;
    }

    @RequestMapping(value = "/{deviceToken}/rpc", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity> postRpcRequest(@PathVariable("deviceToken") String deviceToken,
                                                         @RequestBody String json) {
        DeferredResult<ResponseEntity> responseWriter = new DeferredResult<ResponseEntity>();
        HttpSessionCtx ctx = getHttpSessionCtx(responseWriter);
        if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
            try {
                JsonObject request = new JsonParser().parse(json).getAsJsonObject();
                process(ctx, new ToServerRpcRequestMsg(0,
                        request.get("method").getAsString(),
                        request.get("params").toString()));
            } catch (IllegalStateException | JsonSyntaxException ex) {
                responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            }
        } else {
            responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        }
        return responseWriter;
    }

    @RequestMapping(value = "/{deviceToken}/attributes/updates", method = RequestMethod.GET, produces = "application/json")
    public DeferredResult<ResponseEntity> subscribeToAttributes(@PathVariable("deviceToken") String deviceToken,
                                                                @RequestParam(value = "timeout", required = false, defaultValue = "0") long timeout) {
        return subscribe(deviceToken, timeout, new AttributesSubscribeMsg());
    }

    @RequestMapping(value = "/{deviceToken}/attributes/register", method = RequestMethod.POST, produces = "application/json")
    public DeferredResult<ResponseEntity> register(@PathVariable("deviceToken") String deviceToken,
                                                   @RequestBody String json) {
        System.err.println(json);
        DeferredResult<ResponseEntity> responseWriter = new DeferredResult<ResponseEntity>();
        HttpSessionCtx ctx = getHttpSessionCtx(responseWriter);

        JsonObject info = new JsonParser().parse(json).getAsJsonObject();
        String manufacture = info.has("manufacture")?info.get("manufacture").getAsString():null;
        String deviceType = info.has("deviceType")?info.get("deviceType").getAsString():null;
        String model = info.has("model")?info.get("model").getAsString():null;
        if(!StringUtil.checkNotNull(manufacture,deviceType,model)){
            responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            return responseWriter;
        }
        if(ctx.login(new DeviceTokenCredentials(deviceToken))){
            Device device = ctx.getDevice();
            device.setManufacture(manufacture);
            device.setDeviceType(deviceType);
            device.setModel(model);
            deviceService.saveDevice(device);
            responseWriter.setResult(new ResponseEntity<>(HttpStatus.OK));
        }else{
            responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        }
        DeviceRecognitionMsg msg = new DeviceRecognitionMsg(manufacture,deviceType,model,ctx.getDevice());
        processor.onMsg(msg);
        return responseWriter;
    }

    private DeferredResult<ResponseEntity> subscribe(String deviceToken, long timeout, FromDeviceMsg msg) {
        DeferredResult<ResponseEntity> responseWriter = new DeferredResult<ResponseEntity>();
        HttpSessionCtx ctx = getHttpSessionCtx(responseWriter, timeout);
        if (ctx.login(new DeviceTokenCredentials(deviceToken))) {
            try {
                process(ctx, msg);
            } catch (IllegalStateException | JsonSyntaxException ex) {
                responseWriter.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            }
        } else {
            responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        }
        return responseWriter;
    }

    private HttpSessionCtx getHttpSessionCtx(DeferredResult<ResponseEntity> responseWriter) {
        return getHttpSessionCtx(responseWriter, defaultTimeout);
    }

    private HttpSessionCtx getHttpSessionCtx(DeferredResult<ResponseEntity> responseWriter, long timeout) {
        return new HttpSessionCtx(processor, authService, responseWriter, timeout != 0 ? timeout : defaultTimeout);
    }

    private void process(HttpSessionCtx ctx, FromDeviceMsg request) {
        AdaptorToSessionActorMsg msg = new BasicAdaptorToSessionActorMsg(ctx, request);
        processor.process(new BasicToDeviceActorSessionMsg(ctx.getDevice(), msg));
    }

}
