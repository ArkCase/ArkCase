package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.AcmQueueDao;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by riste.tutureski on 9/25/2015.
 */
public class QueueToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmQueue>
{
    private AcmQueueDao acmQueueDao;

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
        properties.put(SearchConstants.PROPERTY_QUEUE_ORDER_S, in.getDisplayOrder());

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
        properties.put(SearchConstants.PROPERTY_QUEUE_ORDER_S, in.getDisplayOrder());

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
        boolean objectNotNull = acmObjectType != null;
        String ourClassName = AcmQueue.class.getName();
        String theirClassName = acmObjectType.getName();
        boolean classNames = theirClassName.equals(ourClassName);
        boolean isSupported = objectNotNull && classNames;

        return isSupported;
    }

    public AcmQueueDao getAcmQueueDao()
    {
        return acmQueueDao;
    }

    public void setAcmQueueDao(AcmQueueDao acmQueueDao)
    {
        this.acmQueueDao = acmQueueDao;
    }
}
