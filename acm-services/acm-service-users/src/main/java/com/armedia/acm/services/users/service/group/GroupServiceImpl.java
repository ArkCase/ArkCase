package com.armedia.acm.services.users.service.group;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectAlreadyExistsException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupServiceImpl implements GroupService
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private UserDao userDao;
    private AcmGroupDao groupDao;
    private ExecuteSolrQuery executeSolrQuery;

    @Override
    public AcmGroup findByName(String name)
    {
        return groupDao.findByName(name);
    }

    @Override
    public AcmGroup save(AcmGroup group)
    {
        return groupDao.save(group);
    }

    @Override
    public AcmGroup createGroup(AcmGroup group) throws AcmObjectAlreadyExistsException
    {
        String groupName = group.getName();
        AcmGroup acmGroup = groupDao.findByName(groupName);
        if (acmGroup != null)
        {
            throw new AcmObjectAlreadyExistsException("Group " + groupName + " already exists.");
        }
        group.setName(MapperUtils.buildGroupName(groupName, Optional.empty()));
        return groupDao.save(group);
    }

    @Override
    @Transactional
    public AcmGroup saveAndFlush(AcmGroup group)
    {
        AcmGroup managed = groupDao.save(group);
        groupDao.getEm().flush();
        return managed;
    }

    @Override
    public String getLdapGroupsForUser(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws MuleException
    {

        log.info("Taking all groups and ascendant groups from Solr. Authenticated user is [{}]",
                usernamePasswordAuthenticationToken.getName());

        String query = "object_type_s:GROUP AND object_sub_type_s:LDAP_GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE "
                + "AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        return executeSolrQuery.getResultsByPredefinedQuery(usernamePasswordAuthenticationToken, SolrCore.ADVANCED_SEARCH, query, 0, 1000,
                "name asc");
    }

    @Override
    public String getUserMembersForGroup(String groupName, Optional<String> userStatus, Authentication auth) throws MuleException
    {
        String statusQuery = userStatus.map(it -> {
            try
            {
                AcmUserState state = AcmUserState.valueOf(it);
                return String.format(" AND status_lcs:%s", state);
            }
            catch (IllegalArgumentException e)
            {
                log.debug("usersStatus: [{}] is not a valid value. Won't be included in the query!", userStatus);
                return "";
            }
        }).orElse("");

        String query = String.format("object_type_s:USER AND groups_id_ss:%s", buildSafeGroupNameForSolrSearch(groupName));
        query = query.replace("_002E_", ".");
        query += statusQuery;

        log.debug("Executing query for users in group: [{}]", query);
        return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, 0, 1000, "");
    }

    private String buildSafeGroupNameForSolrSearch(String groupName)
    {
        if (groupName.contains(" "))
        {
            groupName = "\"" + groupName + "\"";
        }
        groupName = groupName.replace("&", "%26"); // instead of URL encoding
        groupName = groupName.replace("?", "%3F"); // instead of URL encoding
        return groupName;
    }

    @Override
    @Transactional
    public List<AcmGroup> findByUserMember(AcmUser user)
    {
        return groupDao.findByUserMember(user);
    }

    @Override
    @Transactional
    public AcmGroup markGroupDeleted(String groupName) throws AcmObjectNotFoundException
    {
        return markGroupDeleted(groupName, false);
    }

    @Override
    @Transactional
    public AcmGroup markGroupDeleted(String groupName, boolean flushInstructions) throws AcmObjectNotFoundException
    {
        AcmGroup acmGroup = findByName(groupName);
        if (acmGroup == null)
        {
            throw new AcmObjectNotFoundException("GROUP", null, "Group with name " + groupName + " not found");
        }

        acmGroup.removeAsMemberOf();
        Assert.isTrue(acmGroup.isNotMemeberOfGroups());

        // acmGroup.getUserMembers().stream().collect(Collectors.toMap(Function.identity(), acmUser ->
        // Collections.singleton(acmGroup)));
        Set<AcmGroup> descendantGroups = AcmGroupUtils.findDescendantsForAcmGroup(acmGroup);
        Set<AcmUser> users = descendantGroups.stream().flatMap(group -> group.getUserMembers().stream()).collect(Collectors.toSet());
        users.addAll(acmGroup.getUserMembers());

        acmGroup.setAscendantsList(null);
        acmGroup.setStatus(AcmGroupStatus.DELETE);

        acmGroup.removeMembers();

        acmGroup.setUserMembers(new HashSet<>());

        save(acmGroup);

        descendantGroups.forEach(group -> {
            String ancestorsStringList = AcmGroupUtils.buildAncestorsStringForAcmGroup(group);
            group.setAscendantsList(ancestorsStringList);
            save(group);
        });

        if (flushInstructions)
        {
            groupDao.getEm().flush();
        }

        return acmGroup;
    }

    @Override
    @Transactional
    public AcmGroup removeGroupMembership(String groupName, String parentGroupName) throws AcmObjectNotFoundException
    {
        return removeGroupMembership(groupName, parentGroupName, false);
    }

    @Override
    @Transactional
    public AcmGroup removeGroupMembership(String groupName, String parentGroupName, boolean flushInstructions)
            throws AcmObjectNotFoundException
    {
        AcmGroup acmGroup = findByName(groupName);
        AcmGroup parentGroup = findByName(parentGroupName);

        if (acmGroup == null || parentGroup == null)
        {
            String groupNotFound = acmGroup == null ? groupName : parentGroupName;
            log.warn("Group [{}] not found", groupNotFound);
            throw new AcmObjectNotFoundException("GROUP", null, "Group with name " + groupNotFound + " not found");
        }

        log.debug("Remove group member [{}] from group [{}]", groupName, parentGroupName);
        parentGroup.removeGroupMember(acmGroup);

        if (acmGroup.isNotMemeberOfGroups())
        {
            log.debug("Group [{}] has no other parent groups, will be deleted", groupName);
            return markGroupDeleted(groupName);
        } else
        {
            log.debug("Build ancestors string for group: [{}]", groupName);
            acmGroup.setAscendantsList(AcmGroupUtils.buildAncestorsStringForAcmGroup(acmGroup));
            save(acmGroup);

            Set<AcmGroup> descendantGroups = AcmGroupUtils.findDescendantsForAcmGroup(acmGroup);
            Set<AcmUser> users = descendantGroups.stream().flatMap(group -> group.getUserMembers().stream()).collect(Collectors.toSet());
            users.addAll(acmGroup.getUserMembers());

            log.debug("Remove roles for user members from the parent group [{}]", parentGroupName);

            descendantGroups.forEach(group -> {
                log.debug("Build ancestors string for descendants group: [{}]", groupName);
                String ancestorsStringList = AcmGroupUtils.buildAncestorsStringForAcmGroup(group);
                group.setAscendantsList(ancestorsStringList);
                save(group);
            });

            if (flushInstructions)
            {
                groupDao.getEm().flush();
            }
            return acmGroup;
        }
    }

    @Override
    @Transactional
    public AcmGroup setSupervisor(AcmUser supervisor, String groupId, boolean applyToAll) throws AcmUserActionFailedException
    {
        AcmGroup group = groupDao.findByName(groupId);

        if (group == null)
        {
            log.error("Failed to set supervisor to group. Group [{}] was not found.", groupId);
            throw new AcmUserActionFailedException("Set supervisor", "Group", -1L,
                    "Failed to set supervisor to group. Group " + groupId + " was not found.", null);
        }

        supervisor = userDao.findByUserId(supervisor.getUserId());

        group.setSupervisor(supervisor);

        if (applyToAll)
        {
            // TODO: Apply supervisors to all objects assigned to this group
        }
        return group;
    }

    @Override
    @Transactional
    public AcmGroup addUserMembersToGroup(List<String> members, String groupId) throws AcmObjectNotFoundException
    {
        AcmGroup group = null;
        for (String userId : members)
        {
            AcmUser user = userDao.findByUserId(userId);
            if (user != null)
            {
                group = addUserMemberToGroup(user, groupId);
            } else
            {
                log.warn("User with id [{}] not found", userId);
            }
        }
        return group;
    }

    @Override
    public AcmGroup addUserMemberToGroup(AcmUser user, String groupId, boolean flushInstructions) throws AcmObjectNotFoundException
    {
        AcmGroup group = groupDao.findByName(groupId);

        if (group == null)
        {
            log.warn("Group [{}] was not found.", groupId);
            throw new AcmObjectNotFoundException("GROUP", null, "Group " + groupId + " was not found");
        }

        log.debug("Add User [{}] as member to Group [{}]", user.getUserId(), group.getName());
        group.addUserMember(user);

        if (flushInstructions)
        {
            groupDao.getEm().flush();
        }
        return group;
    }

    @Override
    public AcmGroup addUserMemberToGroup(AcmUser user, String groupId) throws AcmObjectNotFoundException
    {
        return addUserMemberToGroup(user, groupId, false);
    }

    @Override
    public AcmGroup removeSupervisor(String groupId, boolean applyToAll) throws AcmUserActionFailedException
    {
        AcmGroup group = groupDao.findByName(groupId);
        if (group == null)
        {
            log.error("Failed to remove supervisor from group. Group [{}] was not found.", groupId);
            throw new AcmUserActionFailedException("Remove Supervisor", "Group", -1L,
                    "Failed to remove supervisor from group. Group " + groupId + " was not found.", null);
        }
        group.setSupervisor(null);

        if (applyToAll)
        {
            // TODO: Remove supervisors from all objects assigned to this group
        }
        return group;
    }

    @Override
    @Transactional
    public AcmGroup removeUserMembersFromGroup(List<String> members, String groupId) throws AcmObjectNotFoundException
    {
        AcmGroup group = null;
        for (String user : members)
        {
            group = removeUserMemberFromGroup(user, groupId);
        }
        return group;
    }

    @Override
    @Transactional
    public AcmGroup removeUserMemberFromGroup(AcmUser user, String groupId) throws AcmObjectNotFoundException
    {
        AcmGroup group = groupDao.findByName(groupId);

        if (group == null)
        {
            log.warn("Group [{}] was not found.", groupId);
            throw new AcmObjectNotFoundException("GROUP", null, "Group " + groupId + " was not found");
        }

        group.removeUserMember(user);
        return group;
    }

    @Override
    @Transactional
    public AcmGroup removeUserMemberFromGroup(String userId, String groupId) throws AcmObjectNotFoundException
    {
        AcmUser user = userDao.findByUserId(userId);
        if (user == null)
        {
            log.warn("User [{}] was not found.", userId);
            throw new AcmObjectNotFoundException("USER", null, "User " + userId + " was not found");
        }
        log.debug("Removing User [{}] from Group [{}]", user.getUserId(), groupId);
        return removeUserMemberFromGroup(user, groupId);
    }

    @Override
    @Transactional
    public AcmGroup removeUserMemberFromGroup(String userMember, String groupId, boolean flushInstructions)
            throws AcmObjectNotFoundException
    {
        AcmGroup acmGroup = removeUserMemberFromGroup(userMember, groupId);
        if (flushInstructions)
        {
            groupDao.getEm().flush();
        }

        return acmGroup;
    }

    @Override
    @Transactional
    public AcmGroup saveAdHocSubGroup(String subGroupId, String parentId) throws AcmCreateObjectFailedException

    {
        AcmGroup parent = groupDao.findByName(parentId);
        AcmGroup subGroup = groupDao.findByName(subGroupId);

        if (parent == null || subGroup == null)
        {
            StringBuilder errorMessage = new StringBuilder();
            if (parent == null)
            {
                errorMessage.append("Parent group with id [").append(parentId).append("] not found.");
            }
            if (subGroup == null)
            {
                if (errorMessage.length() > 0)
                {
                    errorMessage.append(" ");
                }
                errorMessage.append("Subgroup with id [").append(subGroupId).append("] not found.");
            }

            throw new AcmCreateObjectFailedException("GROUP", errorMessage.toString(), null);
        }

        // If supervisor for the subgroup is empty, get from the parent group
        if (subGroup.getSupervisor() == null)
        {
            subGroup.setSupervisor(parent.getSupervisor());
        }

        subGroup.setAscendantsList(parent.getAscendantsList());
        subGroup.addAscendant(parentId);
        parent.addGroupMember(subGroup);

        return subGroup;
    }

    @Override
    @Transactional
    public AcmGroup saveAdHocSubGroup(AcmGroup subGroup, String parentId)
            throws AcmCreateObjectFailedException, AcmObjectAlreadyExistsException
    {
        AcmGroup parent = groupDao.findByName(parentId);
        if (parent == null)
        {
            throw new AcmCreateObjectFailedException("GROUP", "Parent group with id [" + parentId + "] not found", null);
        }

        // If supervisor for the subgroup is empty, get from the parent group
        if (subGroup.getSupervisor() == null)
        {
            subGroup.setSupervisor(parent.getSupervisor());
        }

        String groupName = MapperUtils.buildGroupName(subGroup.getName(), Optional.empty());
        subGroup.setName(groupName);
        subGroup.setDisplayName(groupName);
        AcmGroup acmGroup = createGroup(subGroup);
        parent.addGroupMember(acmGroup);
        return acmGroup;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
