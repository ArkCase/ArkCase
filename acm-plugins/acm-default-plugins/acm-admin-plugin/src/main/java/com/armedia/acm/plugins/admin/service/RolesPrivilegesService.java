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

import static com.armedia.acm.plugins.admin.model.RolePrivilegesConstants.PROP_APPLICATION_ROLES;
import static com.armedia.acm.plugins.admin.model.RolePrivilegesConstants.ROLE_PREFIX;

import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import com.armedia.acm.plugins.admin.model.PrivilegeItem;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmRoleType;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TransactionRequiredException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Created by admin on 6/3/15.
 */
public class RolesPrivilegesService
{

    private Logger log = LoggerFactory.getLogger(getClass());
    private String applicationRolesFile;
    private String applicationPrivilegesFile;
    private String applicationRolesPrivilegesPropertiesFile;
    private String applicationRolesPrivilegesTemplatesLocation;
    private String applicationRolesPrivilegesTemplateFile;
    private String applicationRolesPrivilegesFile;

    private UserDao userDao;

    /**
     * Retrieve list of roles
     *
     * @return list of roles
     * @throws AcmRolesPrivilegesException
     */
    public List<String> retrieveRoles() throws AcmRolesPrivilegesException
    {
        return loadRoles();
    }

    /**
     * Retrieve application's privileges
     *
     * @return map of privileges and descriptions
     * @throws AcmRolesPrivilegesException
     */
    public Map<String, String> retrievePrivileges() throws AcmRolesPrivilegesException
    {
        return loadPrivileges();
    }

    /**
     * Retrieve application's privileges by authorization
     *
     * @return map of privileges and descriptions
     * @throws AcmRolesPrivilegesException
     */
    public Map<String, String> retrievePrivilegesByAuthorization(Boolean authorized, String roleName) throws AcmRolesPrivilegesException
    {
        Map<String, String> privileges;
        if (authorized)
        {
            privileges = loadRolePrivileges(roleName);
        }
        else
        {
            privileges = loadPrivileges();

            for (Map.Entry<String, String> entry : loadRolePrivileges(roleName).entrySet())
            {
                privileges.remove(entry.getKey());
            }
        }
        return privileges;
    }

    /**
     * Retrieve application's privileges for an application role paged
     *
     * @return list of objects (PrivilegeItem)
     * @throws AcmRolesPrivilegesException
     */
    public List<PrivilegeItem> getPrivilegesByRolePaged(String roleName, String sortDirection, Integer startRow, Integer maxRows,
            Boolean authorized)
            throws AcmRolesPrivilegesException
    {
        Map<String, String> privileges = retrievePrivilegesByAuthorization(authorized, roleName);

        return getPrivilegesPaged(privileges, sortDirection, startRow, maxRows, "");
    }

    /**
     * Retrieve application's privileges for an application role by role name
     *
     * @return list of objects (PrivilegeItem)
     * @throws AcmRolesPrivilegesException
     */
    public List<PrivilegeItem> getPrivilegesByRole(String roleName, Boolean authorized, String filterName, String sortDirection,
            Integer startRow, Integer maxRows)
            throws AcmRolesPrivilegesException
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
    public Map<String, String> retrieveRolePrivileges(String roleName) throws AcmRolesPrivilegesException
    {
        return loadRolePrivileges(roleName);
    }

    /**
     * Retrieve roles of privilege
     *
     * @param privilegeName
     * @return
     */
    public List<String> retrieveRolesByPrivilege(String privilegeName) throws AcmRolesPrivilegesException
    {
        try (InputStream propertyStream = FileUtils.openInputStream(new File(applicationRolesPrivilegesPropertiesFile)))
        {
            List<String> roles = new ArrayList<>();
            Properties props = new Properties();
            props.load(propertyStream);

            // Search privilegeName in role-privileges maps
            for (Object roleNameIter : props.keySet())
            {
                String roleName = (String) roleNameIter;
                String propPrivileges = props.getProperty(roleName, "");
                if (!propPrivileges.isEmpty())
                {
                    List<String> privileges = Arrays.asList(propPrivileges.split(","));

                    if (privileges.contains(privilegeName))
                    {
                        roles.add(roleName);
                    }
                }
            }
            return roles;

        }
        catch (Exception e)
        {
            log.error("Can't load privilege's [{}] roles", privilegeName, e);
            throw new AcmRolesPrivilegesException(String.format("Can't load privilege's '%s' roles", privilegeName), e);
        }
    }

    /**
     * Retrieve list of roles filtered by name & paged
     *
     * @param privilegeName
     * @return
     */
    public List<String> getRolesByNamePaged(String privilegeName, String sortBy, String sortDirection, Integer startRow, Integer maxRows,
            Boolean authorized, String filterName) throws AcmRolesPrivilegesException
    {
        List<String> rolesByPrivilege = retrieveRolesByPrivilege(privilegeName);
        List<String> allRoles = loadRoles();
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
        List<String> roles = loadRoles();
        boolean rolePresent = false;
        for (String roleIter : roles)
        {
            if (roleIter.equals(roleName))
            {
                rolePresent = true;
                break;
            }
        }
        if (!rolePresent)
        {
            throw new AcmRolesPrivilegesException(String.format("Can't update role's privileges. Role '%s' is absent", roleName));
        }

        // Save new role privileges
        addRolePrivileges(roleName, privileges);

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
        boolean rolePresent = loadRoles().stream().anyMatch(role -> role.equalsIgnoreCase(roleName));

        if (!rolePresent)
        {
            throw new AcmRolesPrivilegesException(String.format("Can't update role's privileges. Role '%s' is absent",
                    roleName));
        }

        // Save new role privileges
        addRolePrivileges(roleName, privileges);

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
    @Transactional
    public void createRole(String roleName) throws AcmRolesPrivilegesException
    {

        roleName = roleName.toUpperCase().replaceAll("\\s+", "_");
        if (!roleName.startsWith(ROLE_PREFIX))
        {
            roleName = ROLE_PREFIX + roleName;
        }

        List<String> roles = loadRoles();
        // Check if new role presents in roles file
        boolean rolePresent = false;
        if (roles.contains(roleName))
        {
            rolePresent = true;
        }
        if (rolePresent)
        {
            throw new AcmRolesPrivilegesException(String.format("Role '%s' already exists", roleName));
        }
        else
        {
            roles.add(roleName);
            saveRoles(roles);
            AcmRole acmRole = new AcmRole();
            acmRole.setRoleName(roleName);
            acmRole.setRoleType(AcmRoleType.APPLICATION_ROLE);
            try
            {
                userDao.saveAcmRole(acmRole);
                addRoleToPrivileges(roleName);
            }
            catch (IllegalArgumentException | TransactionRequiredException e)
            {
                roles.remove(roleName);
                saveRoles(roles);
                log.error("Can't save info into the roles file", e);
                throw new AcmRolesPrivilegesException("Can't save info into the roles file", e);
            }
        }
    }

    public void updateRole(String roleName, String newRoleName) throws AcmRolesPrivilegesException
    {
        List<String> roles = loadRoles();
        // Check if new role presents in roles file
        int presentRoleIndex = -1;
        for (int i = 0; i < roles.size(); i++)
        {
            if (roles.get(i).equals(roleName))
            {
                presentRoleIndex = i;
                break;
            }
        }
        if (presentRoleIndex == -1)
        {
            throw new AcmRolesPrivilegesException(String.format("Role '%s' doesn't exist", roleName));
        }
        else
        {
            roles.set(presentRoleIndex, newRoleName);
            saveRoles(roles);
            Map<String, String> rolesPrivileges = loadRolesPrivileges();

            // Replace old Role name to the new Role
            String value = rolesPrivileges.get(roleName);
            if (value != null)
            {
                rolesPrivileges.remove(roleName);
                rolesPrivileges.put(newRoleName, value);
                saveRolesPrivileges(rolesPrivileges);
            }
        }
    }

    private void addRoleToPrivileges(String roleName) throws AcmRolesPrivilegesException
    {
        try (InputStream applicationInputStream = FileUtils.openInputStream(new File(applicationRolesPrivilegesPropertiesFile)))
        {
            Properties props = new Properties();
            props.load(applicationInputStream);
            String propPrivileges = props.getProperty(roleName);
            if(Objects.isNull(propPrivileges))
            {
                props.setProperty(roleName, new String());
                try (OutputStream applicationOutputStream = FileUtils.openOutputStream(new File(applicationRolesPrivilegesPropertiesFile)))
                {
                    props.store(applicationOutputStream, String.format("Updated at yyyy-MM-dd hh:mm:ss", new Date()));
                }
            }
        }
        catch (Exception e)
        {
            log.error("Can't save role [{}] to file", roleName, e);
            throw new AcmRolesPrivilegesException(String.format("Can't save role '%s' to file", roleName), e);
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
            Map<String, String> rolesPrivileges = loadRolesPrivileges();
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
            saveRolesPrivileges(rolesPrivileges);
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
            Map<String, String> rolesPrivileges = loadRolesPrivileges();

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
            saveRolesPrivileges(rolesPrivileges);
            updateRolesPrivilegesConfig();
        }
        catch (Exception e)
        {
            log.error("Can't remove privileges from roles", e);
            throw new AcmRolesPrivilegesException("Can't remove privileges from roles", e);
        }
    }

    /**
     * Load roles list form file
     *
     * @return roles list
     * @throws AcmRolesPrivilegesException
     */
    private List<String> loadRoles() throws AcmRolesPrivilegesException
    {
        try (InputStream rolesStream = FileUtils.openInputStream(new File(applicationRolesFile)))
        {
            // Load Application Roles properties file
            Properties props = new Properties();
            props.load(rolesStream);

            String propRoles = props.getProperty(PROP_APPLICATION_ROLES);
            List<String> roles = new ArrayList<>(Arrays.asList(propRoles.split(",")));
            return roles;
        }
        catch (Exception e)
        {
            log.error("Can't load roles file", e);
            throw new AcmRolesPrivilegesException("Can't load roles file", e);
        }
    }

    /**
     * Load all privileges from file
     *
     * @return privileges and descriptions map
     * @throws AcmRolesPrivilegesException
     */
    private Map<String, String> loadPrivileges() throws AcmRolesPrivilegesException
    {
        try (InputStream privilegesStream = FileUtils.openInputStream(new File(applicationPrivilegesFile)))
        {
            // Load Privileges properties file
            Properties props = new Properties();
            props.load(privilegesStream);

            Set<String> privilegesKeys = props.stringPropertyNames();
            Map<String, String> priveleges = new HashMap<>();
            for (String keyIter : privilegesKeys)
            {
                priveleges.put(keyIter, props.getProperty(keyIter));
            }

            return priveleges;
        }
        catch (Exception e)
        {
            log.error("Can't load privileges file", e);
            throw new AcmRolesPrivilegesException("Can't load privileges file", e);
        }
    }

    /**
     * Save list of roles
     *
     * @param roles
     *            saved roles list
     * @throws AcmRolesPrivilegesException
     */
    private void saveRoles(List<String> roles) throws AcmRolesPrivilegesException
    {
        try (OutputStream rolesStream = FileUtils.openOutputStream(new File(applicationRolesFile)))
        {
            Properties props = new Properties();
            String propRoles = String.join(",", roles);
            props.setProperty(PROP_APPLICATION_ROLES, propRoles);

            props.store(rolesStream, String.format("Updated at yyyy-MM-dd hh:mm:ss", new Date()));
        }
        catch (Exception e)
        {
            log.error("Can't save info into the roles file", e);
            throw new AcmRolesPrivilegesException("Can't save info into the roles file", e);
        }
    }

    private Map<String, String> loadRolesPrivileges() throws AcmRolesPrivilegesException
    {
        try (InputStream applicationStream = FileUtils.openInputStream(new File(applicationRolesPrivilegesPropertiesFile)))
        {
            Map<String, String> result = new HashMap<>();
            Properties props = new Properties();
            props.load(applicationStream);
            for (Object keyIter : props.keySet())
            {
                String key = (String) keyIter;
                result.put(key, props.getProperty(key));
            }
            return result;

        }
        catch (Exception e)
        {
            log.error("Can't load roles privileges file", e);
            throw new AcmRolesPrivilegesException("Can't load roles privileges file", e);
        }
    }

    /**
     * Load specific role's privileges
     *
     * @param roleName
     *            Role name
     * @return map of privileges and descriptions
     * @throws AcmRolesPrivilegesException
     */
    private Map<String, String> loadRolePrivileges(String roleName) throws AcmRolesPrivilegesException
    {
        try (
                InputStream applicationStream = FileUtils.openInputStream(new File(applicationRolesPrivilegesPropertiesFile));
                InputStream privilegesStream = FileUtils.openInputStream(new File(applicationPrivilegesFile)))
        {
            Map<String, String> rolePrivileges = new HashMap<>();

            Properties props = new Properties();
            props.load(applicationStream);

            // Search roleName in role-privileges maps
            String propPrivileges = props.getProperty(roleName, "");
            if (!propPrivileges.isEmpty())
            {
                List<String> privileges = Arrays.asList(propPrivileges.split(","));

                // Get all privileges with descriptions
                Properties allPrivilegesProps = new Properties();
                allPrivilegesProps.load(privilegesStream);

                // Combine privileges and descriptions into one map
                for (String privilegeIter : privileges)
                {
                    rolePrivileges.put(privilegeIter, allPrivilegesProps.getProperty(privilegeIter, privilegeIter));
                }
            }
            return rolePrivileges;

        }
        catch (Exception e)
        {
            log.error("Can't load role [{}] privileges from file", roleName, e);
            throw new AcmRolesPrivilegesException(String.format("Can't load role '%s' privileges from file", roleName), e);
        }
    }

    /**
     * Save role's privileges to the file
     *
     * @param roleName
     *            Role name
     * @param privileges
     *            List of privileges
     */
    private void addRolePrivileges(String roleName, List<String> privileges) throws AcmRolesPrivilegesException
    {
        // opening an output stream and input stream for the same file at the same time means the output stream
        // will overwrite the file contents before the input stream can read it... so here, we split into two
        // try blocks.
        try (InputStream applicationInputStream = FileUtils.openInputStream(new File(applicationRolesPrivilegesPropertiesFile)))
        {
            Properties props = new Properties();
            props.load(applicationInputStream);
            String propPrivileges = props.getProperty(roleName);
            propPrivileges += (propPrivileges.isEmpty() ? "" : ",") + String.join(",", privileges);
            Set<String> updatedRole = new TreeSet<>(Arrays.asList(propPrivileges.split(",")));
            propPrivileges = String.join(",", new ArrayList<>(updatedRole));
            props.setProperty(roleName, propPrivileges);

            try (OutputStream applicationOutputStream = FileUtils.openOutputStream(new File(applicationRolesPrivilegesPropertiesFile)))
            {
                props.store(applicationOutputStream, String.format("Updated at yyyy-MM-dd hh:mm:ss", new Date()));
            }

        }
        catch (Exception e)
        {
            log.error("Can't save role [{}] privileges to file", roleName, e);
            throw new AcmRolesPrivilegesException(String.format("Can't save role '%s' privileges to file", roleName), e);
        }
    }

    private void saveRolesPrivileges(Map<String, String> rolesPrivileges) throws AcmRolesPrivilegesException
    {
        try (OutputStream applicationStream = FileUtils.openOutputStream(new File(applicationRolesPrivilegesPropertiesFile)))
        {
            Properties props = new Properties();
            props.putAll(rolesPrivileges);
            props.store(applicationStream, String.format("Updated at yyyy-MM-dd hh:mm:ss", new Date()));

        }
        catch (Exception e)
        {
            log.error("Can't save roles privileges to file", e);
            throw new AcmRolesPrivilegesException("Can't save roles privileges to file", e);
        }
    }

    private void updateRolesPrivilegesConfig() throws AcmRolesPrivilegesException
    {
        try (InputStream applicationInputStream = FileUtils.openInputStream(new File(applicationRolesPrivilegesPropertiesFile)))
        {
            // Load roles privileges properties
            Properties props = new Properties();
            props.load(applicationInputStream);

            // Create Map of lists. Roles should be grouped by privileges
            Map<String, List<String>> privileges = new HashMap<>();
            for (Object roleKeyIter : props.keySet())
            {
                String role = (String) roleKeyIter;
                List<String> rolePrivileges = Arrays.asList(props.getProperty(role, "").split(","));
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

    public void setApplicationRolesFile(String applicationRolesFile)
    {
        this.applicationRolesFile = applicationRolesFile;
    }

    public void setApplicationPrivilegesFile(String applicationPrivilegesFile)
    {
        this.applicationPrivilegesFile = applicationPrivilegesFile;
    }

    public void setApplicationRolesPrivilegesPropertiesFile(String applicationRolesPrivilegesPropertiesFile)
    {
        this.applicationRolesPrivilegesPropertiesFile = applicationRolesPrivilegesPropertiesFile;
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
}
