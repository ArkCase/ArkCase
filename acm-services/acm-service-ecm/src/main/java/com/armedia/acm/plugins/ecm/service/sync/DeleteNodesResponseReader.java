package com.armedia.acm.plugins.ecm.service.sync;

import com.armedia.acm.plugins.ecm.model.sync.EcmDeleteEvent;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by dmiller on 5/12/17.
 */
public interface DeleteNodesResponseReader
{
    List<EcmDeleteEvent> read(JSONObject deleteNodesJson);
}
