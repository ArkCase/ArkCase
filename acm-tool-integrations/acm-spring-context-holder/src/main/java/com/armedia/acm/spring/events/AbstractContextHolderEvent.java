package com.armedia.acm.spring.events;

import org.springframework.context.ApplicationEvent;

/**
 * Created by nebojsha on 24.04.2015.
 */
public abstract class AbstractContextHolderEvent extends ApplicationEvent{
    private String contextName;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the component that published the event (never {@code null})
     */
    public AbstractContextHolderEvent(Object source, String name) {
        super(source);
        contextName = name;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }
}
