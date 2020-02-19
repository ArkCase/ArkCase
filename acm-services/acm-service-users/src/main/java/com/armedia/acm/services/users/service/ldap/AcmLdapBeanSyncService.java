package com.armedia.acm.services.users.service.ldap;

import java.util.Map;

public interface AcmLdapBeanSyncService
{

    void sync();

    void syncLdapGroupConfigAttributes();

    void syncLdapUserConfigAttributes();

    void createLdapUserConfig(String id, String directoryType, Map<String, Object> props,
            Map<String, Map<String, Object>> properties);

    void createLdapGroupConfig(String id, String directoryType, Map<String, Object> props,
            Map<String, Map<String, Object>> properties);

    void deleteLdapDirectoryConfig(String directoryId);
}
