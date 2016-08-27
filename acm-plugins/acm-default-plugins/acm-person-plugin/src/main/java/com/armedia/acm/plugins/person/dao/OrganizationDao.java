package com.armedia.acm.plugins.person.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Organization;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

public class OrganizationDao extends AcmAbstractDao<Organization>
{
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


