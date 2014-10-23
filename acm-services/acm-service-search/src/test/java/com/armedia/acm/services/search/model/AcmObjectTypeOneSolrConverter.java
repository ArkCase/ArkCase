package com.armedia.acm.services.search.model;

import com.armedia.acm.services.search.model.solr.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;

/**
 * Created by armdev on 10/23/14.
 */
public class AcmObjectTypeOneSolrConverter implements AcmObjectToSolrDocTransformer<AcmObjectTypeOne>
{
    private int handledObjectsCount = 0;

    private int handledQuickSearchCount = 0;

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmObjectTypeOne in)
    {
        ++handledObjectsCount;
        return new SolrAdvancedSearchDocument();
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmObjectTypeOne in)
    {
        ++handledQuickSearchCount;
        return new SolrDocument();
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmObjectTypeOne.class.equals(acmObjectType);
    }

    public int getHandledObjectsCount()
    {
        return handledObjectsCount;
    }

    public int getHandledQuickSearchCount()
    {
        return handledQuickSearchCount;
    }
}
