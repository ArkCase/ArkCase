package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.person.dao.OrganizationAssociationDao;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/23/14.
 */
public class OrganizationAssociationToSolrTransformer implements AcmObjectToSolrDocTransformer<OrganizationAssociation>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private OrganizationAssociationDao organizationAssociationDao;
    private UserDao userDao;

    @Override
    public List<OrganizationAssociation> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getOrganizationAssociationDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(OrganizationAssociation organizationAssociation)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        solrDoc.setId(organizationAssociation.getId() + "-ORGANIZATION-ASSOCIATION");
        solrDoc.setObject_id_s(organizationAssociation.getId() + "");
        solrDoc.setObject_type_s("ORGANIZATION-ASSOCIATION");
        solrDoc.setCreate_date_tdt(organizationAssociation.getCreated());
        solrDoc.setCreator_lcs(organizationAssociation.getCreator());
        solrDoc.setModified_date_tdt(organizationAssociation.getModified());
        solrDoc.setModifier_lcs(organizationAssociation.getModifier());

        solrDoc.setChild_id_s(organizationAssociation.getOrganization().getOrganizationId() + "");
        solrDoc.setChild_type_s("ORGANIZATION");
        solrDoc.setParent_id_s(organizationAssociation.getParentId() + "");
        solrDoc.setParent_type_s(organizationAssociation.getParentType());
        solrDoc.setParent_number_lcs(organizationAssociation.getParentTitle());

        solrDoc.setType_lcs(organizationAssociation.getOrganizationType());

        solrDoc.setName(organizationAssociation.getOrganization().getOrganizationValue() + " (" + organizationAssociation.getOrganizationType() + ")");

        solrDoc.setTitle_parseable(organizationAssociation.getOrganization().getOrganizationValue() + " ("
                + organizationAssociation.getOrganizationType() + ")");

        solrDoc.setParent_ref_s(organizationAssociation.getParentId() + "-" + organizationAssociation.getParentType());

        solrDoc.setDescription_parseable(organizationAssociation.getDescription());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(organizationAssociation.getCreator());
        if (creator != null)
        {
            solrDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(organizationAssociation.getModifier());
        if (modifier != null)
        {
            solrDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return solrDoc;
    }

    @Override
    public SolrDocument toSolrQuickSearch(OrganizationAssociation in)
    {
        // we don't want person associations in quick search, so just return null
        return null;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(OrganizationAssociation in)
    {
        // No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return OrganizationAssociation.class.equals(acmObjectType);
    }

    public OrganizationAssociationDao getOrganizationAssociationDao()
    {
        return organizationAssociationDao;
    }

    public void setOrganizationAssociationDao(OrganizationAssociationDao organizationAssociationDao)
    {
        this.organizationAssociationDao = organizationAssociationDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return OrganizationAssociation.class;
    }
}
