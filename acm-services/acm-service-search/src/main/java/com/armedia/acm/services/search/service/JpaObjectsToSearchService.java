package com.armedia.acm.services.search.service;

import com.armedia.acm.data.AcmDatabaseChangesEvent;
import com.armedia.acm.data.AcmObjectChangelist;
import com.armedia.acm.services.search.model.solr.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by armdev on 10/23/14.
 */
public class JpaObjectsToSearchService implements ApplicationListener<AcmDatabaseChangesEvent>
{
    private SpringContextHolder springContextHolder;
    private Logger log = LoggerFactory.getLogger(getClass());
    private SendDocumentsToSolr sendToSolr;

    @Override
    public void onApplicationEvent(AcmDatabaseChangesEvent databaseChangesEvent)
    {
        updateObjectsInSolr(databaseChangesEvent.getObjectChangelist());
    }

    public void updateObjectsInSolr(AcmObjectChangelist changes)
    {
        int addCount = changes.getAddedObjects().size();
        int updateCount = changes.getUpdatedObjects().size();
        int deleteCount = changes.getDeletedObjects().size();

        log.debug("Sending objects to SOLR: " + addCount + " added; " + updateCount + " updated; " +
            deleteCount + " deleted.");

        List<SolrAdvancedSearchDocument> addOrUpdateSolrAdvancedSearch = new ArrayList<>();
        List<SolrAdvancedSearchDocument> deleteFromSolrAdvancedSearch = new ArrayList<>();
        List<SolrDocument> addOrUpdateSolrQuickSearch = new ArrayList<>();
        List<SolrDocument> deleteFromSolrQuickSearch = new ArrayList<>();

        Collection<AcmObjectToSolrDocTransformer> transformers = getSpringContextHolder().getAllBeansOfType(
                AcmObjectToSolrDocTransformer.class).values();

        for ( AcmObjectToSolrDocTransformer transformer : transformers )
        {
            toSolrDocuments(transformer, changes.getAddedObjects(), addOrUpdateSolrAdvancedSearch, addOrUpdateSolrQuickSearch);
            toSolrDocuments(transformer, changes.getUpdatedObjects(), addOrUpdateSolrAdvancedSearch, addOrUpdateSolrQuickSearch);
            toSolrDocuments(transformer, changes.getDeletedObjects(), deleteFromSolrAdvancedSearch, deleteFromSolrQuickSearch);
        }

        if ( !addOrUpdateSolrAdvancedSearch.isEmpty() )
        {
            getSendToSolr().sendSolrAdvancedSearchDocuments(addOrUpdateSolrAdvancedSearch);
        }

        if ( !addOrUpdateSolrQuickSearch.isEmpty() )
        {
            getSendToSolr().sendSolrQuickSearchDocuments(addOrUpdateSolrQuickSearch);
        }

    }

    private void toSolrDocuments(
            AcmObjectToSolrDocTransformer transformer,
            List<Object> jpaObjects,
            List<SolrAdvancedSearchDocument> solrAdvancedSearchDocs,
            List<SolrDocument> solrQuickSearchDocs)
    {
        for ( Object jpaObject : jpaObjects )
        {
            if ( transformer.isAcmObjectTypeSupported(jpaObject.getClass() ))
            {
                SolrAdvancedSearchDocument advancedSearchDocument = transformer.toSolrAdvancedSearch(jpaObject);
                solrAdvancedSearchDocs.add(advancedSearchDocument);

                SolrDocument quickSearchDocument = transformer.toSolrQuickSearch(jpaObject);
                solrQuickSearchDocs.add(quickSearchDocument);
            }
        }
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }


    public void setSendToSolr(SendDocumentsToSolr sendToSolr)
    {
        this.sendToSolr = sendToSolr;
    }

    public SendDocumentsToSolr getSendToSolr()
    {
        return sendToSolr;
    }
}
