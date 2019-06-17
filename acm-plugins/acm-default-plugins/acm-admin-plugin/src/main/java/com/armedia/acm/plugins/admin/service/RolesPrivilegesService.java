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

import com.armedia.acm.configuration.service.ConfigurationPropertyException;
import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import com.armedia.acm.plugins.admin.model.PrivilegeItem;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.ApplicationPrivilegesConfig;
import com.armedia.acm.services.users.model.ApplicationRolesConfig;
import com.armedia.acm.services.users.model.ApplicationRolesToPrivilegesConfig;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
        return privilegesConfig.getPrivilegesDescriptions();
    }

    /**
     * Retrieve application's privileges by authorization
     *
     * @return map of privileges and descriptions
     */
    public Map<String, String> retrievePrivilegesByAuthorization(Boolean authorized, String roleName)
    {
        Map<String, String> privilegesByRole = loadRolePrivileges(roleName);
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
        Map<String, String> privileges = retrievePrivilegesByAuthorization(authorized, roleName);

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
        Map<String, String> privileges = retrievePrivilegesByAuthorization(authorized, roleName);

        return getPrivilegesPaged(privileges, sortDirection, startRow, maxRows, filterName);
    }

    public List<PrivilegeItem> getPrivilegesPaged(Map<String, String> privileges, String sortDirection,
            Integer startRow, Integer maxRows, String filterName)
    {
        List<PrivilegeItem> result = privileges.entrySet().stream()
                .map(privilege -> new PrivilegeItem(privilege.getKey(), privilege.getValue()))
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
    public Map<String, String> retrieveRolePrivileges(String roleName)
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
        Map<String, String> rolePrivileges = rolesToPrivilegesConfig.getRolesToPrivileges();
        return rolePrivileges.entrySet()
                .stream()
                .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                .filter(entry -> Arrays.asList(entry.getValue().split(",")).contains(privilegeName))
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
    public void updateRolePrivileges(String roleName, List<String> privileges) throws AcmRolesPrivilegesException
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
            addRolePrivileges(roleName, privileges);
        }
        catch (ConfigurationPropertyException e)
        {
            log.error("Failed to update privileges for role [{}]. {}", roleName, e.getMessage());
            throw new AcmRolesPrivilegesException("Failed to update privileges for role " + roleName, e);
        }

        // Re-generate Roles Privileges XML file
        updateRolesPrivilegesConfig();
    }

    /**
     * Update Role Privilege
     *
     * @param roleName
     *            Updated role name
     * @param privileges
     *            List of role's privileges
     */
    public void savePrivilegesToApplicationRole(String roleName, List<String> privileges) throws AcmRolesPrivilegesException
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
            addRolePrivileges(roleName, privileges);
        }
        catch (ConfigurationPropertyException e)
        {
            log.error("Failed to update privileges for role [{}]. {}", roleName, e.getMessage());
            throw new AcmRolesPrivilegesException("Failed to update privileges for role " + roleName, e);
        }

        // Re-generate Roles Privileges XML file
        updateRolesPrivilegesConfig();
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
            List<String> rolesToSave = new ArrayList<>(roles);
            rolesToSave.add(roleName);
            saveRolesConfig(rolesToSave);
            addRoleToPrivileges(roleName);
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

                saveRolesConfig(roles);
                Map<String, String> rolesPrivileges = rolesToPrivilegesConfig.getRolesToPrivileges();

                String value = rolesPrivileges.get(roleName);
                rolesPrivileges.remove(roleName);
                rolesPrivileges.put(newRoleName, value);
                saveRolesPrivilegesConfig(rolesPrivileges);
            }
            catch (ConfigurationPropertyException e)
            {
                throw new AcmRolesPrivilegesException(String.format("Failed to update role [{}] to [{}]",
                        roleName, newRoleName), e);
            }
        }
    }

    private void addRoleToPrivileges(String roleName) throws AcmRolesPrivilegesException
    {
        Map<String, String> rolesToPrivileges = rolesToPrivilegesConfig.getRolesToPrivileges();
        rolesToPrivileges.putIfAbsent(roleName, "");
        try
        {
            configurationPropertyService.updateProperties(rolesToPrivilegesConfig);
        }
        catch (ConfigurationPropertyException e)
        {
            log.error("Can't save role [{}] to role to privileges configuration", roleName, e);
            throw new AcmRolesPrivilegesException(String.format("Can't save role '%s' to configuration", roleName), e);
        }
    }

    /**
     * Add privileges to list of roles
     *
     * @param roles
     * @param newPrivileges
     */
    public void addRolesPrivileges(List<String> roles, List<String> newPrivileges) throws AcmRolesPrivilegesException
    {
        try
        {
            Map<String, String> rolesPrivileges = rolesToPrivilegesConfig.getRolesToPrivileges();
            for (String role : roles)
            {
                // Search role name in role-privileges maps
                String propPrivileges = rolesPrivileges.get(role);
                List<String> privileges = new LinkedList<>();
                if (propPrivileges != null && !propPrivileges.isEmpty())
                {
                    privileges.addAll(Arrays.asList(propPrivileges.split(",")));
                }

                for (String newPrivilege : newPrivileges)
                {
                    if (!privileges.contains(newPrivilege))
                    {
                        privileges.add(newPrivilege);
                    }
                }
                rolesPrivileges.put(role, String.join(",", privileges));
            }
            saveRolesPrivilegesConfig(rolesPrivileges);
            updateRolesPrivilegesConfig();
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
    public void removeRolesPrivileges(List<String> roles, List<String> removedPrivileges) throws AcmRolesPrivilegesException
    {
        try
        {
            Map<String, String> rolesPrivileges = rolesToPrivilegesConfig.getRolesToPrivileges();

            for (String role : roles)
            {
                // Search role name in role-privileges maps
                String propPrivileges = rolesPrivileges.get(role);
                List<String> privileges = new LinkedList<>();
                if (propPrivileges != null && !propPrivileges.isEmpty())
                {
                    privileges.addAll(Arrays.asList(propPrivileges.split(",")));
                }

                for (String removedPrivilege : removedPrivileges)
                {
                    int foundIndex = privileges.indexOf(removedPrivilege);
                    if (foundIndex != -1)
                    {
                        privileges.remove(foundIndex);
                    }
                }
                rolesPrivileges.put(role, String.join(",", privileges));
            }
            saveRolesPrivilegesConfig(rolesPrivileges);
            updateRolesPrivilegesConfig();
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
    private Map<String, String> loadRolePrivileges(String roleName)
    {
        Map<String, String> rolePrivileges = new HashMap<>();
        Map<String, String> rolePrivilegesMapping = rolesToPrivilegesConfig.getRolesToPrivileges();

        // Search roleName in role-privileges maps
        String rolePrivilegesString = rolePrivilegesMapping.get(roleName);
        if (StringUtils.isNotBlank(rolePrivilegesString))
        {
            List<String> privileges = Arrays.asList(rolePrivilegesString.split(","));

            // Get all privileges with descriptions
            Map<String, String> privilegeDescriptionMapping = privilegesConfig.getPrivilegesDescriptions();

            // Combine privileges and descriptions into one map
            for (String privilegeIter : privileges)
            {
                String privilegeDescription = privilegeDescriptionMapping.getOrDefault(privilegeIter, privilegeIter);
                rolePrivileges.put(privilegeIter, privilegeDescription);
            }
        }
        return rolePrivileges;
    }

    /**
     * Save role's privileges to the file
     *
     * @param roleName
     *            Role name
     * @param privileges
     *            List of privileges
     */
    private void addRolePrivileges(String roleName, List<String> privileges)
            throws ConfigurationPropertyException
    {
        Map<String, String> rolesPrivileges = rolesToPrivilegesConfig.getRolesToPrivileges();
        String privilegesString = rolesPrivileges.get(roleName);
        privilegesString = (StringUtils.isNotBlank(privilegesString) ? privilegesString + "," : "") + String.join(",", privileges);
        rolesPrivileges.put(roleName, privilegesString);
        configurationPropertyService.updateProperties(rolesToPrivilegesConfig);
    }

    private void saveRolesPrivilegesConfig(Map<String, String> rolesPrivileges)
            throws ConfigurationPropertyException
    {
        ApplicationRolesToPrivilegesConfig rolesToPrivilegesConfig = new ApplicationRolesToPrivilegesConfig();
        rolesToPrivilegesConfig.setRolesToPrivileges(rolesPrivileges);
        configurationPropertyService.updateProperties(rolesToPrivilegesConfig);
    }

    private void updateRolesPrivilegesConfig() throws AcmRolesPrivilegesException
    {
        Map<String, String> rolePrivilegesMapping = rolesToPrivilegesConfig.getRolesToPrivileges();
        // Create Map of lists. Roles should be grouped by privileges
        Map<String, List<String>> privileges = new HashMap<>();

        for (Object roleKeyIter : rolePrivilegesMapping.keySet())
        {
            String role = (String) roleKeyIter;
            List<String> rolePrivileges = Arrays.asList(rolePrivilegesMapping.getOrDefault(role, "")
                    .split(","));
            for (String privilegeIter : rolePrivileges)
            {
                if (!privileges.containsKey(privilegeIter))
                {
                    privileges.put(privilegeIter, new ArrayList<>());
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

    private void saveRolesConfig(List<String> roles) throws ConfigurationPropertyException
    {
        ApplicationRolesConfig rolesConfig = new ApplicationRolesConfig();
        rolesConfig.setApplicationRolesString(String.join(",", roles));
        configurationPropertyService.updateProperties(rolesConfig);
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
}
