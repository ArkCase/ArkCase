package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 11.11.2014.
 */
public class UserToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmUser>
{

    private UserDao userDao;

    @Override
    public List<AcmUser> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getUserDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmUser in)
    {

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getUserId() + "-USER");
        solr.setObject_id_s(in.getUserId() + "");
        solr.setObject_type_s("USER");
        solr.setName(in.getFullName());
        solr.setFirst_name_lcs(in.getFirstName());
        solr.setLast_name_lcs(in.getLastName());
        solr.setEmail_lcs(in.getMail());

        solr.setTitle_parseable(in.getFullName());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setModified_date_tdt(in.getModified());

        solr.setStatus_lcs(in.getUserState());

        // Add groups
        solr.setGroups_id_ss(getGroupIds(in));

        solr.setAdditionalProperty("directory_name_s", in.getUserDirectoryName());
        solr.setAdditionalProperty("country_s", in.getCountry());
        solr.setAdditionalProperty("country_abbreviation_s", in.getCountryAbbreviation());
        solr.setAdditionalProperty("department_s", in.getDepartment());
        solr.setAdditionalProperty("company_s", in.getCompany());
        solr.setAdditionalProperty("title_s", in.getTitle());

        // TODO find a way to add Organization
        // TODO find a way to add Application Title
        // TODO find a way to add Location

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmUser in)
    {

        SolrDocument solr = new SolrDocument();
        solr.setName(in.getFullName());
        solr.setTitle_parseable(in.getFullName());
        solr.setObject_id_s(in.getUserId() + "");
        solr.setObject_type_s("USER");
        solr.setId(in.getUserId() + "-USER");

        solr.setCreate_tdt(in.getCreated());
        solr.setLast_modified_tdt(in.getModified());

        solr.setStatus_s(in.getUserState());

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmUser in)
    {
        // No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmUser.class.equals(acmObjectType);
    }

    private List<String> getGroupIds(AcmUser in)
    {
        List<String> retval = null;

        if (in != null && in.getGroups() != null && in.getGroups().size() > 0)
        {
            retval = new ArrayList<String>();

            for (AcmGroup group : in.getGroups())
            {
                retval.add(group.getName());
            }
        }

        return retval;
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
        return AcmUser.class;
    }
}
