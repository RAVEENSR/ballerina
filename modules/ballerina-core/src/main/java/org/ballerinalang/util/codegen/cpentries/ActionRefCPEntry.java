/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.ballerinalang.util.codegen.cpentries;

import org.ballerinalang.util.codegen.ActionInfo;

import java.util.Objects;

/**
 * {@code ActionRefCPEntry} represents a Ballerina action in the constant pool.
 *
 * @since 0.87
 */
public class ActionRefCPEntry implements ConstantPoolEntry {

    // Index to a valid Package entry in the constant pool
    private int packageCPIndex;
    private String packagePath;

    // Index to a valid name index in the constant pool
    private int nameCPIndex;
    private String actionName;

    // Index to a valid connector ref index in the constant pool
    private int connectorRefCPIndex;
    private StructureRefCPEntry connectorRefCPEntry;

    private ActionInfo actionInfo;

    public ActionRefCPEntry(int packageCPIndex, String packagePath, int connectorRefCPIndex,
                            StructureRefCPEntry connectorRefCPEntry,
                            int nameCPIndex, String actionName) {
        this.packageCPIndex = packageCPIndex;
        this.packagePath = packagePath;
        this.connectorRefCPIndex = connectorRefCPIndex;
        this.connectorRefCPEntry = connectorRefCPEntry;
        this.nameCPIndex = nameCPIndex;
        this.actionName = actionName;
    }

    public int getPackageCPIndex() {
        return packageCPIndex;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public int getNameCPIndex() {
        return nameCPIndex;
    }

    public String getActionName() {
        return actionName;
    }

    public int getConnectorRefCPIndex() {
        return connectorRefCPIndex;
    }

    public StructureRefCPEntry getConnectorRefCPEntry() {
        return connectorRefCPEntry;
    }

    public ActionInfo getActionInfo() {
        return actionInfo;
    }

    public void setActionInfo(ActionInfo actionInfo) {
        this.actionInfo = actionInfo;
    }

    public EntryType getEntryType() {
        return EntryType.CP_ENTRY_ACTION_REF;
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageCPIndex, nameCPIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActionRefCPEntry && packageCPIndex == (((ActionRefCPEntry) obj).packageCPIndex) &&
                nameCPIndex == ((ActionRefCPEntry) obj).nameCPIndex &&
                connectorRefCPIndex == ((ActionRefCPEntry) obj).connectorRefCPIndex;
    }
}
