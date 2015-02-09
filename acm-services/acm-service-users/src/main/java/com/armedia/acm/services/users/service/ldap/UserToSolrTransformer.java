package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;

import java.util.Date;
import java.util.List;

/**
* Created by marjan.stefanoski on 11.11.2014.
*/
public class UserToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmUser> {

    private UserDao userDao;

    @Override
    public List<AcmUser> getObjectsModifiedSince(Date lastModified, int start, int pageSize) {
        return getUserDao().findModifiedSince(lastModified,start,pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmUser in) {

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getUserId() + "-USER");
        solr.setObject_id_s(in.getUserId() + "");
        solr.setObject_type_s("USER");
        solr.setName(in.getFullName());
        solr.setFirst_name_lcs(in.getFirstName());
        solr.setLast_name_lcs(in.getLastName());
        solr.setEmail_lcs(in.getMail());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setModified_date_tdt(in.getModified());

        solr.setStatus_lcs(in.getUserState());

        //TODO find a way to add Organization
        //TODO find a way to add Application Title
        //TODO find a way to add Location
        //TODO find a way to add Groups

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmUser in) {

        SolrDocument solr = new SolrDocument();
        solr.setName(in.getFullName());
        solr.setObject_id_s(in.getUserId() + "");
        solr.setObject_type_s("USER");
        solr.setId(in.getUserId() + "-USER");

        solr.setCreate_tdt(in.getCreated());
        solr.setLast_modified_tdt(in.getModified());


        solr.setStatus_s(in.getUserState());

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmUser in) {
        //No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType) {

        boolean  objectNotNull = acmObjectType != null;
        String ourClassName = AcmUser.class.getName();
        String theirClassName = acmObjectType.getName();
        boolean classNames = theirClassName.equals(ourClassName);
        boolean isSupported = objectNotNull && classNames;

        return isSupported;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
