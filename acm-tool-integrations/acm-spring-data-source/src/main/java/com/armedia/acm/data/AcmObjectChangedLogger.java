package com.armedia.acm.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nebojsha on 10.05.2016.
 */
public class AcmObjectChangedLogger
{
    private transient Logger log = LoggerFactory.getLogger(getClass());

    public void logObject(AcmObjectEvent event)
    {
        log.info("Object changed: {}", event);
    }
}
