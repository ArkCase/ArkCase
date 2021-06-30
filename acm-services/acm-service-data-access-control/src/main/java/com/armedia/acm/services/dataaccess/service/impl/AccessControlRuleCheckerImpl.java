package com.armedia.acm.services.dataaccess.service.impl;

/*-
 * #%L
 * ACM Service: Data Access Control
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ACM_PARTICIPANTS_LCS;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.dataaccess.model.AccessControlRule;
import com.armedia.acm.services.dataaccess.model.AccessControlRules;
import com.armedia.acm.services.dataaccess.model.DataAccessControlConfig;
import com.armedia.acm.services.dataaccess.service.AccessControlRuleChecker;
import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private ObjectConverter objectConverter;

    private DataAccessControlConfig dacConfig;

    /**
     * Logger instance.
     */
    private Logger log = LogManager.getLogger(getClass());

    /**
     * Load the access control rules
     */
    public void postConstruct()
    {
        log.debug("Creating access control rules from [{}]", configurationFile.getAbsolutePath());
        try
        {
            accessControlRules = getObjectConverter().getJsonUnmarshaller().unmarshall(FileUtils.readFileToString(configurationFile),
                    AccessControlRules.class);
        }
        catch (IOException e)
        {
            log.error("Unable to create access control rules from [{}]", configurationFile.getAbsolutePath(), e);
        }
    }

    /**
     * Check if particular user is granted access to a given object. This is accomplished with iterating over all
     * configured AC entries until the first positive match. It only requires one permission to match, from the list of
     * required permissions
     *
     * @param authentication
     *            authentication token
     * @param targetId
     *            the identifier for the object instance
     * @param targetType
     *            target type
     * @param permission
     *            required permission
     * @param solrDocument
     *            Solr data stored for this object
     * @return true if user is allowed to access this object, false otherwise
     */
    @Override
    public boolean isAccessGranted(Authentication authentication, Long targetId, String targetType, String permission, String solrDocument)
    {
        return isAccessGranted(authentication, targetId, targetType, Arrays.asList(permission.split("\\|")), solrDocument);
    }

    private boolean isAccessGranted(Authentication authentication, Long targetId, String targetType, List<String> permissions,
            String solrDocument)
    {
        if (accessControlRules == null || accessControlRules.getAccessControlRuleList() == null)
        {
            log.warn("Missing access control rules configuration");
            return false;
        }
        log.debug("Checking if [{}] is granted executing [{}] on object of type [{}] with id [{}]", authentication.getName(),
                StringUtils.join(permissions, ","), targetType, targetId);
        boolean granted = false;

        JSONObject solrJsonDocument = new JSONObject(solrDocument);
        JSONObject solrJsonResult = solrJsonDocument.getJSONObject("response").getJSONArray("docs").getJSONObject(0);

        Map<String, Object> targetObjectProperties = retrieveTargetObjectProperties(accessControlRules.getPropertiesMapping(),
                solrJsonResult);

        for (String permission : permissions)
        {

            List<AccessControlRule> permissionRules = accessControlRules.getAccessControlRuleList().stream().filter(rule -> {
                if (targetType.equals(rule.getObjectType()) && permission.equals(rule.getActionName()))
                {
                    if (rule.getObjectSubType() != null) // optional field in rule
                    {
                        String targetSubType = (String) targetObjectProperties.get("objectSubType");
                        if (targetSubType == null || !targetSubType.equals(rule.getObjectSubType()))
                        {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            if (permissionRules.size() == 0)
            { // no permissions found add fallback parent permission
                String fallbackPermission = getFallbackPermissionName(permission);
                permissionRules = accessControlRules.getAccessControlRuleList().stream()
                        .filter(rule -> rule.getObjectType().contains(targetType) && rule.getActionName().equals(fallbackPermission))
                        .collect(Collectors.toList());
            }
            for (AccessControlRule rule : permissionRules)
            {
                if (!checkRolesAll(rule.getUserRolesAll(), authentication.getAuthorities(), targetObjectProperties))
                {
                    // log entry created in checkRolesAll() method
                    continue;
                }
                // check if "ANY" roles match
                if (!checkRolesAny(rule.getUserRolesAny(), authentication.getAuthorities(), targetObjectProperties))
                {
                    // log entry created in checkRolesAny() method
                    continue;
                }

                if (evaluate(rule.getObjectProperties(), authentication, targetObjectProperties)
                        && checkParticipantTypes(rule.getUserIsParticipantTypeAny(), authentication, solrJsonResult))
                {
                    granted = true;
                    log.debug("[{}] is granted executing [{}] on object of type [{}] with id [{}], matching rule [{}]",
                            authentication.getName(), permission, targetType, targetId, rule);
                    break;
                }
            }
            if (granted)
            {
                break;
            }
        }

        if (!granted)
        {
            log.warn("[{}] is denied executing [{}] on object of type [{}] with id [{}], no matching rule found", authentication.getName(),
                    StringUtils.join(permissions, ","), targetType, targetId);
        }
        return granted;
    }

    private String getFallbackPermissionName(String permission)
    {
        String parentActionName = null;
        if (permission.toLowerCase().matches("(" + dacConfig.getFallbackGetObjectExpression() + ").*"))
        {
            // read parent permission
            parentActionName = "getObject";
        }
        else if (permission.toLowerCase()
                .matches("(" + dacConfig.getFallbackEditObjectExpression() + ").*"))
        {
            // write parent permission
            parentActionName = "editObject";
        }
        else if (permission.toLowerCase().matches("(" + dacConfig.getFallbackInsertObjectExpression() + ").*"))
        {
            // insert parent permission
            parentActionName = "insertObject";
        }
        else if (permission.toLowerCase().matches("(" + dacConfig.getFallbackDeleteObjectExpression() + ").*"))
        {
            // delete parent permission
            parentActionName = "deleteObject";
        }
        return parentActionName;
    }

    /**
     * If userIsParticipantTypeAny in AC is defined, evaluate if principal is any of the defined participant's types
     *
     * @param userIsParticipantTypeAny
     *            list of "ANY" participants
     * @param authentication
     *            Authentication token
     * @param solrJsonResult
     *            JSONObject of parsed Solr documents response
     * @return true if principal is any of the defined participant's types
     */
    private boolean checkParticipantTypes(List<String> userIsParticipantTypeAny, Authentication authentication, JSONObject solrJsonResult)
    {
        if (userIsParticipantTypeAny != null && !userIsParticipantTypeAny.isEmpty())
        {
            // if participants defined
            if (solrJsonResult.has(ACM_PARTICIPANTS_LCS))
            {
                log.debug("Checking if {} is a participant of type in 'userIsParticipantTypeAny' list", authentication.getName());
                JSONArray securityParticipants = new JSONArray(solrJsonResult.getString(ACM_PARTICIPANTS_LCS));
                Set<String> participantsOfTypeAny = getParticipantsOfType(securityParticipants, userIsParticipantTypeAny);
                return participantsOfTypeAny.stream().anyMatch(ldapId -> isParticipantAnyOf(ldapId, authentication));
            }
        }
        log.debug("No participants to be checked, returning true");
        // there are no participants that need to be checked, return true
        return true;
    }

    /**
     * Get set of all participant's ids whose types are in defined participant's types
     *
     * @param participants
     *            Participants of CASE/COMPLAINT from SOLR result
     * @param userIsParticipantTypeAny
     *            list of "ANY" participants
     * @return set of all participant's ids whose types are found in the list userIsParticipantTypeAny
     */
    private Set<String> getParticipantsOfType(JSONArray participants, List<String> userIsParticipantTypeAny)
    {
        return IntStream.range(0, participants.length()).mapToObj(participants::getJSONObject)
                .filter(jsonObject -> userIsParticipantTypeAny.contains(jsonObject.getString("type")))
                .peek(it -> log.debug("Participant [{}:{}] is in required participant any list",
                        it.getString("type"), it.getString("ldapId")))
                .map(jsonObject -> jsonObject.getString("ldapId")).collect(Collectors.toSet());
    }

    /**
     * Check if principal is the required participant or is member of the required group
     *
     * @param participantId
     *            Ldap id of the required participant type
     * @param authentication
     *            Authentication token
     * @return true if principal is the required participant or is in the required group
     */
    private boolean isParticipantAnyOf(String participantId, Authentication authentication)
    {
        String principalId = authentication.getName();
        Set<String> principalAuthorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        boolean isParticipantPrincipal = participantId.equals(principalId);
        boolean isPrincipalInGroup = principalAuthorities.contains(participantId);
        return isPrincipalInGroup || isParticipantPrincipal;
    }

    /**
     * Retrieve target object properties by parsing Solr document stored for this object.
     *
     * @param solrJsonResult
     *            JSONObject of parsed Solr documents response
     * @return map of retrieved properties
     */
    private Map<String, Object> retrieveTargetObjectProperties(Map<String, String> propertiesMapping, JSONObject solrJsonResult)
    {
        Map<String, Object> targetObjectProperties = new HashMap<>();

        // extract the doc element of Solr response
        Iterator<String> it = solrJsonResult.keys();
        while (it.hasNext())
        {
            String key = it.next();
            Object value = solrJsonResult.get(key);
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
     * @param userRolesAll
     *            list of "ALL" roles
     * @param grantedAuthorities
     *            list of granted authorities for the user
     * @param targetObjectProperties
     *            target object properties (retrieved from Solr)
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
                if (evaluateAuthorityWildcardRole(userRole, authority.getAuthority()))
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

    private boolean evaluateAuthorityWildcardRole(String role, String authority)
    {
        if (StringUtils.endsWith(role, AcmRoleToGroupMapping.GROUP_NAME_WILD_CARD))
        {
            String roleName = StringUtils.substringBeforeLast(role, "@");
            String authorityName = StringUtils.substringBeforeLast(authority, "@");
            return authorityName.equalsIgnoreCase(roleName);
        }
        else
        {
            return role.equalsIgnoreCase(authority);
        }
    }

    /**
     * Check if any of the "ANY" roles match.
     *
     * @param userRolesAny
     *            list of "ANY" roles
     * @param grantedAuthorities
     *            list of granted authorities for the user
     * @param targetObjectProperties
     *            target object properties (retrieved from Solr)
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
                if (evaluateAuthorityWildcardRole(userRole, authority.getAuthority()))
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
     * @param userRole
     *            user role string, as defined in access control rules
     * @param targetObjectProperties
     *            target object properties (retrieved from Solr)
     * @return evaluated user role
     */
    private String evaluateRole(String userRole, Map<String, Object> targetObjectProperties)
    {
        Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(userRole);
        while (matcher.find())
        {
            String propertyName = matcher.group(1);
            Object propertyValue = targetObjectProperties.get(propertyName);
            if (!(propertyValue instanceof String))
            {
                continue;
            }
            userRole = matcher.replaceFirst((String) propertyValue);
            matcher = pattern.matcher(userRole);
        }
        return userRole;
    }

    /**
     * Evaluate single AC rule.
     *
     * @param requiredProperties
     *            required object properties used in permission checking (read from configuration file)
     * @param authentication
     *            authentication token
     * @param targetObjectProperties
     *            target object properties (retrieved from Solr)
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
        Optional<Entry<String, Object>> result = requiredProperties.entrySet().stream().filter(requiredProperty -> {
            String key = requiredProperty.getKey();
            Object value = targetObjectProperties.get(key);
            Object expectedValue = requiredProperty.getValue();
            if (value == null)
            {
                return true;
            }
            else if (expectedValue.getClass().isAssignableFrom(ArrayList.class))
            {
                ArrayList<String> list = (ArrayList<String>) expectedValue;
                return !list.contains(value);
            }
            else if (expectedValue.getClass().isAssignableFrom(String.class))
            {
                String str = (String) expectedValue;
                return !str.equals(value);
            }
            return false;
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

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public DataAccessControlConfig getDacConfig()
    {
        return dacConfig;
    }

    public void setDacConfig(DataAccessControlConfig dacConfig)
    {
        this.dacConfig = dacConfig;
    }
}
