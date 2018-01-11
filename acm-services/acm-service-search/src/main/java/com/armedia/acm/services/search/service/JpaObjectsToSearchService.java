package com.armedia.acm.services.search.service;

import com.armedia.acm.data.AcmDatabaseChangesEvent;
import com.armedia.acm.data.AcmObjectChangelist;
import com.armedia.acm.services.search.model.solr.*;
import com.armedia.acm.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        log.debug("Sending objects to SOLR: " + addCount + " added; " + updateCount + " updated; " + deleteCount + " deleted.");

        List<SolrAdvancedSearchDocument> addOrUpdateSolrAdvancedSearch = new ArrayList<>();
        List<SolrAdvancedSearchDocument> deleteFromSolrAdvancedSearch = new ArrayList<>();
        List<SolrDocument> addOrUpdateSolrQuickSearch = new ArrayList<>();
        List<SolrDocument> deleteFromSolrQuickSearch = new ArrayList<>();
        List<SolrContentDocument> addOrUpdateSolrContentFile = new ArrayList<>();
        List<SolrContentDocument> deleteFromSolrContentFile = new ArrayList<>();

        Collection<AcmObjectToSolrDocTransformer> transformers = getSpringContextHolder()
                .getAllBeansOfType(AcmObjectToSolrDocTransformer.class).values();

        Map<Class<?>, List<AcmObjectToSolrDocTransformer>> typeTransformerMap = transformers.stream()
                .collect(Collectors.groupingBy(AcmObjectToSolrDocTransformer::getAcmObjectTypeSupported));

        toSolrDocuments(typeTransformerMap, changes.getAddedObjects(), addOrUpdateSolrAdvancedSearch, addOrUpdateSolrQuickSearch,
                addOrUpdateSolrContentFile);
        toSolrDocuments(typeTransformerMap, changes.getUpdatedObjects(), addOrUpdateSolrAdvancedSearch, addOrUpdateSolrQuickSearch,
                addOrUpdateSolrContentFile);
        toSolrDocuments(typeTransformerMap, changes.getDeletedObjects(), deleteFromSolrAdvancedSearch, deleteFromSolrQuickSearch,
                deleteFromSolrContentFile);

        if (!addOrUpdateSolrAdvancedSearch.isEmpty())
        {
            getSendToSolr().sendSolrAdvancedSearchDocuments(addOrUpdateSolrAdvancedSearch);
        }

        if (!addOrUpdateSolrQuickSearch.isEmpty())
        {
            getSendToSolr().sendSolrQuickSearchDocuments(addOrUpdateSolrQuickSearch);
        }

        if (!addOrUpdateSolrContentFile.isEmpty())
        {
            getSendToSolr().sendSolrContentFileIndexDocuments(addOrUpdateSolrContentFile);
        }

        if (!deleteFromSolrAdvancedSearch.isEmpty())
        {
            log.debug("Docs to delete from advanced search: " + deleteFromSolrAdvancedSearch.size());
            // for delete, we need to send a special request format including only the document ID. So we copy this
            // list into a delete request list.
            List<SolrDeleteDocumentByIdRequest> deletes = copyDeleteDocsToDeleteRequests(deleteFromSolrAdvancedSearch);
            getSendToSolr().sendSolrAdvancedSearchDeletes(deletes);
        }

        if (!deleteFromSolrQuickSearch.isEmpty())
        {
            // for delete, we need to send a special request format including only the document ID. So we copy this
            // list into a delete request list.
            List<SolrDeleteDocumentByIdRequest> deletes = copyDeleteDocsToDeleteRequests(deleteFromSolrQuickSearch);
            getSendToSolr().sendSolrQuickSearchDeletes(deletes);
        }

        if (!deleteFromSolrContentFile.isEmpty())
        {

            // for delete, we need to send a special request format including only the document ID. So we copy this
            // list into a delete request list.
            List<SolrDeleteDocumentByIdRequest> deletes = copyDeleteDocsToDeleteRequests(deleteFromSolrContentFile);
            getSendToSolr().sendSolrContentFileIndexDeletes(deletes);
        }

    }

    private List<SolrDeleteDocumentByIdRequest> copyDeleteDocsToDeleteRequests(List<? extends SolrBaseDocument> deletedDocs)
    {
        List<SolrDeleteDocumentByIdRequest> deletes = new ArrayList<>(deletedDocs.size());
        for (SolrBaseDocument delete : deletedDocs)
        {
            deletes.add(new SolrDeleteDocumentByIdRequest(new SolrDocumentId(delete.getId())));
        }
        return deletes;
    }

    private void toSolrDocuments(Map<Class<?>, List<AcmObjectToSolrDocTransformer>> typeTransformerMap, List<Object> jpaObjects,
            List<SolrAdvancedSearchDocument> solrAdvancedSearchDocs, List<SolrDocument> solrQuickSearchDocs,
            List<SolrContentDocument> solrContentFileDocs)
    {

        Map<Class<?>, List<Object>> objects = jpaObjects.stream().collect(Collectors.groupingBy(Object::getClass));
        for (Class<?> clazz : objects.keySet())
        {
            List<AcmObjectToSolrDocTransformer> transformers = typeTransformerMap.get(clazz);
            if (transformers != null)
            {
                for (Object jpaObject : objects.get(clazz))
                {
                    for (AcmObjectToSolrDocTransformer transformer : transformers)
                    {
                        // transformers can return null if they don't want to add to the advanced or quick search
                        // repo...

                        try
                        {
                            SolrAdvancedSearchDocument advancedSearchDocument = transformer.toSolrAdvancedSearch(jpaObject);
                            if (advancedSearchDocument != null)
                            {
                                solrAdvancedSearchDocs.add(advancedSearchDocument);
                            }
                        } catch (Exception e)
                        {
                            log.error("[{}]: unable to generate Advanced search document for [{}]", transformer.getClass(), jpaObject.toString());
                        }

                        try
                        {
                            SolrDocument quickSearchDocument = transformer.toSolrQuickSearch(jpaObject);
                            if (quickSearchDocument != null)
                            {
                                solrQuickSearchDocs.add(quickSearchDocument);
                            }
                        } catch (Exception e)
                        {
                            log.error("[{}]: unable to generate Quick search document for [{}]", transformer.getClass(), jpaObject.toString());
                        }

                        try
                        {
                            SolrContentDocument contentFileDocument = transformer.toContentFileIndex(jpaObject);
                            if (contentFileDocument != null)
                            {
                                solrContentFileDocs.add(contentFileDocument);
                            }
                        } catch (Exception e)
                        {
                            log.error("[{}]: unable to generate Content file index for [{}]", transformer.getClass(), jpaObject.toString());
                        }
                    }
                }
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
