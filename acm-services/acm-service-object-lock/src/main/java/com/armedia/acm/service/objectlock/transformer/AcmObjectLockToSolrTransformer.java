package com.armedia.acm.service.objectlock.transformer;

import com.armedia.acm.service.objectlock.dao.AcmObjectLockDao;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

/**
 * Created by nebojsha on 21.08.2015.
 */
public class AcmObjectLockToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmObjectLock>
{
    private AcmObjectLockDao dao;
    private UserDao userDao;

    @Override
    public List<AcmObjectLock> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmObjectLock in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-OBJECT_LOCK");
        solr.setObject_id_s(in.getId().toString());
        solr.setObject_type_s("OBJECT_LOCK");

        solr.setParent_id_s(in.getObjectId().toString());
        solr.setParent_type_s(in.getObjectType());
        solr.setParent_ref_s(in.getObjectId() + "-" + in.getObjectType());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            solr.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmObjectLock in)
    {

        SolrDocument solr = new SolrDocument();

        solr.setObject_id_s(in.getId().toString());
        solr.setObject_type_s("OBJECT_LOCK");
        solr.setId(in.getId() + "-OBJECT_LOCK");

        solr.setParent_object_id_s(in.getObjectId().toString());
        solr.setParent_object_type_s(in.getObjectType());
        solr.setParent_ref_s(in.getObjectId() + "-" + in.getObjectType());

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmObjectLock in)
    {
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmObjectLock.class.equals(acmObjectType);
    }

    public AcmObjectLockDao getDao()
    {
        return dao;
    }

    public void setDao(AcmObjectLockDao dao)
    {
        this.dao = dao;
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
        return AcmObjectLock.class;
    }
}
