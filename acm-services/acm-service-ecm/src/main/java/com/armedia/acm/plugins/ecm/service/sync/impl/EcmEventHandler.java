package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by dmiller on 5/15/17.
 */
public class EcmEventHandler implements ApplicationListener<EcmEvent>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(EcmEvent ecmEvent)
    {
        log.debug("Got an ECM event {}", ecmEvent);

    }
}
