package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.data.AcmBeforeUpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by armdev on 11/24/14.
 */
public class ProtectedObjectUpdateListener implements AcmBeforeUpdateListener
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void beforeUpdate(Object object)
    {
        log.debug("Before update on object type: " + object.getClass().getName());
    }
}
