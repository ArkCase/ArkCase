package com.armedia.acm.plugins.ecm.service.sync;

import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by dmiller on 5/12/17.
 */
public interface EcmAuditResponseReader
{
    List<EcmEvent> read(JSONObject auditResponseJson);
}
