/**
 *
 */
package com.armedia.acm.services.users.dao.group;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.GroupConstants;
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

        AcmGroup retval = null;

        try
        {
            retval = dbQuery.getSingleResult();
        } catch (Exception e)
        {
            if (e instanceof NoResultException)
            {
                LOG.info("There is no any group with name = " + name);
            } else if (e instanceof NonUniqueResultException)
            {
                LOG.info("There is no unique group found with name = " + name + ". More than one group has this name.");
            } else
            {
                LOG.error("Error while retrieving group by group name = " + name, e);
            }
        }

        return retval;
    }

    @Transactional
    public boolean deleteAcmGroupByName(String name)
    {
        Query queryToDelete = getEm().createQuery("SELECT group FROM AcmGroup group WHERE group.name = :groupName");
        queryToDelete.setParameter("groupName", name);

        AcmGroup groupToBeDeleted = (AcmGroup) queryToDelete.getSingleResult();
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
    public AcmGroup markGroupDelete(String name)
    {
        Query query = getEm().createQuery("SELECT group FROM AcmGroup group WHERE group.name = :name");
        query.setParameter("name", name);

        AcmGroup group = (AcmGroup) query.getSingleResult();

        if (group != null)
        {
            group.setStatus(AcmGroupStatus.DELETE);
            group.setParentGroup(null);
            if (group.getChildGroups() != null)
            {
                List<AcmGroup> childGroups = group.getChildGroups();
                group.setChildGroups(null);
                group = removeMemebres(group);

                childGroups.forEach(cg -> markGroupDelete(cg.getName()));
            }
        }
        return group;
    }

    private AcmGroup removeMemebres(AcmGroup group)
    {
        if (group.getMembers() != null)
        {
            // Clone the members that should be removed because of
            // concurrent modification exception
            Set<AcmUser> clonedMembers = getClonedMembers(group.getMembers());
            for (AcmUser member : clonedMembers)
            {
                group.removeMember(member);
            }
        }

        return save(group);
    }

    @Transactional
    public AcmGroup removeMembersFromGroup(String name, Set<AcmUser> membersToRemove)
    {
        Query query = getEm().createQuery("SELECT group FROM AcmGroup group WHERE group.name = :name");
        query.setParameter("name", name);

        AcmGroup group = (AcmGroup) query.getSingleResult();

        if (group != null)
        {
            if (group.getMembers() != null)
            {
                for (AcmUser member : membersToRemove)
                {
                    if (group.getMembers().contains(member))
                    {
                        group.removeMember(member);
                    }
                }
            }

            group = save(group);
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
        query.setParameter("groupStatus", AcmGroupStatus.DELETE);
        query.setParameter("userRoleState", "VALID");
        query.setParameter("directoryName", directoryName);
        query.setParameter("userState", "VALID");

        List<AcmGroup> groups = query.getResultList();

        if (groups != null && groups.size() > 0)
        {
            for (AcmGroup group : groups)
            {
                group.setStatus(AcmGroupStatus.INACTIVE);
                save(group);
            }
        }
    }

    @Transactional
    public List<AcmGroup> findByUserMember(AcmUser user)
    {
        Query query = getEm().createQuery("SELECT group FROM AcmGroup group WHERE group.members = :user");
        query.setParameter("user", user);

        List<AcmGroup> groups = query.getResultList();

        return groups;
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

        query.setParameter("name", group.getName() + GroupConstants.UUID_LIKE_STRING);
        query.setParameter("status", AcmGroupStatus.DELETE);
        List<AcmGroup> result = query.getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    public AcmGroup subGroupByUIName(AcmGroup group)
    {
        TypedQuery<AcmGroup> query = getEm().createQuery("SELECT group FROM AcmGroup group WHERE group.name LIKE :name AND " +
                "group.parentGroup.name = :uuidName AND group.status <> :status", AcmGroup.class);

        query.setParameter("name", group.getName() + GroupConstants.UUID_LIKE_STRING);
        query.setParameter("uuidName", group.getParentGroup().getName());
        query.setParameter("status", AcmGroupStatus.DELETE);
        List<AcmGroup> result = query.getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    @Transactional
    public AcmGroup findByMatchingName(String name)
    {
        CriteriaBuilder builder = this.getEm().getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(AcmGroup.class);
        Root group = query.from(AcmGroup.class);
        query.select(group);
        query.where(builder.and(new Predicate[]{builder.like(group.get("name"), name)}));

        TypedQuery dbQuery = this.getEm().createQuery(query);
        AcmGroup retval = null;


        try
        {
            retval = (AcmGroup) dbQuery.getSingleResult();
        } catch (NoResultException e)
        {
            LOG.info("There is no any group with name = {}", name);
        } catch (NonUniqueResultException e)
        {
            LOG.info("There is no unique group found with name = {}. More than one group has this name.", name);
        } catch (Exception e)
        {
            LOG.error("Error while retrieving group by group name = {}", name, e);
        }
        return retval;
    }

    @Override
    protected Class<AcmGroup> getPersistenceClass()
    {
        return AcmGroup.class;
    }

}
