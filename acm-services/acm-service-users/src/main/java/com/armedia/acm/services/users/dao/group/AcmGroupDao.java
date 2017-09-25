package com.armedia.acm.services.users.dao.group;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRoleState;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupConstants;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author riste.tutureski
 */
public class AcmGroupDao extends AcmAbstractDao<AcmGroup>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Transactional
    public AcmGroup findByName(String name)
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<AcmGroup> query = builder.createQuery(AcmGroup.class);
        Root<AcmGroup> group = query.from(AcmGroup.class);

        query.select(group);

        query.where(
                builder.and(
                        builder.equal(group.<String>get("name"), name)
                )
        );

        TypedQuery<AcmGroup> dbQuery = getEm().createQuery(query);

        AcmGroup acmGroup = null;

        try
        {
            acmGroup = dbQuery.getSingleResult();
        } catch (NoResultException e)
        {
            LOG.warn("There is no group with name [{}]", name);
        } catch (NonUniqueResultException e)
        {
            LOG.warn("There is no unique group found with name [{}]. More than one group has this name", name);
        } catch (Exception e)
        {
            LOG.error("Error while retrieving group by group name [{}]", name, e);
        }

        return acmGroup;
    }

    @Transactional
    public boolean deleteAcmGroupByName(String name)
    {
        AcmGroup groupToBeDeleted = findByName(name);
        if (groupToBeDeleted != null)
        {
            getEm().remove(groupToBeDeleted);
            return true;
        } else
        {
            return false;
        }
    }

    @Transactional
    public AcmGroup markGroupDeleted(AcmGroup group)
    {
        group.setAscendantsList(null);
        group.setStatus(AcmGroupStatus.DELETE);

        group.getUserMembers()
                .forEach(user -> user.getGroups().remove(group));
        group.getMemberOfGroups()
                .forEach(acmGroup -> acmGroup.getMemberGroups().remove(group));
        group.getMemberGroups()
                .forEach(acmGroup -> acmGroup.getMemberOfGroups().remove(group));

        group.setUserMembers(new HashSet<>());
        group.setMemberOfGroups(new HashSet<>());
        group.setMemberGroups(new HashSet<>());

        return save(group);
    }

    public void markRolesByGroupInvalid(String groupName)
    {
        Query markInvalid = getEm().createQuery("UPDATE AcmUserRole aur set aur.userRoleState = :state "
                + "WHERE aur.roleName = :groupName");
        markInvalid.setParameter("state", AcmUserRoleState.INVALID.name());
        markInvalid.setParameter("groupName", groupName);
        markInvalid.executeUpdate();
    }

    @Transactional
    public AcmGroup removeMembersFromGroup(String name, Set<AcmUser> membersToRemove)
    {
        AcmGroup group = findByName(name);

        if (group != null)
        {
            membersToRemove.forEach(member -> member.getGroups().remove(group));

            Set<AcmUser> userMembers = group.getUserMembers();
            userMembers.removeAll(membersToRemove);
        }
        return group;
    }

    /**
     * Mark user groups that are associated exclusively to a single directory inactive.
     *
     * @param directoryName LDAP directory name
     * @param groupType     user group type
     */
    @Transactional
    public void markAllGroupsInactive(String directoryName, String groupType)
    {
        // the following query should read:
        // "select groups that are associated exclusively to a single directory"
        Query query = getEm().createQuery(
                "SELECT group FROM AcmGroup group " +
                        "WHERE group.type = :groupType " +
                        "AND group.status != :groupStatus " +
                        "AND group.name NOT IN " +
                        "(SELECT DISTINCT userRole.roleName FROM AcmUserRole userRole " +
                        "WHERE userRole.userRoleState = :userRoleState " +
                        "AND userRole.userId IN " +
                        // valid users retrieved from other directories
                        "(SELECT user.userId FROM AcmUser user " +
                        "WHERE user.userDirectoryName != :directoryName " +
                        "AND user.userState = :userState))");
        query.setParameter("groupType", groupType);
        query.setParameter("groupStatus", AcmGroupStatus.DELETE.name());
        query.setParameter("userRoleState", "VALID");
        query.setParameter("directoryName", directoryName);
        query.setParameter("userState", AcmUserState.VALID);

        List<AcmGroup> groups = query.getResultList();

        for (AcmGroup group : groups)
        {
            group.setStatus(AcmGroupStatus.INACTIVE);
            save(group);
        }
    }

    @Transactional
    public List<AcmGroup> findByUserMember(AcmUser user)
    {
        Query query = getEm().createQuery("SELECT group FROM AcmGroup group WHERE group.userMembers = :user");
        query.setParameter("user", user);

        return (List<AcmGroup>) query.getResultList();
    }

    private Set<AcmUser> getClonedMembers(Set<AcmUser> members)
    {
        @SuppressWarnings("unchecked")
        Set<AcmUser> clonedMembers = (HashSet) new HashSet<>(members).clone();

        return clonedMembers;
    }

    public AcmGroup groupByUIName(AcmGroup group)
    {
        TypedQuery<AcmGroup> query = getEm().createQuery("SELECT group FROM AcmGroup group WHERE group.name LIKE :name AND " +
                "group.parentGroup IS NULL AND group.status <> :status", AcmGroup.class);

        query.setParameter("name", group.getName() + AcmGroupConstants.UUID_LIKE_STRING);
        query.setParameter("status", AcmGroupStatus.DELETE.name());
        List<AcmGroup> result = query.getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Find ad-hoc group by matching name.
     * <p>
     * We need this since UI names and internal names of ad-hoc groups differ
     * (ArkCase is adding `-UUID-...` suffix internally)
     *
     * @param name group name
     * @return ad-hoc group if found, null otherwise
     */
    @Transactional
    public AcmGroup findByMatchingName(String name)
    {
        CriteriaBuilder builder = this.getEm().getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(AcmGroup.class);
        Root group = query.from(AcmGroup.class);
        query.select(group);
        query.where(builder.and(new Predicate[] { builder.like(group.<String>get("name"), name + "-UUID-%") }));

        TypedQuery dbQuery = this.getEm().createQuery(query);
        AcmGroup acmGroup = null;

        try
        {
            acmGroup = (AcmGroup) dbQuery.getSingleResult();
        } catch (NoResultException e)
        {
            LOG.warn("There is no group with name [{}]", name);
        } catch (NonUniqueResultException e)
        {
            LOG.warn("There is no unique group found with name [{}]. More than one group has this name", name);
        } catch (Exception e)
        {
            LOG.error("Error while retrieving group by group name [{}]", name, e);
        }
        return acmGroup;
    }

    public List<AcmGroup> findLdapGroupsByDirectory(String directoryName)
    {
        TypedQuery<AcmGroup> allLdapGroupsInDirectory = getEm().
                createQuery("SELECT DISTINCT acmGroup FROM AcmGroup acmGroup LEFT JOIN FETCH acmGroup.userMembers "
                        + "WHERE acmGroup.type = :groupType AND acmGroup.directoryName = :directoryName", AcmGroup.class);
        allLdapGroupsInDirectory.setParameter("groupType", AcmGroupType.LDAP_GROUP);
        allLdapGroupsInDirectory.setParameter("directoryName", directoryName);
        return allLdapGroupsInDirectory.getResultList();
    }

    @Override
    protected Class<AcmGroup> getPersistenceClass()
    {
        return AcmGroup.class;
    }

}
