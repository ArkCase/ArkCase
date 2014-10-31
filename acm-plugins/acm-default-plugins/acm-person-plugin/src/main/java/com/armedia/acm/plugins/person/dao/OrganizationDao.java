package com.armedia.acm.plugins.person.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Organization;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class OrganizationDao extends AcmAbstractDao<Organization>
{
    public Organization getOrganizationByOrganizationName(String companyName) throws AcmObjectNotFoundException {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<Organization> query = builder.createQuery(Organization.class);
        Root<Organization> d = query.from(Organization.class);
        query.select(d).where(builder.equal(d.get("organizationValue"), companyName));
        TypedQuery<Organization> dbQuery = getEm().createQuery(query);
        List<Organization> results = null;
        results = dbQuery.getResultList();
        if( results.isEmpty()){
            throw new AcmObjectNotFoundException("person",null, "Object not found",null);
        }
        return results.get(0);
    }

    @Transactional
    public void deleteOrganizationById(Long id){
        Organization organizationForDel = getEm().find(Organization.class,id);
        getEm().remove(organizationForDel);
    }

    @Override
    protected Class<Organization> getPersistenceClass()
    {
        return Organization.class;
    }
}


