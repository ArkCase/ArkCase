package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.model.AcmLdapEntity;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by armdev on 7/3/14.
 */
public class LdapSyncServiceTest extends EasyMockSupport
{
    private LdapSyncService unit;

    private SpringLdapDao mockLdapDao;
    private LdapTemplate mockLdapTemplate;

    @Before
    public void setUp()
    {
        mockLdapDao = createMock(SpringLdapDao.class);
        mockLdapTemplate = createMock(LdapTemplate.class);

        unit = new LdapSyncService();
        unit.setLdapDao(mockLdapDao);
    }

    @Test
    public void queryLdapUsers_applicationRoles_differentCases()
    {
        Map<String, String> rolesToGroupMap = new HashMap<>();
        String groupOne = "GroupOne";
        String roleOne = "RoleOne";
        rolesToGroupMap.put(roleOne, groupOne);

        AcmLdapSyncConfig config = new AcmLdapSyncConfig();
        config.setRoleToGroupMap(rolesToGroupMap);

        String directoryName = "directoryName";

        Set<String> roles = new HashSet<>();
        List<AcmUser> users = new ArrayList<>();
        Map<String, List<AcmUser>> usersByApplicationRole = new HashMap<>();
        Map<String, List<AcmUser>> usersByLdapGroup = new HashMap<>();

        String userDnOne = "dn1";
        String userDnTwo = "dn2";
        String[] memberDns = {userDnOne, userDnTwo};
        LdapGroup group = new LdapGroup();
        group.setGroupName(groupOne.toLowerCase());
        group.setMemberDistinguishedNames(memberDns);

        List<LdapGroup> groups = Arrays.asList(group);

        AcmLdapEntity userOne = new AcmUser();
        userOne.setDistinguishedName(userDnOne);

        AcmLdapEntity userTwo = new AcmUser();
        userTwo.setDistinguishedName(userDnTwo);

        List<AcmLdapEntity> entities = Arrays.asList(userOne, userTwo);

        expect(mockLdapDao.buildLdapTemplate(config)).andReturn(mockLdapTemplate);
        expect(mockLdapDao.findGroups(mockLdapTemplate, config)).andReturn(groups);
        expect(mockLdapDao.findGroupMembers(mockLdapTemplate, config, group)).andReturn(entities);

        replayAll();

        unit.queryLdapUsers(config, directoryName, roles, users, usersByApplicationRole, usersByLdapGroup);

        verifyAll();

        assertEquals(1, usersByApplicationRole.size());
        assertEquals(1, usersByLdapGroup.size());

    }

    @Test
    public void queryLdapUsers_nestedGroups()
    {
        Map<String, String> rolesToGroupMap = new HashMap<>();
        String groupName = "GROUP";
        rolesToGroupMap.put("ROLE", groupName);
        AcmLdapSyncConfig config = new AcmLdapSyncConfig();
        config.setRoleToGroupMap(rolesToGroupMap);

        String directoryName = "directoryName";

        Set<String> roles = new HashSet<>();
        List<AcmUser> users = new ArrayList<>();
        Map<String, List<AcmUser>> usersByApplicationRole = new HashMap<>();
        Map<String, List<AcmUser>> usersByLdapGroup = new HashMap<>();

        String userDistinguishedName = "dn1";
        String groupDistinguishedName = "dn2";
        String[] memberDns = {userDistinguishedName, groupDistinguishedName};
        LdapGroup group = new LdapGroup();
        group.setGroupName(groupName);
        group.setMemberDistinguishedNames(memberDns);

        List<LdapGroup> groups = new ArrayList<>();
        groups.add(group);

        AcmLdapEntity user = new AcmUser();
        user.setDistinguishedName(userDistinguishedName);

        AcmLdapEntity role = new AcmRole();
        role.setDistinguishedName(groupDistinguishedName);

        List<AcmLdapEntity> entities = Arrays.asList(user, role);

        expect(mockLdapDao.buildLdapTemplate(config)).andReturn(mockLdapTemplate);
        expect(mockLdapDao.findGroups(mockLdapTemplate, config)).andReturn(groups);
        expect(mockLdapDao.findGroupMembers(mockLdapTemplate, config, group)).andReturn(entities);

        LdapGroup nestedGroup = new LdapGroup();
        expect(mockLdapDao.findGroup(mockLdapTemplate, config, groupDistinguishedName)).andReturn(nestedGroup);

        List<AcmLdapEntity> nestedUsers = new ArrayList<>();
        AcmUser user1 = new AcmUser();
        AcmUser user2 = new AcmUser();
        AcmRole role1 = new AcmRole();
        role1.setDistinguishedName("dnRole1");
        nestedUsers.add(user1);
        nestedUsers.add(user2);
        nestedUsers.add(role1);

        expect(mockLdapDao.findGroupMembers(mockLdapTemplate, config, nestedGroup)).andReturn(nestedUsers);

        expect(mockLdapDao.findGroup(mockLdapTemplate, config, role1.getDistinguishedName())).andReturn(nestedGroup);

        AcmUser user3 = new AcmUser();
        AcmUser user4 = new AcmUser();
        List<AcmLdapEntity> secondLevelNestedUsers = new ArrayList<>();
        secondLevelNestedUsers.add(user3);
        secondLevelNestedUsers.add(user4);

        expect(mockLdapDao.findGroupMembers(mockLdapTemplate, config, nestedGroup)).andReturn(secondLevelNestedUsers);

        replayAll();

        unit.queryLdapUsers(config, directoryName, roles, users, usersByApplicationRole, usersByLdapGroup);

        verifyAll();

        assertEquals(5, users.size());

        assertEquals(1, usersByApplicationRole.size());
        assertEquals(1, usersByLdapGroup.size());


    }
}
