/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.nativeimpl.actions.ftp;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeEnum;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.nativeimpl.actions.ftp.util.FileConstants;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.Attribute;
import org.ballerinalang.natives.annotations.BallerinaAction;
import org.ballerinalang.natives.annotations.BallerinaAnnotation;
import org.ballerinalang.natives.connectors.AbstractNativeAction;
import org.ballerinalang.natives.connectors.BallerinaConnectorManager;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.messaging.CarbonMessageProcessor;
import org.wso2.carbon.messaging.ClientConnector;
import org.wso2.carbon.messaging.exceptions.ClientConnectorException;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * {@code Init} is the Init action implementation of the FTP Connector.
 *
 * @since 0.9
 */
@BallerinaAction(
        packageName = "ballerina.net.ftp",
        actionName = "<init>",
        connectorName = FileConstants.CONNECTOR_NAME,
        args = {@Argument(name = "ftpClientConnector", type = TypeEnum.CONNECTOR)})
@BallerinaAnnotation(annotationName = "Description", attributes = { @Attribute(name = "value",
        value = "The init action implementation for FTP connector.") })
@Component(
        name = "action.net.ftp.init",
        immediate = true,
        service = AbstractNativeAction.class)
public class Init extends AbstractFtpAction {
    private static final Logger log = LoggerFactory.getLogger(Init.class);

    @Override
    public BValue execute(Context context) {
        ClientConnector ftpConnector = BallerinaConnectorManager.getInstance().getClientConnector(
                FileConstants.FTP_CONNECTOR_NAME);

        Map<String, Object> fsOpts = new HashMap<>();
        fsOpts.put("PROTOCOL", "ftp");
        fsOpts.put("FTP_PASSIVE_MODE", Boolean.TRUE); //TODO: Make this configurable from Ballerina?

        if (ftpConnector == null) {
            CarbonMessageProcessor carbonMessageProcessor =
                    BallerinaConnectorManager.getInstance().getMessageProcessor();
            ServiceLoader<ClientConnector> clientConnectorLoader = ServiceLoader.load(ClientConnector.class);

            clientConnectorLoader.forEach((clientConnector) -> {
                clientConnector.setMessageProcessor(carbonMessageProcessor);
                BallerinaConnectorManager.getInstance().registerClientConnector(clientConnector);
            });
            // using "file" here due to the constraint caused by the common VFS connector
            ftpConnector = BallerinaConnectorManager.getInstance().getClientConnector("file");
        }

        try {
            ftpConnector.init(null, null, fsOpts);
        } catch (ClientConnectorException e) {
            log.error("Error in initializing the FTP Client Connector", e);
        }
        return null;
    }

    @Override
    public boolean isNonBlockingAction() {
        return false;
    }
}
