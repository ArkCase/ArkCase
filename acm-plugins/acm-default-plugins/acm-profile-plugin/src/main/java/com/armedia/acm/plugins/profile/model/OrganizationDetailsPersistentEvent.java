package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class OrganizationDetailsPersistentEvent extends AcmEvent {

    private static final String OBJECT_TYPE = "COMPANY_DETAILS";

    public OrganizationDetailsPersistentEvent(OrganizationDetails source) {//,String userId) {
        super(source);
        setObjectId(source.getOrganizationDetailsId());
        setEventDate(new Date());
        //setUserId(userId);
    }

    @Override
    public String getObjectType() {
        return OBJECT_TYPE;
    }
}
