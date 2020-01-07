package com.armedia.acm.services.users.dao.group;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;
import java.util.Set;

/**
 * @author riste.tutureski
 */
public class AcmGroupDao extends AcmAbstractDao<AcmGroup>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    @Transactional
    public AcmGroup findByName(String name)
    {
        return findByName(name, FlushModeType.AUTO);
    }

    @Transactional
    public AcmGroup findByName(String name, FlushModeType flushModeType)
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<AcmGroup> query = builder.createQuery(AcmGroup.class);
        Root<AcmGroup> group = query.from(AcmGroup.class);

        query.select(group);

        query.where(
                builder.and(
                        builder.equal(group.<String> get("name"), name)));

        TypedQuery<AcmGroup> dbQuery = getEm().createQuery(query);

        dbQuery.setFlushMode(flushModeType);

        AcmGroup acmGroup = null;

        try
        {
            acmGroup = dbQuery.getSingleResult();
        }
        catch (NoResultException e)
        {
            LOG.warn("There is no group with name [{}]", name);
        }
        catch (NonUniqueResultException e)
        {
            LOG.warn("There is no unique group found with name [{}]. More than one group has this name", name);
        }
        catch (Exception e)
        {
            LOG.error("Error while retrieving group by group name [{}]", name, e);
        }

        return acmGroup;
    }

    public List<AcmGroup> findByMatchingName(String name)
    {
        TypedQuery<AcmGroup> query = getEm().createQuery("SELECT ag FROM AcmGroup ag WHERE ag.name LIKE :name", AcmGroup.class);
        query.setParameter("name", name + "@%");
        return query.getResultList();
    }

    @Transactional
    public AcmGroup removeMembersFromGroup(String name, Set<AcmUser> membersToRemove)
    {
        AcmGroup group = findByName(name);

        if (group != null)
        {
            membersToRemove.forEach(member -> member.getGroups().remove(group));

            Set<AcmUser> userMembers = group.getUserMembers(false);
            userMembers.removeAll(membersToRemove);
        }
        return group;
    }

    @Transactional
    public List<AcmGroup> findByUserMember(AcmUser user)
    {
        TypedQuery<AcmGroup> query = getEm().createQuery("SELECT group FROM AcmGroup group "
                + "WHERE group.userMembers = :user", AcmGroup.class);
        query.setParameter("user", user);

        return query.getResultList();
    }

    public List<AcmGroup> findLdapGroupsByDirectory(String directoryName)
    {
        TypedQuery<AcmGroup> allLdapGroupsInDirectory = getEm().createQuery(
                "SELECT DISTINCT acmGroup FROM AcmGroup acmGroup LEFT JOIN FETCH acmGroup.userMembers "
                        + "WHERE acmGroup.type = com.armedia.acm.services.users.model.group.AcmGroupType.LDAP_GROUP "
                        + "AND acmGroup.directoryName = :directoryName",
                AcmGroup.class);
        allLdapGroupsInDirectory.setParameter("directoryName", directoryName);
        return allLdapGroupsInDirectory.getResultList();
    }

    public List<AcmGroup> findByTypeWithUsers(AcmGroupType type)
    {
        TypedQuery<AcmGroup> query = getEm().createQuery("SELECT DISTINCT acmGroup "
                + "FROM AcmGroup acmGroup "
                + "LEFT JOIN FETCH acmGroup.userMembers "
                + "WHERE acmGroup.type = :groupType", AcmGroup.class);
        query.setParameter("groupType", type);
        return query.getResultList();
    }

    public List<AcmGroup> findByStatusAndType(AcmGroupStatus status, AcmGroupType type)
    {
        TypedQuery<AcmGroup> query = getEm()
                .createQuery("SELECT acmGroup "
                        + "FROM AcmGroup acmGroup "
                        + "WHERE acmGroup.status = :status "
                        + "AND acmGroup.type = :groupType", AcmGroup.class);
        query.setParameter("status", status);
        query.setParameter("groupType", type);
        return query.getResultList();
    }

    @Transactional
    public void deleteGroup(AcmGroup group)
    {
        getEm().remove(group);
    }

    @Override
    protected Class<AcmGroup> getPersistenceClass()
    {
        return AcmGroup.class;
    }
}
