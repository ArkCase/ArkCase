package com.armedia.acm.spring.events;

/**
 * @author mario.gjurcheski
 *
 */
public class LdapDirectoryReplaced extends AbstractContextHolderEvent
{

    /**
     * Create a new ApplicationEvent.
     *
     * @param source
     *            the component that published the event (never {@code null})
     * @param name
     */
    public LdapDirectoryReplaced(Object source, String name)
    {
        super(source, name);
    }
}
