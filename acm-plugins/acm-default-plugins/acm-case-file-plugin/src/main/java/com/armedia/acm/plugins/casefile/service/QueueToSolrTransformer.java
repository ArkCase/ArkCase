package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.AcmQueueDao;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by riste.tutureski on 9/25/2015.
 */
public class QueueToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmQueue>
{
    private AcmQueueDao acmQueueDao;
    private UserDao userDao;

    @Override
    public List<AcmQueue> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getAcmQueueDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmQueue in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-QUEUE");
        solr.setObject_id_s(in.getId().toString());
        solr.setObject_type_s("QUEUE");
        solr.setName(in.getName());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        Map<String, Object> properties = solr.getAdditionalProperties();

        properties.put(SearchConstants.PROPERTY_QUEUE_ID_S, in.getId().toString());
        properties.put(SearchConstants.PROPERTY_QUEUE_NAME_S, in.getName());
        properties.put(SearchConstants.PROPERTY_QUEUE_ORDER, in.getDisplayOrder());

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
    public SolrDocument toSolrQuickSearch(AcmQueue in)
    {
        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-QUEUE");
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s("QUEUE");
        solr.setName(in.getName());

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        Map<String, Object> properties = solr.getAdditionalProperties();

        properties.put(SearchConstants.PROPERTY_QUEUE_ID_S, in.getId().toString());
        properties.put(SearchConstants.PROPERTY_QUEUE_NAME_S, in.getName());
        properties.put(SearchConstants.PROPERTY_QUEUE_ORDER, in.getDisplayOrder());

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmQueue in)
    {
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmQueue.class.equals(acmObjectType);
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public AcmQueueDao getAcmQueueDao()
    {
        return acmQueueDao;
    }

    public void setAcmQueueDao(AcmQueueDao acmQueueDao)
    {
        this.acmQueueDao = acmQueueDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return AcmQueue.class;
    }
}
