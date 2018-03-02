package com.armedia.acm.plugins.dashboard.site.model;

import com.armedia.acm.core.model.AcmEvent;

/**
 * Created by joseph.mcgrady on 4/26/2017.
 */
public class SiteEvent extends AcmEvent
{
    private static final long serialVersionUID = 38795920545L;
    private static final String EVENT_TYPE_BASE = "com.armedia.acm.site";

    public SiteEvent(Site site, String eventType, boolean succeeded, String ipAddress)
    {
        super(site);

        setObjectId(site.getId());
        setObjectType(site.getObjectType());
        setEventDate(site.getModified());
        setUserId(site.getModifier());
        setEventType(EVENT_TYPE_BASE + "." + eventType);
        setSucceeded(succeeded);
        setIpAddress(ipAddress);
    }
}