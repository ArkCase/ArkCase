package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.services.dataaccess.model.AccessControlRule;
import com.armedia.acm.services.dataaccess.model.AccessControlRules;
import com.armedia.acm.services.dataaccess.service.AccessControlRuleChecker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
     * Access Control rules configuration JSON file ($HOME/.acm/accessControlRules.json)
     */
    private File configurationFile;

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Spring expression parser instance.
     */
    private ExpressionParser parser;

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
     * @param permission     required permission
     * @return true if user is allowed to access this object, false otherwise
     */
    public boolean isAccessGranted(Authentication authentication, Long targetId, String targetType, String permission)
    {
        if (accessControlRules.getAccessControlRuleList() == null)
        {
            log.warn("Missing access control rules configuration");
            return false;
        }
        log.debug("Checking if [{}] is granted access to object of type [{}] with id [{}]", authentication.getName(), targetType, targetId);
        boolean granted = false;
        Object targetObject = retrieveObject(targetId, targetType);

        // loop trough configured access control rules, break on first positive match
        for (AccessControlRule accessControlRule : accessControlRules.getAccessControlRuleList())
        {
            // check if action name matches
            if (permission == null || !permission.equals(accessControlRule.getActionName()))
            {
                log.debug("Non matching permission [{} != {}], ignoring", accessControlRule.getActionName(), permission);
                continue;
            }
            // check if target type matches
            if (targetType == null || !targetType.equals(accessControlRule.getObjectType()))
            {
                log.debug("Non matching target type [{} != {}], ignoring", accessControlRule.getObjectType(), targetType);
                continue;
            }
            // check if "ALL" roles match
            if (!checkRolesAll(accessControlRule.getUserRolesAll(), authentication.getAuthorities()))
            {
                // log entry created in checkRolesAll() method
                continue;
            }
            // check if "ANY" roles match
            if (!checkRolesAny(accessControlRule.getUserRolesAny(), authentication.getAuthorities()))
            {
                // log entry created in checkRolesAny() method
                continue;
            }
            // all initial checks passed, proceed with checking required object properties
            granted = evaluate(accessControlRule.getObjectProperties(), authentication, targetObject);
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

    /**
     * Check if all of the "ALL" roles match.
     *
     * @param userRolesAll       list of "ALL" roles
     * @param grantedAuthorities list of granted authorities for the user
     * @return true if all of the roles are assigned to the user, false if any of the roles is missing
     */
    private boolean checkRolesAll(List<String> userRolesAll, Collection<? extends GrantedAuthority> grantedAuthorities)
    {
        if (userRolesAll == null)
        {
            // no user roles requested
            return true;
        }
        // all roles must match
        for (String userRole : userRolesAll)
        {
            boolean found = false;
            for (GrantedAuthority authority : grantedAuthorities)
            {
                if (userRole.equals(authority.getAuthority()))
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
     * @param userRolesAny       list of "ANY" roles
     * @param grantedAuthorities list of granted authorities for the user
     * @return true if any of the roles are assigned to the user, false if none of the roles are assigned
     */
    private boolean checkRolesAny(List<String> userRolesAny, Collection<? extends GrantedAuthority> grantedAuthorities)
    {
        if (userRolesAny == null)
        {
            // no user roles requested
            return true;
        }
        // any role can match
        for (String userRole : userRolesAny)
        {
            for (GrantedAuthority authority : grantedAuthorities)
            {
                if (userRole.equals(authority.getAuthority()))
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
     * Retrieve object used in permission checking.
     *
     * @param targetId   object identifier
     * @param targetType object type
     * @return object retrieved from object storage
     */
    private Object retrieveObject(Long targetId, String targetType)
    {
        // FIXME: how to retrieve the object???
        return new Object();
    }

    /**
     * Evaluate single AC rule.
     *
     * @param requiredProperties required object properties used in permission checking (read from configuration file)
     * @param authentication     authentication token
     * @param targetObject       object which properties we will evaluate
     * @return evaluated AC rule
     */
    private boolean evaluate(Map<String, String> requiredProperties, Authentication authentication, Object targetObject)
    {
        if (requiredProperties == null)
        {
            // no required properties
            return true;
        }
        StandardEvaluationContext context = new StandardEvaluationContext(targetObject);

        for (Map.Entry<String, String> requiredProperty : requiredProperties.entrySet())
        {
            String key = requiredProperty.getKey();
            String value = parser.parseExpression(key).getValue(context, String.class);
            String expectedValue = requiredProperty.getValue();
            if (value == null || !value.equals(expectedValue))
            {
                // some of the required properties do not match, fail this check
                log.warn("Object property [{}] does not match expected value [{} != {}]", key, value, expectedValue);
                return false;
            }
        }
        return true;
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

    public ExpressionParser getParser()
    {
        return parser;
    }

    public void setParser(ExpressionParser parser)
    {
        this.parser = parser;
    }
}
