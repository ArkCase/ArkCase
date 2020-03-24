package com.armedia.acm.spring.events;

/**
 * @author mario.gjurcheski
 *
 */
public class LdapDirectoryAdded extends AbstractContextHolderEvent
{

    /**
     * Create a new ApplicationEvent.
     *
     * @param source
     *            the component that published the event (never {@code null})
     * @param name
     */
    public LdapDirectoryAdded(Object source, String name)
    {
        super(source, name);
    }
}
