package com.armedia.acm.services.users.service.ldap;

/**
 * @author mario.gjurcheski
 *
 */
public interface AcmLdapBeanSyncService
{

    /**
     * synchronize the ldap configuration beans
     */
    void sync();

    /**
     * synchronizes the ldap group beans when ldap configuration is updated.
     *
     */
    void syncLdapGroupConfigAttributes();

    /**
     * synchronizes the ldap user beans when ldap configuration is updated.
     *
     */
    void syncLdapUserConfigAttributes();


    /**
     * Creates ldap user configuration in config server.
     *
     * @params directoryType,
     *         props, properties
     */
    void createLdapUserConfig(String id, String directoryType);

    /**
     * Creates ldap group configuration in config server.
     *
     * @params directoryType,
     *         props, properties
     */
    void createLdapGroupConfig(String id, String directoryType);

}
