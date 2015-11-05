package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.services.dataaccess.model.AccessControlEntry;
import com.armedia.acm.services.dataaccess.model.AccessControlList;
import com.armedia.acm.services.dataaccess.service.AccessControlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Check if particular user is granted access to a given object.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 05.11.2015.
 */
public class AccessControlServiceImpl implements AccessControlService
{
    /**
     * Access Control List, configured in a JSON file.
     */
    private AccessControlList accessControlList;

    /**
     * ACL configuration JSON file ($HOME/.acm/accessControlList.json)
     */
    private File configurationFile;

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Load the access control list
     */
    public void postConstruct()
    {
        log.debug("Creating ACL from [{}]", configurationFile.getAbsolutePath());
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            accessControlList = mapper.readValue(configurationFile, AccessControlList.class);
        } catch (IOException e)
        {
            log.error("Unable to create ACL from [{}]", configurationFile.getAbsolutePath(), e);
        }
    }

    /**
     * Check if particular user is granted access to a given object.
     * This is accomplished with iterating over all configured ACL entries until the first positive match
     *
     * @param authentication authentication token
     * @param targetId       the identifier for the object instance
     * @param targetType     target type
     * @param permission     permission object
     * @return true if user is allowed to access this object, false otherwise
     */
    public boolean isAccessGranted(Authentication authentication, Long targetId, String targetType, Object permission)
    {
        log.debug("Check if [{}] is granted access to object with id [{}]", authentication.getName(), targetId);
        boolean granted = false;
        Properties properties = retrieveObjectProperties(targetId);
        for (AccessControlEntry accessControlEntry : accessControlList.getAccessControlEntryList())
        {
            if (targetType == null || !targetType.equals(accessControlEntry.getObjectType()))
            {
                continue;
            }
            granted = evaluate(properties, authentication, accessControlEntry);
            if (granted)
            {
                break;
            }
        }
        return granted;
    }

    @Override
    public AccessControlList getAccessControlList()
    {
        return accessControlList;
    }

    /**
     * Retrieve object properties used in permission checking
     *
     * @param targetId object identifier
     * @return list of properties
     */
    private Properties retrieveObjectProperties(Long targetId)
    {
        return new Properties();
    }

    /**
     * Evaluate single ACl entry
     *
     * @param properties         object properties used in permission checking
     * @param authentication     authentication token
     * @param accessControlEntry ACL entry
     * @return evaluated ACL entry
     */
    private boolean evaluate(Properties properties, Authentication authentication, AccessControlEntry accessControlEntry)
    {
        // empty implementation
        return false;
    }

    public File getConfigurationFile()
    {
        return configurationFile;
    }

    public void setConfigurationFile(File configurationFile)
    {
        this.configurationFile = configurationFile;
    }
}
