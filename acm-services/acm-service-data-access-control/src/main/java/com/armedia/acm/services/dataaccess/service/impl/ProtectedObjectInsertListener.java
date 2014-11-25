package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.data.AcmBeforeInsertListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by armdev on 11/24/14.
 */
public class ProtectedObjectInsertListener implements AcmBeforeInsertListener
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void beforeInsert(Object object)
    {
        log.debug("Before insert on object type: " + object.getClass().getName());
    }
}
