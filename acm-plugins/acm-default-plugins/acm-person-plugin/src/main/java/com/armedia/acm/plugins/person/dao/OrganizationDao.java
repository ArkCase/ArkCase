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


import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Organization;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;

import java.util.List;

public class OrganizationDao extends AcmAbstractDao<Organization>
{
    public Organization getOrganizationByOrganizationName(String companyName) throws AcmObjectNotFoundException
    {
        Organization organization = findByOrganizationName(companyName);
        if (organization == null)
        {
            throw new AcmObjectNotFoundException("organization", null, "Object not found", null);
        }
        return organization;
    }

    public Organization findByOrganizationName(String organizationName)
    {
        String query = "SELECT o FROM Organization o where o.organizationValue = :organizationName";
        TypedQuery<Organization> dbQuery = getEm().createQuery(query, Organization.class);
        dbQuery.setParameter("organizationName", organizationName);
        List<Organization> results = dbQuery.getResultList();
        if (results.isEmpty())
        {
            return null;
        }
        return results.get(0);
    }

    public List<Organization> findOrganizationsByName(String organizationName)
    {
        String query = "SELECT o FROM Organization o WHERE LOWER(o.organizationValue) = :organizationName";
        TypedQuery<Organization> dbQuery = getEm().createQuery(query, Organization.class);
        dbQuery.setParameter("organizationName", organizationName.toLowerCase());
        List<Organization> results = dbQuery.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return results;
    }

    @Transactional
    public void deleteOrganizationById(Long id)
    {
        Organization organizationForDel = getEm().find(Organization.class, id);
        getEm().remove(organizationForDel);
    }

    @Override
    protected Class<Organization> getPersistenceClass()
    {
        return Organization.class;
    }
}
