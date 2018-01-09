package com.armedia.acm.services.users.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import org.springframework.transaction.annotation.Transactional;

public class AcmUserRoleService
{
    private UserDao userDao;
    private AcmRoleToGroupMapping roleToGroupConfig;
    private Logger log = LoggerFactory.getLogger(getClass());

    public Set<String> getUserRoles(String userId)
    {
        Map<String, List<String>> groupToRoleMap = roleToGroupConfig.getGroupToRolesMap();

        AcmUser user = userDao.findByUserId(userId);
        Set<AcmGroup> userGroups = user.getGroups();

        Set<String> userGroupNames = Stream
                .concat(userGroups.stream().map(AcmGroup::getName), userGroups.stream().flatMap(AcmGroup::getAscendantsStream))
                .collect(Collectors.toSet());

        return userGroupNames.stream().filter(groupToRoleMap::containsKey).flatMap(g -> groupToRoleMap.get(g).stream())
                .collect(Collectors.toSet());
    }

    @Transactional
    public void saveRolesPerAddedGroups(Set<AcmGroup> groups)
    {
        // added groups are also valid AcmRoles
        groups.forEach(group -> {
            AcmRole role = new AcmRole();
            role.setRoleType(AcmRoleType.valueOf(group.getType().name()));
            role.setRoleName(group.getName());
            userDao.saveAcmRole(role);
        });

        userDao.getEntityManager().flush();
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setRoleToGroupConfig(AcmRoleToGroupMapping roleToGroupConfig)
    {
        this.roleToGroupConfig = roleToGroupConfig;
    }
}
