package com.armedia.acm.services.users.service.group;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupConstants;
import com.armedia.acm.services.users.service.AcmUserRoleService;
import org.mule.api.MuleException;
import org.mule.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GroupServiceImpl implements GroupService
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private UserDao userDao;
    private AcmGroupDao groupDao;
    private AcmUserRoleService userRoleService;
    private ExecuteSolrQuery executeSolrQuery;

    private Pattern pattern = Pattern.compile(AcmGroupConstants.UUID_REGEX_STRING);

    @Override
    public AcmGroup findByName(String name)
    {
        return groupDao.findByName(name);
    }

    @Override
    public AcmGroup save(AcmGroup groupToSave)
    {
        return groupDao.save(groupToSave);
    }

    @Override
    public AcmGroup updateGroupWithMembers(AcmGroup group, Set<AcmUser> members)
    {
        for (AcmUser member : members)
        {
            group.addUserMember(member);
        }

        groupDao.save(group);

        return group;
    }

    @Override
    public Set<AcmUser> updateMembersWithDatabaseInfo(Set<AcmUser> members)
    {
        return members.stream()
                .map(it -> userDao.findByUserId(it.getUserId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public String getLdapGroupsForUser(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws MuleException
    {

        log.info("Taking all groups and subgroups from Solr. Authenticated user is {}", usernamePasswordAuthenticationToken.getName());

        String query = "object_type_s:GROUP AND object_sub_type_s:LDAP_GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE "
                + "AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        return executeSolrQuery.getResultsByPredefinedQuery(usernamePasswordAuthenticationToken,
                SolrCore.ADVANCED_SEARCH, query, 0, 1000, "name asc");

    }

    @Override
    public AcmGroup checkAndSaveAdHocGroup(AcmGroup group)
    {
        group.setName(group.getName() + "-UUID-" + UUID.getUUID());
        return groupDao.save(group);
    }

    @Override
    public boolean isUUIDPresentInTheGroupName(String str)
    {
        return pattern.matcher(str).matches();
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
                }
        ).orElse("");

        String query = String.format("object_type_s:USER AND groups_id_ss:%s",
                buildSafeGroupNameForSolrSearch(groupName));
        query = query.replace("_002E_", ".");
        query += statusQuery;

        log.debug("Executing query for users in group: [{}]", query);
        return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH,
                query, 0, 1000, "");
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

    /**
     * Creates or updates ad-hoc group based on the client info coming in from CRM
     *
     * @param //acmGroup group we want to rename
     * @param //         newName  group new name
     */
    @Override
    @Transactional
    public void renameGroup(AcmGroup acmGroup, String newName)
    {
        AcmGroup newGroup = new AcmGroup();
        newGroup.setName(String.format("%s-UUID-%s", newName, UUID.getUUID()));
        // copy the properties from the original found group.
        newGroup.setSupervisor(acmGroup.getSupervisor());
        newGroup.setType(acmGroup.getType());
        newGroup.setStatus(acmGroup.getStatus());
        newGroup.setDescription(acmGroup.getDescription());
        newGroup.setMemberGroups(acmGroup.getMemberGroups());
        newGroup.setMemberOfGroups(acmGroup.getMemberOfGroups());
        newGroup.setCreator(acmGroup.getCreator());
        newGroup.setUserMembers(acmGroup.getUserMembers());
        groupDao.save(newGroup);

        groupDao.markGroupDeleted(acmGroup.getName());
    }

    @Override
    @Transactional
    public List<AcmGroup> findByUserMember(AcmUser user)
    {
        return groupDao.findByUserMember(user);
    }

    @Override
    public AcmGroup markGroupDeleted(String groupName)
    {
        return groupDao.markGroupDeleted(groupName);
    }

    @Override
    @Transactional
    public AcmGroup setSupervisor(AcmUser supervisor, String groupId, boolean applyToAll) throws AcmUserActionFailedException
    {
        AcmGroup group = groupDao.findByName(groupId);

        if (group == null)
        {
            log.error("Failed to set supervisor to group. Group [{}] was not found.", groupId);
            throw new AcmUserActionFailedException("Set supervisor", "Group", -1L, "Failed to set supervisor to group. Group "
                    + groupId + " was not found.", null);
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
    public AcmGroup addMembersToAdHocGroup(Set<AcmUser> members, String groupId) throws AcmUserActionFailedException
    {
        AcmGroup group = groupDao.findByName(groupId);
        if (group == null)
        {
            log.error("Failed to add members to group. Group [{}] was not found.", groupId);
            throw new AcmUserActionFailedException("Save Members", "Group", -1L, "Failed to add members to group. Group "
                    + groupId + " was not found.", null);
        }
        members = updateMembersWithDatabaseInfo(members);
        members.forEach(group::addUserMember);
        members.forEach(member ->
                userRoleService.saveValidUserRolesPerAddedUserGroups(member.getUserId(), new HashSet<>(Arrays.asList(group))));
        return group;
    }

    @Override
    public AcmGroup removeSupervisor(String groupId, boolean applyToAll) throws AcmUserActionFailedException
    {
        AcmGroup group = groupDao.findByName(groupId);
        if (group == null)
        {
            log.error("Failed to remove supervisor from group. Group [{}] was not found.", groupId);
            throw new AcmUserActionFailedException("Remove Supervisor", "Group", -1L, "Failed to remove supervisor from group. Group "
                    + groupId + " was not found.", null);
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
    public AcmGroup removeMembersFromAdHocGroup(Set<AcmUser> members, String groupId)
    {
        members = updateMembersWithDatabaseInfo(members);
        AcmGroup group = groupDao.removeMembersFromGroup(groupId, members);
        members.forEach(member ->
                userRoleService.saveInvalidUserRolesPerRemovedUserGroups(member, new HashSet<>(Arrays.asList(group))));
        return group;
    }

    @Override
    @Transactional
    public AcmGroup saveAdHocSubGroup(AcmGroup subGroup, String parentId) throws AcmCreateObjectFailedException
    {
        AcmGroup parent = groupDao.findByName(parentId);
        if (parent == null)
        {
            throw new AcmCreateObjectFailedException("GROUP", "Parent group with id " +
                    parentId + " not found", null);
        }

        // If supervisor for the subgroup is empty, get from the parent group
        if (subGroup.getSupervisor() == null)
        {
            subGroup.setSupervisor(parent.getSupervisor());
        }

        subGroup.addAscendant(parentId);
        subGroup.setName(subGroup.getName() + "-UUID-" + UUID.getUUID());
        parent.addGroupMember(subGroup);
        return subGroup;
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

    public void setUserRoleService(AcmUserRoleService userRoleService)
    {
        this.userRoleService = userRoleService;
    }
}
