package com.armedia.acm.plugins.admin.service;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import static com.armedia.acm.plugins.admin.model.RolePrivilegesConstants.ROLE_PREFIX;

import com.armedia.acm.configuration.service.CollectionPropertiesConfigurationService;
import com.armedia.acm.configuration.service.ConfigurationPropertyException;
import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.configuration.util.MergeFlags;
import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import com.armedia.acm.plugins.admin.model.PrivilegeItem;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.ApplicationPrivilegesConfig;
import com.armedia.acm.services.users.model.ApplicationRolesConfig;
import com.armedia.acm.services.users.model.ApplicationRolesToPrivilegesConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TransactionRequiredException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Created by admin on 6/3/15.
 */
public class RolesPrivilegesService
{
    private Logger log = LogManager.getLogger(getClass());
    private String applicationRolesPrivilegesTemplatesLocation;
    private String applicationRolesPrivilegesTemplateFile;
    private String applicationRolesPrivilegesFile;
    private UserDao userDao;
    private ApplicationRolesConfig rolesConfig;
    private ApplicationPrivilegesConfig privilegesConfig;
    private ApplicationRolesToPrivilegesConfig rolesToPrivilegesConfig;
    private ConfigurationPropertyService configurationPropertyService;
    private CollectionPropertiesConfigurationService collectionPropertiesConfigurationService;

    /**
     * Retrieve list of roles
     * 
     * @return list of roles
     */
    public List<String> retrieveRoles()
    {
        return rolesConfig.getApplicationRoles();
    }

    /**
     * Retrieve application's privileges
     *
     * @return map of privileges and descriptions
     * @throws AcmRolesPrivilegesException
     */
    public Map<String, String> retrievePrivileges()
    {
        return privilegesConfig.getApplicationPrivileges();
    }

    /**
     * Retrieve application's privileges by authorization
     *
     * @return map of privileges and descriptions
     */
    public Map<String, Object> retrievePrivilegesByAuthorization(Boolean authorized, String roleName)
    {
        Map<String, Object> privilegesByRole = loadRolePrivileges(roleName);
        if (authorized)
        {
            return privilegesByRole;
        }
        else
        {
            Map<String, String> allPrivileges = retrievePrivileges();
            return allPrivileges.entrySet().stream()
                    .filter(entry -> !privilegesByRole.containsKey(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    /**
     * Retrieve application's privileges for an application role paged
     *
     * @return list of objects (PrivilegeItem)
     */
    public List<PrivilegeItem> getPrivilegesByRolePaged(String roleName, String sortDirection, Integer startRow, Integer maxRows,
            Boolean authorized)
    {
        Map<String, Object> privileges = retrievePrivilegesByAuthorization(authorized, roleName);

        return getPrivilegesPaged(privileges, sortDirection, startRow, maxRows, "");
    }

    /**
     * Retrieve application's privileges for an application role by role name
     *
     * @return list of objects (PrivilegeItem)
     */
    public List<PrivilegeItem> getPrivilegesByRole(String roleName, Boolean authorized, String filterName, String sortDirection,
            Integer startRow, Integer maxRows)
    {
        Map<String, Object> privileges = retrievePrivilegesByAuthorization(authorized, roleName);

        return getPrivilegesPaged(privileges, sortDirection, startRow, maxRows, filterName);
    }

    public List<PrivilegeItem> getPrivilegesPaged(Map<String, Object> privileges, String sortDirection,
            Integer startRow, Integer maxRows, String filterName)
    {
        List<PrivilegeItem> result = privileges.entrySet().stream()
                .map(privilege -> new PrivilegeItem(privilege.getKey(), (String) privilege.getValue()))
                .collect(Collectors.toList());

        if (sortDirection.contains("DESC"))
        {
            Collections.sort(result, Collections.reverseOrder());
        }
        else
        {
            Collections.sort(result);
        }

        if (startRow > result.size())
        {
            return result;
        }
        maxRows = maxRows > privileges.size() ? privileges.size() : maxRows;

        if (!filterName.isEmpty())
        {
            result.removeIf(privilegeItem -> !(privilegeItem.getValue().toLowerCase().contains(filterName.toLowerCase())));
        }

        return result.stream().skip(startRow).limit(maxRows).collect(Collectors.toList());
    }

    /**
     * Retrieves role's privileges
     *
     * @return map of privileges and descriptions
     */
    public Map<String, Object> retrieveRolePrivileges(String roleName)
    {
        return loadRolePrivileges(roleName);
    }

    /**
     * Retrieve roles of privilege
     *
     * @param privilegeName
     * @return
     */
    public List<String> retrieveRolesByPrivilege(String privilegeName)
    {

        Map<String, List<Object>> rolePrivileges = rolesToPrivilegesConfig.getRolesToPrivileges();
        return rolePrivileges.entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(privilegeName))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve list of roles filtered by name & paged
     *
     * @param privilegeName
     * @return
     */
    public List<String> getRolesByNamePaged(String privilegeName, String sortBy, String sortDirection, Integer startRow, Integer maxRows,
            Boolean authorized, String filterName)
    {
        List<String> rolesByPrivilege = retrieveRolesByPrivilege(privilegeName);
        List<String> allRoles = retrieveRoles();
        List<String> rolesByPrivilegePaged;

        if (authorized)
        {
            rolesByPrivilegePaged = new ArrayList<>(rolesByPrivilege);
        }
        else
        {
            rolesByPrivilegePaged = allRoles.stream().filter(role -> !rolesByPrivilege.contains(role)).collect(Collectors.toList());
        }

        if (sortDirection.contains("DESC"))
        {
            rolesByPrivilegePaged.sort(Collections.reverseOrder());
        }
        else
        {
            Collections.sort(rolesByPrivilegePaged);
        }

        if (startRow > rolesByPrivilegePaged.size())
        {
            return rolesByPrivilegePaged;
        }
        maxRows = maxRows > rolesByPrivilegePaged.size() ? rolesByPrivilegePaged.size() : maxRows;

        if (!filterName.isEmpty())
        {
            rolesByPrivilegePaged.removeIf(role -> !(role.toLowerCase().contains(filterName.toLowerCase())));
        }

        return rolesByPrivilegePaged.stream().skip(startRow).limit(maxRows).collect(Collectors.toList());
    }

    /**
     * Update Role Privileges
     *
     * @param roleName
     *            Updated role name
     * @param privileges
     *            List of role's privileges
     */
    public void updateRolePrivileges(String roleName, List<Object> privileges) throws AcmRolesPrivilegesException
    {
        // Check if role present in system
        List<String> roles = retrieveRoles();
        if (!roles.contains(roleName))
        {
            throw new AcmRolesPrivilegesException(String.format("Can't update role's privileges. Role '%s' is absent", roleName));
        }

        // Save new role privileges
        try
        {
            Map<String, List<Object>> rolePrivileges = addRolePrivileges(roleName, privileges);
            updateRolesPrivilegesConfig(rolePrivileges);
        }
        catch (ConfigurationPropertyException e)
        {
            log.error("Failed to update privileges for role [{}]. {}", roleName, e.getMessage());
            throw new AcmRolesPrivilegesException("Failed to update privileges for role " + roleName, e);
        }

        // Re-generate Roles Privileges XML file

    }

    /**
     * Update Role Privilege
     *
     * @param roleName
     *            Updated role name
     * @param privileges
     *            List of role's privileges
     */
    public void savePrivilegesToApplicationRole(String roleName, List<Object> privileges) throws AcmRolesPrivilegesException
    {
        // Check if role present in system
        boolean rolePresent = retrieveRoles().stream().anyMatch(role -> role.equalsIgnoreCase(roleName));

        if (!rolePresent)
        {
            throw new AcmRolesPrivilegesException(String.format("Can't update role's privileges. Role '%s' is absent",
                    roleName));
        }

        // Save new role privileges
        try
        {
            Map<String, List<Object>> rolePrivileges = addRolePrivileges(roleName, privileges);
            // Re-generate Roles Privileges XML file
            updateRolesPrivilegesConfig(rolePrivileges);
        }
        catch (ConfigurationPropertyException e)
        {
            log.error("Failed to update privileges for role [{}]. {}", roleName, e.getMessage());
            throw new AcmRolesPrivilegesException("Failed to update privileges for role " + roleName, e);
        }


    }

    /**
     * Create new role
     *
     * @param roleName
     *            new role name
     * @throws AcmRolesPrivilegesException
     */
    @Transactional(rollbackFor = AcmRolesPrivilegesException.class)
    public void createRole(String roleName) throws AcmRolesPrivilegesException
    {
        roleName = roleName.toUpperCase().replaceAll("\\s+", "_");
        if (!roleName.startsWith(ROLE_PREFIX))
        {
            roleName = ROLE_PREFIX + roleName;
        }

        List<String> roles = retrieveRoles();
        // Check if new role presents in roles file
        if (roles.contains(roleName))
        {
            throw new AcmRolesPrivilegesException(String.format("Role '%s' already exists", roleName));
        }

        AcmRole acmRole = new AcmRole();
        acmRole.setRoleName(roleName);
        acmRole.setRoleType(AcmRoleType.APPLICATION_ROLE);
        try
        {
            userDao.saveAcmRole(acmRole);
            List<Object> rolesToSave = new LinkedList<>();
            rolesToSave.add(roleName);

            Map<String, Object> rolesConfig = collectionPropertiesConfigurationService.updateListEntry(
                    ApplicationRolesConfig.MERGE_APPLICATION_ROLES_OP, "roles",
                    rolesToSave,
                    MergeFlags.MERGE);

            configurationPropertyService.updateProperties(rolesConfig);

        }
        catch (IllegalArgumentException | TransactionRequiredException | ConfigurationPropertyException e)
        {
            throw new AcmRolesPrivilegesException("Failed to save new role [" + roleName + "]", e);
        }
    }

    @Transactional(rollbackFor = AcmRolesPrivilegesException.class)
    public void updateRole(String roleName, String newRoleName) throws AcmRolesPrivilegesException
    {
        List<String> roles = retrieveRoles();

        if (!roles.contains(roleName))
        {
            throw new AcmRolesPrivilegesException(String.format("Role '%s' doesn't exist", roleName));
        }
        else
        {
            userDao.deleteAcmRole(roleName);
            AcmRole acmRole = new AcmRole();
            acmRole.setRoleName(newRoleName);
            acmRole.setRoleType(AcmRoleType.APPLICATION_ROLE);
            userDao.saveAcmRole(acmRole);
            try
            {
                roles.add(newRoleName);
                roles.remove(roleName);

                Map<String, List<Object>> rolesPrivileges = rolesToPrivilegesConfig.getRolesToPrivileges();

                List<Object> value = rolesPrivileges.get(roleName);

                List<Object> rolesToRemove = new LinkedList<>();
                rolesToRemove.add(roleName);
                List<Object> rolesToSave = new LinkedList<>();
                rolesToSave.add(newRoleName);

                Map<String, Object> rolesPrivilegesConfig = collectionPropertiesConfigurationService.updateAndRemoveMapProperties(
                        ApplicationRolesToPrivilegesConfig.ROLES_TO_PRIVILEGES_PROP_KEY, roleName, newRoleName, value, value);

                Map<String, Object> rolesConfig = collectionPropertiesConfigurationService.updateAndRemoveListProperties(
                        ApplicationRolesConfig.MERGE_APPLICATION_ROLES_OP,
                        "roles", rolesToRemove, rolesToSave);

                Map<String, Object> configMap = new HashMap<>(rolesPrivilegesConfig);
                configMap.putAll(rolesConfig);

                configurationPropertyService.updateProperties(configMap);
            }
            catch (ConfigurationPropertyException e)
            {
                throw new AcmRolesPrivilegesException(String.format("Failed to update role [{}] to [{}]",
                        roleName, newRoleName), e);
            }
        }
    }

    /**
     * Add privileges to list of roles
     *
     * @param roles
     * @param newPrivileges
     */
    public void addRolesPrivileges(List<String> roles, List<Object> newPrivileges) throws AcmRolesPrivilegesException
    {
        try
        {
            Map<String, List<Object>> rolesPrivileges = rolesToPrivilegesConfig.getRolesToPrivileges();
            for (String role : roles)
            {
                Map<String, Object> rolesPrivilegesConfig = collectionPropertiesConfigurationService.updateMapProperty(
                        ApplicationRolesToPrivilegesConfig.ROLES_TO_PRIVILEGES_PROP_KEY,
                        role,
                        newPrivileges,
                        MergeFlags.MERGE);

                configurationPropertyService.updateProperties(rolesPrivilegesConfig);

                // Search role name in role-privileges maps
                List<Object> propPrivileges = rolesPrivileges.get(role);
                List<Object> privileges = new LinkedList<>();
                if (propPrivileges != null && !propPrivileges.isEmpty())
                {
                    privileges.addAll(propPrivileges);
                }

                for (Object newPrivilege : newPrivileges)
                {
                    if (!privileges.contains(newPrivilege))
                    {
                        privileges.add(newPrivilege);
                    }
                }
                rolesPrivileges.put(role, privileges);
            }
            updateRolesPrivilegesConfig(rolesPrivileges);
        }
        catch (Exception e)
        {
            log.error("Can't add roles to privileges", e);
            throw new AcmRolesPrivilegesException("Can't add roles to privileges", e);
        }
    }

    /**
     * Remove privileges form list of roles
     *
     * @param roles
     * @param removedPrivileges
     */
    public void removeRolesPrivileges(List<String> roles, List<Object> removedPrivileges) throws AcmRolesPrivilegesException
    {
        try
        {
            Map<String, List<Object>> rolesPrivileges = rolesToPrivilegesConfig.getRolesToPrivileges();

            for (String role : roles)
            {

                Map<String, Object> rolesPrivilegesConfig = collectionPropertiesConfigurationService.updateMapProperty(
                        ApplicationRolesToPrivilegesConfig.ROLES_TO_PRIVILEGES_PROP_KEY,
                        role,
                        removedPrivileges,
                        MergeFlags.REMOVE);

                configurationPropertyService.updateProperties(rolesPrivilegesConfig);

                // Search role name in role-privileges maps
                List<Object> propPrivileges = rolesPrivileges.get(role);
                List<Object> privileges = new LinkedList<>();
                if (propPrivileges != null && !propPrivileges.isEmpty())
                {
                    privileges.addAll(propPrivileges);
                }

                for (Object removedPrivilege : removedPrivileges)
                {
                    int foundIndex = privileges.indexOf(removedPrivilege);
                    if (foundIndex != -1)
                    {
                        privileges.remove(foundIndex);
                    }
                }
                rolesPrivileges.put(role, new ArrayList<>(privileges));
            }

            updateRolesPrivilegesConfig(rolesPrivileges);
        }
        catch (Exception e)
        {
            log.error("Can't remove privileges from roles", e);
            throw new AcmRolesPrivilegesException("Can't remove privileges from roles", e);
        }
    }

    /**
     * Load specific role's privileges
     *
     * @param roleName
     *            Role name
     * @return map of privileges and descriptions
     */
    private Map<String, Object> loadRolePrivileges(String roleName)
    {
        Map<String, Object> rolePrivileges = new HashMap<>();
        Map<String, List<Object>> rolePrivilegesMapping = rolesToPrivilegesConfig.getRolesToPrivileges();

        // Search roleName in role-privileges maps
        List<Object> privileges = rolePrivilegesMapping.get(roleName);
        if (privileges != null)
        {
            // Get all privileges with descriptions
            Map<String, String> privilegeDescriptionMapping = privilegesConfig.getApplicationPrivileges();

            // Combine privileges and descriptions into one map
            for (Object privilegeIter : privileges)
            {
                String privilegeDescription = privilegeDescriptionMapping.getOrDefault(privilegeIter, (String) privilegeIter);
                rolePrivileges.put((String) privilegeIter, privilegeDescription);
            }
        }
        return rolePrivileges;
    }

    /**
     * Save role's privileges to the file
     * 
     * @param roleName
     *            Role name
     * @param privilegesToAdd
     */
    private Map<String, List<Object>> addRolePrivileges(String roleName, List<Object> privilegesToAdd)
            throws ConfigurationPropertyException
    {
        Map<String, List<Object>> rolesPrivileges = rolesToPrivilegesConfig.getRolesToPrivileges();

        Map<String, Object> rolesPrivilegesConfig = collectionPropertiesConfigurationService.updateMapProperty(
                ApplicationRolesToPrivilegesConfig.ROLES_TO_PRIVILEGES_PROP_KEY,
                roleName,
                privilegesToAdd,
                MergeFlags.MERGE);

        configurationPropertyService.updateProperties(rolesPrivilegesConfig);

        List<Object> privileges = rolesPrivileges.get(roleName);

        Set<Object> noDuplicatePrivileges = new HashSet<>();

        if (privileges != null)
        {
            noDuplicatePrivileges.addAll(privileges);
        }

        if (noDuplicatePrivileges == null)
        {
            rolesPrivileges.put(roleName, privilegesToAdd);
        }
        else
        {
            for (Object prvilege : privilegesToAdd)
            {
                if (!noDuplicatePrivileges.contains(prvilege))
                {
                    noDuplicatePrivileges.add(prvilege);
                }
            }
            rolesPrivileges.put(roleName, new ArrayList<>(noDuplicatePrivileges));
        }

        return rolesPrivileges;
    }

    private void updateRolesPrivilegesConfig(Map<String, List<Object>> rolePrivilegesMapping) throws AcmRolesPrivilegesException
    {
        // Create Map of lists. Roles should be grouped by privileges
        Map<String, List<String>> privileges = new HashMap<>();

        for (Object roleKeyIter : rolePrivilegesMapping.keySet())
        {
            String role = (String) roleKeyIter;
            List<Object> rolePrivileges = rolePrivilegesMapping.getOrDefault(role, new ArrayList<>());

            for (Object privilegeIter : rolePrivileges)
            {
                if (!privileges.containsKey(privilegeIter))
                {
                    privileges.put(privilegeIter.toString(), new ArrayList<>());
                }
                // Add Role to map grouped by privilege
                privileges.get(privilegeIter).add(role);
            }
        }

        try
        {
            // Load template and render configuration file
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setDirectoryForTemplateLoading(new File(applicationRolesPrivilegesTemplatesLocation));
            Template tmpl = cfg.getTemplate(applicationRolesPrivilegesTemplateFile);

            try (OutputStream applicationOutputStream = new FileOutputStream(new File(applicationRolesPrivilegesFile));
                    Writer writer = new BufferedWriter(new OutputStreamWriter(applicationOutputStream, StandardCharsets.UTF_8)))
            {
                tmpl.process(privileges, writer);
            }

        }
        catch (Exception e)
        {
            log.error("Can't update roles privileges config file", e);
            throw new AcmRolesPrivilegesException("Can't update roles privileges config file", e);
        }
    }

    public void setApplicationRolesPrivilegesTemplatesLocation(String applicationRolesPrivilegesTemplatesLocation)
    {
        this.applicationRolesPrivilegesTemplatesLocation = applicationRolesPrivilegesTemplatesLocation;
    }

    public void setApplicationRolesPrivilegesTemplateFile(String applicationRolesPrivilegesTemplateFile)
    {
        this.applicationRolesPrivilegesTemplateFile = applicationRolesPrivilegesTemplateFile;
    }

    public void setApplicationRolesPrivilegesFile(String applicationRolesPrivilegesFile)
    {
        this.applicationRolesPrivilegesFile = applicationRolesPrivilegesFile;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public ApplicationRolesConfig getRolesConfig()
    {
        return rolesConfig;
    }

    public void setRolesConfig(ApplicationRolesConfig rolesConfig)
    {
        this.rolesConfig = rolesConfig;
    }

    public ApplicationPrivilegesConfig getPrivilegesConfig()
    {
        return privilegesConfig;
    }

    public void setPrivilegesConfig(ApplicationPrivilegesConfig privilegesConfig)
    {
        this.privilegesConfig = privilegesConfig;
    }

    public ApplicationRolesToPrivilegesConfig getRolesToPrivilegesConfig()
    {
        return rolesToPrivilegesConfig;
    }

    public void setRolesToPrivilegesConfig(ApplicationRolesToPrivilegesConfig rolesToPrivilegesConfig)
    {
        this.rolesToPrivilegesConfig = rolesToPrivilegesConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }

    public void setCollectionPropertiesConfigurationService(
            CollectionPropertiesConfigurationService collectionPropertiesConfigurationService)
    {
        this.collectionPropertiesConfigurationService = collectionPropertiesConfigurationService;
    }
}
