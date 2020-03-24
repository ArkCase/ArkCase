package com.armedia.acm.spring.events;

/**
 * @author mario.gjurcheski
 *
 */
public class LdapDirectoryDeleted extends AbstractContextHolderEvent
{

    /**
     * Create a new ApplicationEvent.
     *
     * @param source
     *            the component that published the event (never {@code null})
     * @param name
     */
    public LdapDirectoryDeleted(Object source, String name)
    {
        super(source, name);
    }
}
