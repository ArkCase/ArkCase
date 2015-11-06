package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.services.dataaccess.model.AccessControlRule;
import com.armedia.acm.services.dataaccess.model.AccessControlRules;
import com.armedia.acm.services.dataaccess.service.AccessControlRuleChecker;
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
public class AccessControlRuleCheckerImpl implements AccessControlRuleChecker
{
    /**
     * Access Control rules, configured in a JSON file.
     */
    private AccessControlRules accessControlRules;

    /**
     * ACL configuration JSON file ($HOME/.acm/accessControlRules.json)
     */
    private File configurationFile;

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Load the access control rules
     */
    public void postConstruct()
    {
        log.debug("Creating access control rules from [{}]", configurationFile.getAbsolutePath());
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            accessControlRules = mapper.readValue(configurationFile, AccessControlRules.class);
        } catch (IOException e)
        {
            log.error("Unable to create access control rules from [{}]", configurationFile.getAbsolutePath(), e);
        }
    }

    /**
     * Check if particular user is granted access to a given object.
     * This is accomplished with iterating over all configured AC entries until the first positive match
     *
     * @param authentication authentication token
     * @param targetId       the identifier for the object instance
     * @param targetType     target type
     * @param permission     permission object
     * @return true if user is allowed to access this object, false otherwise
     */
    public boolean isAccessGranted(Authentication authentication, Long targetId, String targetType, Object permission)
    {
        log.debug("Checking if [{}] is granted access to object of type [{}] with id [{}]", authentication.getName(), targetType, targetId);
        boolean granted = false;
        Properties properties = retrieveObjectProperties(targetId);
        for (AccessControlRule accessControlRule : accessControlRules.getAccessControlRuleList())
        {
            if (targetType == null || !targetType.equals(accessControlRule.getObjectType()))
            {
                continue;
            }
            granted = evaluate(properties, authentication, accessControlRule);
            if (granted)
            {
                log.debug("[{}] is granted access to object of type [{}] with id [{}], matching rule [{}]", authentication.getName(), targetType, targetId, accessControlRule);
                break;
            }
        }
        if (!granted)
        {
            log.warn("[{}] is denied access to object of type [{}] with id [{}], no matching rule found", authentication.getName(), targetType, targetId);
        }
        return granted;
    }

    @Override
    public AccessControlRules getAccessControlRules()
    {
        return accessControlRules;
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
     * Evaluate single AC rule
     *
     * @param properties        object properties used in permission checking
     * @param authentication    authentication token
     * @param accessControlRule AC rule
     * @return evaluated AC rule
     */
    private boolean evaluate(Properties properties, Authentication authentication, AccessControlRule accessControlRule)
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
