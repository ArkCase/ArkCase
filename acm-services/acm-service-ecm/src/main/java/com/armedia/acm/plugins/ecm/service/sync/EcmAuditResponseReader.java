package com.armedia.acm.plugins.ecm.service.sync;

import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

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
}
