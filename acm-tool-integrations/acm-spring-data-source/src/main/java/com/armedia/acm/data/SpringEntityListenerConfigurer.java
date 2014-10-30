package com.armedia.acm.data;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import java.util.List;

/**
 * Created by armdev on 10/21/14.
 */
public class SpringEntityListenerConfigurer
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private EntityManagerFactory entityManagerFactory;

    private List<DescriptorEventListener> defaultListeners;

    private SessionAccumulatingSessionListener sessionListener;

    public void initBean()
    {
        log.debug("in initBean for spring entity listener config");
        Session session = getEntityManagerFactory().unwrap(Session.class);
        session.getEventManager().addListener(getSessionListener());

        Project project = getEntityManagerFactory().unwrap(Session.class).getProject();
        for (EntityType<?> entityType : getEntityManagerFactory().getMetamodel().getEntities() )
        {
            ClassDescriptor descriptor = project.getClassDescriptor(entityType.getJavaType());
            boolean foundDescriptor = descriptor != null;
            if ( foundDescriptor )
            {
                log.debug("adding listeners...");
                descriptor.getEventManager().getEventListeners().addAll(getDefaultListeners());
            }
            log.debug("Entity class: " + entityType.getJavaType().getName() + "; has descriptor? " + foundDescriptor);
        }
    }

    public EntityManagerFactory getEntityManagerFactory()
    {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory)
    {
        this.entityManagerFactory = entityManagerFactory;
    }

    public List<DescriptorEventListener> getDefaultListeners()
    {
        return defaultListeners;
    }

    public void setDefaultListeners(List<DescriptorEventListener> defaultListeners)
    {
        this.defaultListeners = defaultListeners;
    }

    public SessionAccumulatingSessionListener getSessionListener()
    {
        return sessionListener;
    }

    public void setSessionListener(SessionAccumulatingSessionListener sessionListener)
    {
        this.sessionListener = sessionListener;
    }
}
