package com.armedia.acm.plugins.ecm.service.sync;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static java.util.stream.Collectors.toList;

import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Created by dmiller on 5/12/17.
 */
public interface EcmAuditResponseReader
{
    default List<EcmEvent> read(JSONObject auditResponseJson)
    {
        int count = auditResponseJson.getInt("count");

        JSONArray auditEvents = auditResponseJson.getJSONArray("entries");

        return IntStream.range(0, count)
                .mapToObj(auditEvents::getJSONObject)
                .map(this::buildEcmEvent)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    EcmEvent buildEcmEvent(JSONObject createEvent);

    default String extractNodeName(String nodeInfo)
    {
        int nameIdx = nodeInfo.indexOf("name=");
        int startIdx = nameIdx + "name=".length();
        int endIdx = nodeInfo.lastIndexOf(", isFolder");
        return nodeInfo.substring(startIdx, endIdx);
    }

    default String extractNodeId(String nodeInfo)
    {
        int nodeRefIdx = nodeInfo.lastIndexOf("nodeRef=");
        int startIdx = nodeRefIdx + "nodeRef=".length();
        int endIdx = nodeInfo.lastIndexOf("]");
        return nodeInfo.substring(startIdx, endIdx);
    }
}
