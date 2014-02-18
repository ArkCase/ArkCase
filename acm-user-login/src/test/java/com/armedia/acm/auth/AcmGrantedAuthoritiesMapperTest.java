package com.armedia.acm.auth;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 *
 * @author millerd
 */
public class AcmGrantedAuthoritiesMapperTest
{
    private AcmGrantedAuthoritiesMapper unit;
    
    @Before
    public void setUp()
    {
        unit = new AcmGrantedAuthoritiesMapper();
        
    }
    
    /**
     * initBean() should set the active mapping to an empty map when the 
     * mapping properties are not set.
     */
    @Test
    public void initBean_propertiesAreNotSet() 
    {
        unit.initBean();
        
        assertEquals(0, unit.getActiveMapping().size());   
    }
    
    /**
     * initBean() should set the active mappings to have one key for each 
     * value from the properties, with the value for that key being the list 
     * of keys that have that value.  The properties map logical role names 
     * to LDAP groups (to ensure each role name is accounted for).  But we 
     * need to map group names to roles.  So we want to reverse the properties 
     * - make one key for each property value.  But a group might map to more 
     * than one role.  Hence the need to have a list of roles for each group.
     * 
     * Also the group and role names should be normalized to upper case.
     * 
     * Also group and role names should be trimmed.
     * 
     * Also null or empty group or role names - that property should be ignored.
     * 
     * Also the roles should have "ROLE_" in front.
     * 
     * E.g., if the properties have:
     *   - ROLE1=group1
     *   - Role2= group2 
     *   -  ROLE_ROLE3 =group1
     * Then the active mapping should be:
     *   - GROUP1=ROLE_ROLE1,ROLE_ROLE3
     *   - GROUP2=ROLE_ROLE2
     */
    @Test
    public void initBean()
    {
        Properties rolesToGroups = new Properties();
        rolesToGroups.setProperty("ROLE1", "group1");
        rolesToGroups.setProperty("Role2", " group2 ");
        rolesToGroups.setProperty(" ROLE_ROLE3 ", "group1");
        rolesToGroups.setProperty("", "group5");
        rolesToGroups.setProperty("ROLE4", " ");
       
        unit.setApplicationRoleToUserGroupProperties(rolesToGroups);
        
        unit.initBean();
        
        assertEquals(2, unit.getActiveMapping().size());
        assertTrue(unit.getActiveMapping().containsKey("GROUP1"));
        assertTrue(unit.getActiveMapping().containsKey("GROUP2"));
        
        List<String> foundGroup1Roles = unit.getActiveMapping().get("GROUP1");
        assertEquals(2, foundGroup1Roles.size());
        assertTrue(foundGroup1Roles.contains("ROLE_ROLE1"));
        assertTrue(foundGroup1Roles.contains("ROLE_ROLE3"));
        
        List<String> foundGroup2Roles = unit.getActiveMapping().get("GROUP2");
        assertEquals(1, foundGroup2Roles.size());
        assertTrue(foundGroup2Roles.contains("ROLE_ROLE2"));
        
    }
    
    /**
     * mapAuthorities should include each role associated to each group 
     * the user has.
     */
    @Test
    public void mapAuthorities()
    {
        Map<String, List<String>> groupsToRoles = new HashMap<String, List<String>>();
        List<String> roles1 = new ArrayList<String>();
        roles1.add("ROLE_INVESTIGATOR");
        roles1.add("ROLE_INVESTIGATOR_SUPERVISOR");
        
        groupsToRoles.put("GROUP1", roles1);
        
        List<String> roles2 = new ArrayList<>();
        roles2.add("ROLE_ANALYST");
        groupsToRoles.put("GROUP2", roles2);
        
        unit.setActiveMapping(groupsToRoles);
        
        GrantedAuthority auth1 = new AcmGrantedAuthority("GROUP1");
        
        GrantedAuthority auth2 = new AcmGrantedAuthority("GROUP2");
        
        GrantedAuthority authNotMapped = new AcmGrantedAuthority("NONE");
        
        List<GrantedAuthority> auths = Arrays.asList(auth1, auth2, authNotMapped);
        
        Collection<? extends GrantedAuthority> found = unit.mapAuthorities(auths);
        
        assertEquals(3, found.size());
        assertTrue(found.contains(new AcmGrantedAuthority("ROLE_INVESTIGATOR")));
        assertTrue(found.contains(new AcmGrantedAuthority("ROLE_INVESTIGATOR_SUPERVISOR")));
        assertTrue(found.contains(new AcmGrantedAuthority("ROLE_ANALYST")));
        
        
    }

    /**
     * applicationGroupsFromLdapGroups should return the list of logical application
     * group names corresponding to the groups the user actually belongs to in LDAP.
     */
    @Test
    public void applicationGroupsFromLdapGroups()
    {
        Map<String, List<String>> groupsToRoles = new HashMap<>();
        List<String> roles1 = new ArrayList<>();
        roles1.add("ROLE_INVESTIGATOR");
        roles1.add("ROLE_INVESTIGATOR_SUPERVISOR");

        groupsToRoles.put("GROUP1", roles1);

        List<String> roles2 = new ArrayList<>();
        roles2.add("ROLE_ANALYST");
        groupsToRoles.put("GROUP2", roles2);

        unit.setActiveMapping(groupsToRoles);

        List<String> ldapGroups = new ArrayList<>();
        ldapGroups.add("GROUP1");
        ldapGroups.add("GROUP2");

        List<String> appGroups = unit.applicationGroupsFromLdapGroups(ldapGroups);

        assertEquals(3, appGroups.size());
        assertTrue(appGroups.contains("investigator"));
        assertTrue(appGroups.contains("investigator_supervisor"));
        assertTrue(appGroups.contains("analyst"));


    }
}
