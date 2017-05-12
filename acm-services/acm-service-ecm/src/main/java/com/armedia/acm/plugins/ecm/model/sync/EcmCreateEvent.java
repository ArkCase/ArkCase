package com.armedia.acm.plugins.ecm.model.sync;

import com.armedia.acm.core.model.AcmEvent;
import org.json.JSONObject;

/**
 * Created by dmiller on 5/12/17.
 */
public class EcmCreateEvent extends AcmEvent
{
    public EcmCreateEvent(JSONObject source)
    {
        super(source);

    }
}
