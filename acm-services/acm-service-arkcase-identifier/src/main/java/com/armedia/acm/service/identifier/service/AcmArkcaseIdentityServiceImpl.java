package com.armedia.acm.service.identifier.service;

import com.armedia.acm.service.identifier.dao.AcmArkcaseGlobalIdentityDao;
import com.armedia.acm.service.identifier.dao.AcmArkcaseLocalIdentityDao;
import com.armedia.acm.service.identifier.exceptions.AcmIdentityException;
import com.armedia.acm.service.identifier.model.AcmArkcaseIdentity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 *
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
            throw new AcmIdentityException("Not initialized yet.");
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
                log.error("Couldn't set local identity.", e);
            }
            try
            {
                identity.setGlobalID(globalIdentityDao.getIdentity());
            }
            catch (AcmIdentityException e)
            {
                log.error("Couldn't set global identity.", e);
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
