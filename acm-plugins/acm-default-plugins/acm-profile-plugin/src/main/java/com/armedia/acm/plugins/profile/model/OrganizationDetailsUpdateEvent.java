package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.event.AcmEvent;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class OrganizationDetailsUpdateEvent extends OrganizationDetailsPersistentEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.profile.organizationdetails.updated";

    public OrganizationDetailsUpdateEvent(OrganizationDetails source) {
        super(source);
    }

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}
