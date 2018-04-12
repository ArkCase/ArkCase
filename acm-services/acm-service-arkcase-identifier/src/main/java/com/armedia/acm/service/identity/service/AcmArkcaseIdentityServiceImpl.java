package com.armedia.acm.service.identity.service;

import com.armedia.acm.service.identity.dao.AcmArkcaseGlobalIdentityDao;
import com.armedia.acm.service.identity.dao.AcmArkcaseLocalIdentityDao;
import com.armedia.acm.service.identity.exceptions.AcmIdentityException;
import com.armedia.acm.service.identity.exceptions.AcmIdentityNotReadyException;
import com.armedia.acm.service.identity.model.AcmArkcaseIdentity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Provides creating and retrieving arkcase identity
 */
public class AcmArkcaseIdentityServiceImpl implements AcmArkcaseIdentityService, ApplicationListener<ContextRefreshedEvent>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private boolean initialized = false;
    private AcmArkcaseIdentity identity;
    private AcmArkcaseLocalIdentityDao localIdentityDao;
    private AcmArkcaseGlobalIdentityDao globalIdentityDao;

    @Override
    public AcmArkcaseIdentity getIdentity() throws AcmIdentityException
    {
        if (!initialized)
        {
            throw new AcmIdentityNotReadyException("Not initialized yet.");
        }

        return identity;
    }

    /**
     * Handle an application event.
     *
     * @param event
     *            the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        if (((ApplicationContext) event.getSource()).getParent() == null)
        {
            identity = new AcmArkcaseIdentity();
            try
            {
                identity.setInstanceID(localIdentityDao.createIdentityIfNotExists());
            }
            catch (AcmIdentityException e)
            {
                log.error("Couldn't set local identity.", e.getMessage());
            }
            try
            {
                identity.setGlobalID(globalIdentityDao.getIdentity());
            }
            catch (AcmIdentityException e)
            {
                log.error("Couldn't set global identity.", e.getMessage());
            }
            initialized = true;
        }
    }

    public void setLocalIdentityDao(AcmArkcaseLocalIdentityDao localIdentityDao)
    {
        this.localIdentityDao = localIdentityDao;
    }

    public void setGlobalIdentityDao(AcmArkcaseGlobalIdentityDao globalIdentityDao)
    {
        this.globalIdentityDao = globalIdentityDao;
    }
}
