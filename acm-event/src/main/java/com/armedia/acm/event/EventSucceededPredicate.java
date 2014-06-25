package com.armedia.acm.event;

import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by armdev on 6/25/14.
 */
public class EventSucceededPredicate implements Predicate
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean evaluate(Object object)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("type of event: " + object.getClass().toString());
        }

        if ( ! ( object instanceof AcmEvent) )
        {
            return false;
        }

        AcmEvent event = (AcmEvent) object;

        return event.isSucceeded();
    }
}
