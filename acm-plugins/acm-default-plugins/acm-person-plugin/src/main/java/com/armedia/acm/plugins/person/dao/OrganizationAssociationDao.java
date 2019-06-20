package com.armedia.acm.plugins.person.dao;

/*-
 * #%L
 * ACM Default Plugin: Person
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
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;

public class OrganizationAssociationDao extends AcmAbstractDao<OrganizationAssociation>
{
    private transient final Logger LOG = LogManager.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected Class<OrganizationAssociation> getPersistenceClass()
    {
        return OrganizationAssociation.class;
    }

    public List<Organization> findOrganizationByParentIdAndParentType(String parentType, Long parentId)
    {
        Query organizationInAssociation = getEntityManager().createQuery(
                "SELECT organization FROM OrganizationAssociation organizationAssociation, Organization organization " +
                        "WHERE organizationAssociation.parentType = :parentType " +
                        "AND organizationAssociation.parentId = :parentId " +
                        "AND organizationAssociation.organization.organizationId = organization.organizationId");

        organizationInAssociation.setParameter("parentType", parentType.toUpperCase());
        organizationInAssociation.setParameter("parentId", parentId);

        List<Organization> retrival = (List<Organization>) organizationInAssociation.getResultList();

        return retrival;

    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public Organization findOrganizationByOrganizationAssociationId(Long organizationAssociationId)
    {
        Query organizationInAssociation = getEntityManager().createQuery(
                "SELECT organization FROM  OrganizationAssociation organizationAssociation, Organization organization " +
                        "WHERE organizationAssociation.id = :organizationAssociationId " +
                        "AND   organizationAssociation.organization.organizationId = organization.organizationId");

        organizationInAssociation.setParameter("organizationAssociationId", organizationAssociationId);

        Organization found = (Organization) organizationInAssociation.getSingleResult();

        return found;
    }

    @Transactional
    public void deleteOrganizationAssociationById(Long id)
    {
        Query queryToDelete = getEntityManager().createQuery(
                "SELECT organizationAssociation " + "FROM  OrganizationAssociation organizationAssociation " +
                        "WHERE organizationAssociation.id = :organizationAssociationId ");

        queryToDelete.setParameter("organizationAssociationId", id);

        OrganizationAssociation organizationAssociationToBeDeleted = (OrganizationAssociation) queryToDelete.getSingleResult();
        entityManager.remove(organizationAssociationToBeDeleted);

    }
}
