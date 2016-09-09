package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.services.dataaccess.model.AccessControlRule;
import com.armedia.acm.services.dataaccess.model.AccessControlRules;
import com.armedia.acm.services.dataaccess.service.AccessControlRuleChecker;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Check if particular user is granted access to a given object. Created by Petar Ilin <petar.ilin@armedia.com> on
 * 05.11.2015.
 */
public class AccessControlRuleCheckerImpl implements AccessControlRuleChecker
{
    /**
     * Access Control rules, configured in a JSON file.
     */
    private AccessControlRules accessControlRules;

    /**
     * Access Control rules configuration JSON file ($HOME/.acm/accessControlRules.json)
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
     * Check if particular user is granted access to a given object. This is accomplished with iterating over all
     * configured AC entries until the first positive match
     *
     * @param authentication authentication token
     * @param targetId the identifier for the object instance
     * @param targetType target type
     * @param permission required permission
     * @param solrDocument Solr data stored for this object
     * @return true if user is allowed to access this object, false otherwise
     */
    @Override
    public boolean isAccessGranted(Authentication authentication, Long targetId, String targetType, String permission, String solrDocument)
    {
        if (accessControlRules == null || accessControlRules.getAccessControlRuleList() == null)
        {
            log.warn("Missing access control rules configuration");
            return false;
        }
        log.debug("Checking if [{}] is granted executing [{}] on object of type [{}] with id [{}]", authentication.getName(), permission,
                targetType, targetId);
        boolean granted = false;

        Map<String, Object> targetObjectProperties = retrieveTargetObjectProperties(accessControlRules.getPropertiesMapping(),
                solrDocument);

        // loop trough configured access control rules, break on first positive match
        for (AccessControlRule accessControlRule : accessControlRules.getAccessControlRuleList())
        {
            // check if action name matches
            if (permission == null || !permission.equals(accessControlRule.getActionName()))
            {
                log.trace("Non matching permission [{} != {}], ignoring", accessControlRule.getActionName(), permission);
                continue;
            }
            // check if target type matches
            if (targetType == null || !targetType.equals(accessControlRule.getObjectType()))
            {
                log.trace("Non matching target type [{} != {}], ignoring", accessControlRule.getObjectType(), targetType);
                continue;
            }
            // check if target sub type matches (NOTE: it is optional in JSON rules structure)
            if (accessControlRule.getObjectSubType() != null)
            {
                // FIXME: unsafe - "object_sub_type_s" to "objectSubType" mapping has to be defined in
                // accessControlRules.json
                String targetSubType = (String) targetObjectProperties.get("objectSubType");
                if (targetSubType == null || !targetSubType.equals(accessControlRule.getObjectSubType()))
                {
                    log.trace("Non matching target sub type [{} != {}], ignoring", accessControlRule.getObjectSubType(), targetSubType);
                    continue;
                }
            }
            // check if "ALL" roles match
            if (!checkRolesAll(accessControlRule.getUserRolesAll(), authentication.getAuthorities(), targetObjectProperties))
            {
                // log entry created in checkRolesAll() method
                continue;
            }
            // check if "ANY" roles match
            if (!checkRolesAny(accessControlRule.getUserRolesAny(), authentication.getAuthorities(), targetObjectProperties))
            {
                // log entry created in checkRolesAny() method
                continue;
            }
            // all initial checks passed, proceed with checking required object properties
            granted = evaluate(accessControlRule.getObjectProperties(), authentication, targetObjectProperties);
            if (granted)
            {
                log.debug("[{}] is granted executing [{}] on object of type [{}] with id [{}], matching rule [{}]",
                        authentication.getName(), permission, targetType, targetId, accessControlRule);
                break;
            }
        }
        if (!granted)
        {
            log.warn("[{}] is denied executing [{}] on object of type [{}] with id [{}], no matching rule found", authentication.getName(),
                    permission, targetType, targetId);
        }
        return granted;
    }

    /**
     * Retrieve target object properties by parsing Solr document stored for this object.
     *
     * @param solrDocument Solr data stored for this object
     * @return map of retrieved properties
     */
    private Map<String, Object> retrieveTargetObjectProperties(Map<String, String> propertiesMapping, String solrDocument)
    {
        Map<String, Object> targetObjectProperties = new HashMap<>();

        JSONObject jsonObject = new JSONObject(solrDocument);
        // extract the doc element of Solr response
        JSONObject solrProperties = jsonObject.getJSONObject("response").getJSONArray("docs").getJSONObject(0);
        Iterator<String> it = solrProperties.keys();
        while (it.hasNext())
        {
            String key = it.next();
            Object value = solrProperties.get(key);
            // add to map only if Solr property name mapping exists
            if (propertiesMapping.containsKey(key))
            {
                String name = propertiesMapping.get(key);
                targetObjectProperties.put(name, value);
            }
        }
        return targetObjectProperties;
    }

    /**
     * Check if all of the "ALL" roles match.
     *
     * @param userRolesAll list of "ALL" roles
     * @param grantedAuthorities list of granted authorities for the user
     * @param targetObjectProperties target object properties (retrieved from Solr)
     * @return true if all of the roles are assigned to the user, false if any of the roles is missing
     */
    private boolean checkRolesAll(List<String> userRolesAll, Collection<? extends GrantedAuthority> grantedAuthorities,
            Map<String, Object> targetObjectProperties)
    {
        if (userRolesAll == null || userRolesAll.isEmpty())
        {
            // no user roles requested
            return true;
        }
        // all roles must match
        for (String userRole : userRolesAll)
        {
            // replace placeholders, if any
            userRole = evaluateRole(userRole, targetObjectProperties);
            boolean found = false;
            for (GrantedAuthority authority : grantedAuthorities)
            {
                if (userRole.equalsIgnoreCase(authority.getAuthority()))
                {
                    found = true;
                    // if any of the granted roles does match, continue with the next role
                    log.debug("Found \"ALL\" matching user role [{}]", userRole);
                    break;
                }
            }
            if (!found)
            {
                // some of the "ALL" roles missing, fail this check
                log.warn("One of the \"ALL\" user roles [{}] is not assigned to user", userRole);
                return false;
            }
        }
        return true;
    }

    /**
     * Check if any of the "ANY" roles match.
     *
     * @param userRolesAny list of "ANY" roles
     * @param grantedAuthorities list of granted authorities for the user
     * @param targetObjectProperties target object properties (retrieved from Solr)
     * @return true if any of the roles are assigned to the user, false if none of the roles are assigned
     */
    private boolean checkRolesAny(List<String> userRolesAny, Collection<? extends GrantedAuthority> grantedAuthorities,
            Map<String, Object> targetObjectProperties)
    {
        if (userRolesAny == null || userRolesAny.isEmpty())
        {
            // no user roles requested
            return true;
        }
        // any role can match
        for (String userRole : userRolesAny)
        {
            // replace placeholders, if any
            userRole = evaluateRole(userRole, targetObjectProperties);
            for (GrantedAuthority authority : grantedAuthorities)
            {
                if (userRole.equalsIgnoreCase(authority.getAuthority()))
                {
                    // if any of the granted roles does match, break immediately and return true
                    log.debug("Found \"ANY\" matching user role [{}]", userRole);
                    return true;
                }
            }
        }
        // none of the "ANY" roles are assigned to the user
        log.warn("None of the \"ANY\" user roles are assigned to user");
        return false;
    }

    /**
     * Replace placeholders in user role (marked as {{placeholder}})
     *
     * @param userRole user role string, as defined in access control rules
     * @param targetObjectProperties target object properties (retrieved from Solr)
     * @return evaluated user role
     */
    private String evaluateRole(String userRole, Map<String, Object> targetObjectProperties)
    {
        Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(userRole);
        while (matcher.find())
        {
            String propertyName = matcher.group(1);
            String propertyValue = (String) targetObjectProperties.get(propertyName);
            if (propertyValue == null)
            {
                continue;
            }
            userRole = matcher.replaceFirst(propertyValue);
            matcher = pattern.matcher(userRole);
        }
        return userRole;
    }

    /**
     * Evaluate single AC rule.
     *
     * @param requiredProperties required object properties used in permission checking (read from configuration file)
     * @param authentication authentication token
     * @param targetObjectProperties target object properties (retrieved from Solr)
     * @return evaluated AC rule
     */
    private boolean evaluate(Map<String, Object> requiredProperties, Authentication authentication,
            Map<String, Object> targetObjectProperties)
    {
        if (requiredProperties == null || requiredProperties.isEmpty())
        {
            // no required properties
            return true;
        }
        Optional<Entry<String, Object>> result = requiredProperties.entrySet().stream().filter(requiredProperty ->
        {
            String key = requiredProperty.getKey();
            Object value = targetObjectProperties.get(key);
            Object expectedValue = requiredProperty.getValue();
            return value == null || !value.equals(expectedValue);
        }).peek(entry -> log.warn("Object property [{}] does not match expected value [{} != {}]", entry.getKey(),
                targetObjectProperties.get(entry.getKey()), entry.getValue())).findFirst();
        
        return !result.isPresent();
    }

    @Override
    public AccessControlRules getAccessControlRules()
    {
        return accessControlRules;
    }

    @Override
    public void setAccessControlRules(AccessControlRules accessControlRules)
    {
        this.accessControlRules = accessControlRules;
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
