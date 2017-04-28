package com.armedia.acm.plugins.person.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class OrganizationAssociationDao extends AcmAbstractDao<OrganizationAssociation>
{
    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

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
                        "AND organizationAssociation.organization.organizationId = organization.organizationId"
        );

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
                        "AND   organizationAssociation.organization.organizationId = organization.organizationId"
        );

        organizationInAssociation.setParameter("organizationAssociationId", organizationAssociationId);

        Organization found = (Organization) organizationInAssociation.getSingleResult();

        return found;
    }

    public OrganizationAssociation findByOrganizationIdOrganizationTypeParentIdParentTypeSilent(Long organizationId, String organizationType, Long parentId, String parentType)
    {
        Query select = getEntityManager().createQuery(
                "SELECT organizationAssociation " +
                        "FROM  OrganizationAssociation organizationAssociation " +
                        "WHERE organizationAssociation.organization.organizationId = :organizationId " +
                        "AND organizationAssociation.organizationType = :organizationType " +
                        "AND organizationAssociation.parentId = :parentId " +
                        "AND organizationAssociation.parentType = :parentType"
        );

        select.setParameter("organizationId", organizationId);
        select.setParameter("organizationType", organizationType);
        select.setParameter("parentId", parentId);
        select.setParameter("parentType", parentType);

        OrganizationAssociation retval = null;

        try
        {
            retval = (OrganizationAssociation) select.getSingleResult();
        } catch (NoResultException e1)
        {
            LOG.debug("There is no any OrganizationAssociation result for organizationId=" + organizationId + ", organizationType=" + organizationType + ", parentId=" + parentId + ", parentType=" + parentType);
        } catch (Exception e2)
        {
            LOG.debug("Cannot take OrganizationAssociation result for organizationId=" + organizationId + ", organizationType=" + organizationType + ", parentId=" + parentId + ", parentType=" + parentType);
        }

        return retval;
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


