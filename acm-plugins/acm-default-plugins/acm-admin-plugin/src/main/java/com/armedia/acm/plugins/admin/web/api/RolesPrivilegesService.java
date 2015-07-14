package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

/**
 * Created by admin on 6/3/15.
 */
public class RolesPrivilegesService implements RolePrivilegesConstants{
    private Logger log = LoggerFactory.getLogger(getClass());
    private String applicationRolesFile;
    private String applicationPrivilegesFile;
    private String applicationRolesPrivilegesPropertiesFile;
    private String applicationRolesPrivilegesTemplatesLocation;
    private String applicationRolesPrivilegesTemplateFile;
    private String applicationRolesPrivilegesFile;

    /**
     * Retrieve list of roles
     * @return list of roles
     * @throws AcmRolesPrivilegesException
     */
    public List<String> retrieveRoles() throws AcmRolesPrivilegesException {
        return loadRoles();
    }

    /**
     * Retrieve application's privileges
     * @return map of privileges and descriptions
     * @throws AcmRolesPrivilegesException
     */
    public Map<String, String> retrievePrivileges() throws AcmRolesPrivilegesException {
        return loadPrivileges();
    }

    /**
     * Retrieves role's privileges
     * @return map of privileges and descriptions
     */
    public Map<String, String> retrieveRolePrivileges(String roleName) throws AcmRolesPrivilegesException {
        return loadRolePrivileges(roleName);
    }

    /**
     * Retrieve roles of privilege
     * @param privilegeName
     * @return
     */
    public List<String> retrieveRolesByPrivilege(String privilegeName) throws AcmRolesPrivilegesException {
        try {
            List<String> roles = new ArrayList();
            Properties props = new Properties();
            props.load(FileUtils.openInputStream(new File(applicationRolesPrivilegesPropertiesFile)));

            // Search privilegeName in role-privileges maps
            for (Object roleNameIter: props.keySet()) {
                String roleName = (String) roleNameIter;
                String propPrivileges = props.getProperty(roleName, "");
                if (!propPrivileges.isEmpty()) {
                    List<String> privileges = Arrays.asList(propPrivileges.split(","));

                    if (privileges.contains(privilegeName)) {
                        roles.add(roleName);
                    }
                }
            }
            return roles;

        } catch (Exception e){
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't load privilege's '%s' roles", privilegeName), e);
            }
            throw new AcmRolesPrivilegesException(String.format("Can't load privilege's '%s' roles", privilegeName), e);
        }
    }

    /**
     * Update Role Privileges
     * @param roleName Updated role name
     * @param privileges List of role's privileges
     */
    public void updateRolePrivileges(String roleName, List<String> privileges) throws AcmRolesPrivilegesException {
        // Check if role present in system
        List<String> roles = loadRoles();
        boolean rolePresent = false;
        for (String roleIter: roles) {
            if (roleIter.equals(roleName)) {
                rolePresent = true;
                break;
            }
        }
        if (!rolePresent) {
            throw  new AcmRolesPrivilegesException(String.format("Can't update role's privileges. Role '%s' is absent", roleName));
        }

        // Save new role privileges
        saveRolePrivileges(roleName, privileges);

        // Re-generate Roles Privileges XML file
        updateRolesPrivilegesConfig();
    }

    /**
     * Create new role
     * @param roleName new role name
     * @throws AcmRolesPrivilegesException
     */
    public void createRole(String roleName) throws AcmRolesPrivilegesException {
        List<String> roles = loadRoles();
        // Check if new role presents in roles file
        boolean rolePresent = false;
        for (String roleIter : roles) {
            if (roleIter.equals(roleName)) {
                rolePresent = true;
                break;
            }
        }
        if (rolePresent) {
            throw new AcmRolesPrivilegesException(String.format("Role '%s' already exists", roleName));
        } else {
            roles.add(roleName);
            saveRoles(roles);
        }
    }

    public void updateRole(String roleName, String newRoleName) throws AcmRolesPrivilegesException {
        List<String> roles = loadRoles();
        // Check if new role presents in roles file
        int presentRoleIndex = -1;
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).equals(roleName)) {
                presentRoleIndex = i;
                break;
            }
        }
        if (presentRoleIndex == -1) {
            throw new AcmRolesPrivilegesException(String.format("Role '%s' doesn't exist", roleName));
        } else {
            roles.set(presentRoleIndex, newRoleName);
            saveRoles(roles);
            Map<String, String> rolesPrivileges = loadRolesPrivileges();

            // Replace old Role name to the new Role
            String value = rolesPrivileges.get(roleName);
            if (value != null) {
                rolesPrivileges.remove(roleName);
                rolesPrivileges.put(newRoleName, value);
                saveRolesPrivileges(rolesPrivileges);
            }
        }
    }

    /**
     * Add privileges to list of roles
     * @param roles
     * @param newPrivileges
     */
    public void addRolesPrivileges(List<String> roles, List<String> newPrivileges) throws AcmRolesPrivilegesException {
        try {
            Map<String, String> rolesPrivileges = loadRolesPrivileges();
            for (String role : roles) {
                // Search role name in role-privileges maps
                String propPrivileges = rolesPrivileges.get(role);
                List<String> privileges = new LinkedList();
                if (propPrivileges != null && !propPrivileges.isEmpty()) {
                    privileges.addAll(Arrays.asList(propPrivileges.split(",")));
                }

                for (String newPrivilege: newPrivileges) {
                    if (!privileges.contains(newPrivilege)) {
                        privileges.add(newPrivilege);
                    }
                }
                rolesPrivileges.put(role, String.join(",", privileges));
            }
            saveRolesPrivileges(rolesPrivileges);
            updateRolesPrivilegesConfig();
        } catch (Exception e){
            if (log.isErrorEnabled()) {
                log.error("Can't add roles to privileges", e);
            }
            throw new AcmRolesPrivilegesException("Can't add roles to privileges", e);
        }
    }


    /**
     * Remove privileges form list of roles
     * @param roles
     * @param removedPrivileges
     */
    public void removeRolesPrivileges(List<String> roles, List<String> removedPrivileges) throws AcmRolesPrivilegesException {
        try {
            Map<String, String> rolesPrivileges = loadRolesPrivileges();

            for (String role : roles) {
                // Search role name in role-privileges maps
                String propPrivileges = rolesPrivileges.get(role);
                List<String> privileges =  new LinkedList();
                if (propPrivileges != null && !propPrivileges.isEmpty()) {
                    privileges.addAll(Arrays.asList(propPrivileges.split(",")));
                }

                for (String removedPrivilege: removedPrivileges) {
                    int foundIndex = privileges.indexOf(removedPrivilege);
                    if (foundIndex != -1) {
                        privileges.remove(foundIndex);
                    }
                }
                rolesPrivileges.put(role, String.join(",", privileges));
            }
            saveRolesPrivileges(rolesPrivileges);
            updateRolesPrivilegesConfig();
        } catch (Exception e){
            if (log.isErrorEnabled()) {
                log.error("Can't remove privileges from roles", e);
            }
            throw new AcmRolesPrivilegesException("Can't remove privileges from roles", e);
        }
    }

    /**
     * Load roles list form file
     * @return roles list
     * @throws AcmRolesPrivilegesException
     */
    private List<String> loadRoles() throws AcmRolesPrivilegesException {
        try {
            // Load Application Roles properties file
            Properties props = new Properties();
            props.load(FileUtils.openInputStream(new File(applicationRolesFile)));

            String propRoles = props.getProperty(PROP_APPLICATION_ROLES);
            List<String> roles = new LinkedList(Arrays.asList(propRoles.split(",")));
            return roles;
        } catch (Exception e){
            if (log.isErrorEnabled()) {
                log.error("Can't load roles file", e);
            }
            throw new AcmRolesPrivilegesException("Can't load roles file", e);
        }
    }

    /**
     * Load all privileges from file
     * @return privileges and descriptions map
     * @throws AcmRolesPrivilegesException
     */
    private Map<String, String> loadPrivileges() throws AcmRolesPrivilegesException {
        try {
            // Load Privileges properties file
            Properties props = new Properties();
            props.load(FileUtils.openInputStream(new File(applicationPrivilegesFile)));

            Set<Object> privilegesKeys = props.keySet();
            Map<String, String> priveleges = new HashMap<String, String>();
            for (Object keyIter: privilegesKeys) {
                Map<String, String> item = new HashMap<String, String>();
                priveleges.put((String)keyIter, (String)props.get(keyIter));
            }

            return priveleges;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't load privileges file", e);
            }
            throw new AcmRolesPrivilegesException("Can't load privileges file", e);
        }
    }

    /**
     * Save list of roles
     * @param roles saved roles list
     * @throws AcmRolesPrivilegesException
     */
    private void saveRoles(List<String> roles) throws AcmRolesPrivilegesException {
        FileOutputStream fos = null;
        try {
            Properties props = new Properties();
            String propRoles = String.join(",", roles);
            props.setProperty(PROP_APPLICATION_ROLES, propRoles);
            fos =  FileUtils.openOutputStream(new File(applicationRolesFile));
            try {
                props.store(fos, String.format("Updated at yyyy-MM-dd hh:mm:ss", new Date()));
            } finally {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't save info into the roles file", e);
            }
            throw new AcmRolesPrivilegesException("Can't save info into the roles file", e);
        }
    }

    private Map<String, String> loadRolesPrivileges() throws AcmRolesPrivilegesException {
        try {
            Map<String, String> result = new HashMap();
            Properties props = new Properties();
            props.load(FileUtils.openInputStream(new File(applicationRolesPrivilegesPropertiesFile)));
            for (Object keyIter: props.keySet()) {
                String key = (String) keyIter;
                result.put(key, props.getProperty(key));
            }
            return result;

        } catch(Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't load roles privileges file", e);
            }
            throw new AcmRolesPrivilegesException("Can't load roles privileges file", e);
        }
    }

    /**
     * Load specific role's privileges
     * @param roleName Role name
     * @return map of privileges and descriptions
     * @throws AcmRolesPrivilegesException
     */
    private Map<String, String>loadRolePrivileges(String roleName) throws AcmRolesPrivilegesException {
        try {
            Map<String, String> rolePrivileges = new HashMap<String, String>();

            Properties props = new Properties();
            props.load(FileUtils.openInputStream(new File(applicationRolesPrivilegesPropertiesFile)));

            // Search roleName in role-privileges maps
            String propPrivileges = props.getProperty(roleName, "");
            if (!propPrivileges.isEmpty()) {
                List<String> privileges = Arrays.asList(propPrivileges.split(","));

                //Get all privileges with descriptions
                Properties allPrivilegesProps = new Properties();
                allPrivilegesProps.load(FileUtils.openInputStream(new File(applicationPrivilegesFile)));

                // Combine privileges and descriptions into one map
                for (String privilegeIter : privileges) {
                    rolePrivileges.put(privilegeIter, allPrivilegesProps.getProperty(privilegeIter, privilegeIter));
                }
            }
            return rolePrivileges;

        } catch (Exception e){
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't load role '%s' privileges from file", roleName), e);
            }
            throw new AcmRolesPrivilegesException(String.format("Can't load role '%s' privileges from file", roleName), e);
        }
    }

    /**
     * Save role's privileges to the file
     * @param roleName Role name
     * @param privileges List of privileges
     */
    private void saveRolePrivileges(String roleName, List<String> privileges) throws AcmRolesPrivilegesException {
        try {
            Properties props = new Properties();
            props.load(FileUtils.openInputStream(new File(applicationRolesPrivilegesPropertiesFile)));
            String propPrivileges = String.join(",", privileges);
            props.setProperty(roleName, propPrivileges);

            FileOutputStream fos =  FileUtils.openOutputStream(new File(applicationRolesPrivilegesPropertiesFile));
            try {
                props.store(fos, String.format("Updated at yyyy-MM-dd hh:mm:ss", new Date()));
            } finally {
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't save role '%s' privileges to file", roleName), e);
            }
            throw new AcmRolesPrivilegesException(String.format("Can't save role '%s' privileges to file", roleName), e);
        }
    }

    private void saveRolesPrivileges(Map<String, String> rolesPrivileges) throws AcmRolesPrivilegesException {
        FileOutputStream fos = null;
        try {
            fos =  FileUtils.openOutputStream(new File(applicationRolesPrivilegesPropertiesFile));
            Properties props = new Properties();
            props.putAll(rolesPrivileges);
            try {
                props.store(fos,String.format("Updated at yyyy-MM-dd hh:mm:ss", new Date()));
            } finally {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't save roles privileges to file", e);
            }
            throw new AcmRolesPrivilegesException("Can't save roles privileges to file", e);
        }
    }


    private void updateRolesPrivilegesConfig() throws AcmRolesPrivilegesException {
        try {
            // Load roles privileges properties
            Properties props = new Properties();
            props.load(FileUtils.openInputStream(new File(applicationRolesPrivilegesPropertiesFile)));

            // Create Map of lists. Roles should be grouped by privileges
            Map<String, List<String>> privileges = new HashMap<String, List<String>>();
            for (Object roleKeyIter : props.keySet()) {
                String role = (String)roleKeyIter;
                List<String> rolePrivileges = Arrays.asList(props.getProperty(role, "").split(","));
                for (String privilegeIter : rolePrivileges) {
                    if (!privileges.containsKey(privilegeIter)) {
                        privileges.put(privilegeIter, new ArrayList<String>());
                    }
                    // Add Role to map grouped by privilege
                    privileges.get(privilegeIter).add(role);
                }
            }

            // Load template and render configuration file
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setDirectoryForTemplateLoading(new File(applicationRolesPrivilegesTemplatesLocation));
            Template tmpl = cfg.getTemplate(applicationRolesPrivilegesTemplateFile);
            Writer writer = null;
            try {
                writer = new FileWriter(new File(applicationRolesPrivilegesFile));
                tmpl.process(privileges, writer);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't update roles privileges config file", e);
            }
            throw new AcmRolesPrivilegesException("Can't update roles privileges config file", e);
        }
    }

    public void setApplicationRolesFile(String applicationRolesFile) {
        this.applicationRolesFile = applicationRolesFile;
    }

    public void setApplicationPrivilegesFile(String applicationPrivilegesFile) {
        this.applicationPrivilegesFile = applicationPrivilegesFile;
    }

    public void setApplicationRolesPrivilegesPropertiesFile(String applicationRolesPrivilegesPropertiesFile) {
        this.applicationRolesPrivilegesPropertiesFile = applicationRolesPrivilegesPropertiesFile;
    }

    public void setApplicationRolesPrivilegesTemplatesLocation(String applicationRolesPrivilegesTemplatesLocation) {
        this.applicationRolesPrivilegesTemplatesLocation = applicationRolesPrivilegesTemplatesLocation;
    }

    public void setApplicationRolesPrivilegesTemplateFile(String applicationRolesPrivilegesTemplateFile) {
        this.applicationRolesPrivilegesTemplateFile = applicationRolesPrivilegesTemplateFile;
    }

    public void setApplicationRolesPrivilegesFile(String applicationRolesPrivilegesFile) {
        this.applicationRolesPrivilegesFile = applicationRolesPrivilegesFile;
    }
}
