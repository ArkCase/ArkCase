package com.armedia.acm.web.api;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.core.ResolvableType;

/**
 * {@link ApplicationEventMulticaster} implementation for publishing events to synchronous and asynchronous listeners.
 * <p>
 * Created by Bojan Milenkoski on 15.1.2016.
 */
public class DistributiveEventMulticaster implements ApplicationEventMulticaster
{

    private ApplicationEventMulticaster asyncEventMulticaster;
    private ApplicationEventMulticaster syncEventMulticaster;

    @Override
    public void addApplicationListener(ApplicationListener<?> listener)
    {
        // choose multicaster by annotation
        if (listener.getClass().getAnnotation(AsyncApplicationListener.class) != null)
        {
            asyncEventMulticaster.addApplicationListener(listener);
        } else
        {
            syncEventMulticaster.addApplicationListener(listener);
        }
    }

    @Override
    public void addApplicationListenerBean(String listenerBeanName)
    {
        // do nothing
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener)
    {
        asyncEventMulticaster.removeApplicationListener(listener);
        syncEventMulticaster.removeApplicationListener(listener);
    }

    @Override
    public void removeApplicationListenerBean(String listenerBeanName)
    {
        // do nothing
    }

    @Override
    public void removeAllListeners()
    {
        syncEventMulticaster.removeAllListeners();
        asyncEventMulticaster.removeAllListeners();
    }

    @Override
    public void multicastEvent(ApplicationEvent event)
    {
        syncEventMulticaster.multicastEvent(event);
        asyncEventMulticaster.multicastEvent(event);
    }

    @Override
    public void multicastEvent(ApplicationEvent event, ResolvableType resolvableType)
    {
        syncEventMulticaster.multicastEvent(event, resolvableType);
        asyncEventMulticaster.multicastEvent(event, resolvableType);
    }

    public void setAsyncEventMulticaster(ApplicationEventMulticaster asyncEventMulticaster)
    {
        this.asyncEventMulticaster = asyncEventMulticaster;
    }

    public void setSyncEventMulticaster(ApplicationEventMulticaster syncEventMulticaster)
    {
        this.syncEventMulticaster = syncEventMulticaster;
    }
}
