/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.nativeimpl.net.ws;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeEnum;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.AbstractNativeFunction;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.Attribute;
import org.ballerinalang.natives.annotations.BallerinaAnnotation;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.services.dispatchers.ws.WebSocketConnectionManager;
import org.ballerinalang.services.dispatchers.ws.WebSocketServicesRegistry;
import org.ballerinalang.util.codegen.ServiceInfo;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.io.IOException;
import java.util.List;
import javax.websocket.Session;

/**
 * Broadcasts text to all the clients connected to a given endpoint.
 */
@BallerinaFunction(
        packageName = "ballerina.net.ws",
        functionName = "broadcastText",
        args = {
                @Argument(name = "text", type = TypeEnum.STRING)
        },
        isPublic = true
)
@BallerinaAnnotation(annotationName = "Description",
                     attributes = { @Attribute(name = "value", value = "This pushes text from server to all the " +
                             "connected clients of the service.") })
@BallerinaAnnotation(annotationName = "Param",
                     attributes = { @Attribute(name = "text", value = "Text which should be sent") })
public class BroadcastText extends AbstractNativeFunction {

    @Override
    public BValue[] execute(Context context) {

        if (context.getServiceInfo() == null) {
            throw new BallerinaException("This function is only working with WebSocket services");
        }

        String text = getStringArgument(context, 0);
        ServiceInfo service = context.getServiceInfo();
        WebSocketServicesRegistry servicesRegistry = WebSocketServicesRegistry.getInstance();
        if (servicesRegistry.isClientService(service)) {
            ServiceInfo parentService = servicesRegistry.getParentServiceOfClientService(service);
            broadcast(text, context, parentService);
        } else {
            broadcast(text, context, service);
        }
        return VOID_RETURN;
    }

    private void broadcast(String text, Context context, ServiceInfo service) {
        List<Session> sessions = WebSocketConnectionManager.getInstance().getBroadcastConnectionList(service);
        if (sessions == null) {
            throw new BallerinaException("Cannot find a broadcast list for the service: " + service);
        }
        sessions.forEach(
                session -> {
                    try {
                        session.getBasicRemote().sendText(text);
                    } catch (IOException e) {
                        throw new BallerinaException("IO exception occurred during broadcasting text", e, context);
                    }
                }
        );
    }
}
