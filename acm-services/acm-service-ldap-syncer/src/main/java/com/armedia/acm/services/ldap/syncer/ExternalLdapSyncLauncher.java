/**
 *
 */
package com.armedia.acm.services.ldap.syncer;

import com.armedia.acm.spring.SpringContextHolder;

import org.springframework.context.ApplicationListener;

import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Feb 1, 2018
 *
 */
public class ExternalLdapSyncLauncher implements ApplicationListener<AcmLdapSyncEvent>
{

    private SpringContextHolder springContextHolder;

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(AcmLdapSyncEvent event)
    {
        Map<String, ExternalLdapSyncer> ldapSyncers = springContextHolder.getAllBeansOfType(ExternalLdapSyncer.class);
        ldapSyncers.values().forEach(syncer -> syncer.initiateSync());
    }

    /**
     * @param springContextHolder
     *            the springContextHolder to set
     */
    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

}
