package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/27/14.
 */
public class OrganizationToSolrTransformer implements AcmObjectToSolrDocTransformer<Organization>
{

    private OrganizationDao organizationDao;
    private UserDao userDao;

    @Override
    public List<Organization> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getOrganizationDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Organization org)
    {
        SolrAdvancedSearchDocument orgDoc = new SolrAdvancedSearchDocument();
        orgDoc.setId(org.getOrganizationId() + "-ORGANIZATION");
        orgDoc.setObject_type_s("ORGANIZATION");
        orgDoc.setObject_id_s(org.getOrganizationId() + "");

        orgDoc.setCreate_date_tdt(org.getCreated());
        orgDoc.setCreator_lcs(org.getCreator());
        orgDoc.setModified_date_tdt(org.getModified());
        orgDoc.setModifier_lcs(org.getModifier());

        orgDoc.setType_lcs(org.getOrganizationType());
        orgDoc.setValue_parseable(org.getOrganizationValue());

        orgDoc.setName(org.getOrganizationValue());
        orgDoc.setTitle_parseable(org.getOrganizationValue());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(org.getCreator());
        if (creator != null)
        {
            orgDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(org.getModifier());
        if (modifier != null)
        {
            orgDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return orgDoc;
    }

    @Override
    public SolrDocument toSolrQuickSearch(Organization in)
    {

        SolrDocument orgDoc = new SolrDocument();
        orgDoc.setId(in.getOrganizationId() + "-ORGANIZATION");
        orgDoc.setObject_type_s("ORGANIZATION");
        orgDoc.setObject_id_s(in.getOrganizationId() + "");


        orgDoc.setCreate_tdt(in.getCreated());
        orgDoc.setAuthor_s(in.getCreator());
        orgDoc.setLast_modified_tdt(in.getModified());
        orgDoc.setModifier_s(in.getModifier());

        orgDoc.setType_s(in.getOrganizationType());
        orgDoc.setData_s(in.getOrganizationValue());

        orgDoc.setName(in.getOrganizationValue());
        orgDoc.setTitle_parseable(in.getOrganizationValue());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            orgDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            orgDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return orgDoc;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(Organization in)
    {
        // No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Organization.class.equals(acmObjectType);
    }

    public OrganizationDao getOrganizationDao()
    {
        return organizationDao;
    }

    public void setOrganizationDao(OrganizationDao organizationDao)
    {
        this.organizationDao = organizationDao;
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
        return Organization.class;
    }
}
