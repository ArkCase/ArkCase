package com.armedia.acm.spring.events;

/**
 * Created by nebojsha on 26.04.2015.
 */
public class ContextRemovedEvent extends AbstractContextHolderEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the component that published the event (never {@code null})
     */
    public ContextRemovedEvent(Object source, String name) {
        super(source, name);
    }
}
