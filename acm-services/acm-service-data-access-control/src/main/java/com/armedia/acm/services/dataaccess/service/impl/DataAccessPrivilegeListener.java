package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.data.AcmDatabasePreCommitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by armdev on 1/6/15.
 */
public class DataAccessPrivilegeListener implements ApplicationListener<AcmDatabasePreCommitEvent>
{
    private final transient Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(AcmDatabasePreCommitEvent acmDatabasePreCommitEvent)
    {
        log.debug("Got a precommit event: " + acmDatabasePreCommitEvent.getEventType() + " " + acmDatabasePreCommitEvent.getSource());
    }
}
