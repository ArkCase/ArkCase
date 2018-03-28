package com.armedia.acm.spring.events;

/**
 * Created by nebojsha on 26.04.2015.
 */
public class ContextReplacedEvent extends AbstractContextHolderEvent
{
    /**
     * Create a new ApplicationEvent.
     *
     * @param source
     *            the component that published the event (never {@code null})
     */
    public ContextReplacedEvent(Object source, String name)
    {
        super(source, name);
    }
}
