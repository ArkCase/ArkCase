package com.armedia.acm.data;

import com.armedia.acm.spring.SpringContextHolder;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by armdev on 10/21/14.
 */
public class SpringEntityListenerConfigurer implements ApplicationContextAware
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private EntityManagerFactory entityManagerFactory;

    private List<DescriptorEventListener> defaultListeners;

    private SessionAccumulatingSessionListener sessionListener;

    private SpringContextHolder springContextHolder;

    public void onApplicationContextInitialized()
    {
        log.debug("in onApplicationContextInitialized for spring entity listener config");
        Session session = getEntityManagerFactory().unwrap(Session.class);
        session.getEventManager().addListener(getSessionListener());

        Collection<DescriptorEventAdapter> descriptorEventAdapters = generateAdaptersForListeners();

        Project project = getEntityManagerFactory().unwrap(Session.class).getProject();
        for (EntityType<?> entityType : getEntityManagerFactory().getMetamodel().getEntities() )
        {
            ClassDescriptor descriptor = project.getClassDescriptor(entityType.getJavaType());
            boolean foundDescriptor = descriptor != null;
            if ( foundDescriptor )
            {
                log.debug("adding listeners...");
                descriptor.getEventManager().getEventListeners().addAll(getDefaultListeners());
                descriptor.getEventManager().getEventListeners().addAll(descriptorEventAdapters);
            }
            log.debug("Entity class: " + entityType.getJavaType().getName() + "; has descriptor? " + foundDescriptor);
        }
    }

    public Collection<DescriptorEventAdapter> generateAdaptersForListeners()
    {
        List<DescriptorEventAdapter> retval = new ArrayList<>();

        for ( AcmBeforeUpdateListener beforeUpdateListener : findBeforeUpdateListeners() )
        {
            DescriptorEventAdapter dea = new AcmBeforeUpdateAdapter(beforeUpdateListener);
            retval.add(dea);
            }

        for ( AcmBeforeInsertListener beforeInsertListener : findBeforeInsertListeners()  )
        {
            DescriptorEventAdapter dea = new AcmBeforeInsertAdapter(beforeInsertListener);
            retval.add(dea);
        }

        for ( AcmBeforeDeleteListener beforeDeleteListener : findBeforeDeleteListeners() )
        {
            DescriptorEventAdapter dea = new AcmBeforeDeleteAdapter(beforeDeleteListener);
            retval.add(dea);
        }

        return retval;
    }

    public Collection<AcmBeforeInsertListener> findBeforeInsertListeners()
    {
        return getSpringContextHolder().getAllBeansOfType(AcmBeforeInsertListener.class).values();
    }

    public Collection<AcmBeforeDeleteListener> findBeforeDeleteListeners()
    {
        return getSpringContextHolder().getAllBeansOfType(AcmBeforeDeleteListener.class).values();
    }

    public Collection<AcmBeforeUpdateListener> findBeforeUpdateListeners()
    {
        return getSpringContextHolder().getAllBeansOfType(AcmBeforeUpdateListener.class).values();
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        onApplicationContextInitialized();
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }



}
