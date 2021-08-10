package com.armedia.acm.services.search.service;

/*-
 * #%L
 * ACM Service: Search
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.data.AcmDatabaseChangesEvent;
import com.armedia.acm.data.AcmObjectChangelist;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
import com.armedia.acm.services.search.model.solr.SolrDeleteDocumentByIdRequest;
import com.armedia.acm.services.search.model.solr.SolrDocumentId;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
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
    private Logger log = LogManager.getLogger(getClass());
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
        List<SolrContentDocument> addOrUpdateSolrContentFile = new ArrayList<>();
        List<SolrContentDocument> deleteFromSolrContentFile = new ArrayList<>();

        Collection<AcmObjectToSolrDocTransformer> transformers = getSpringContextHolder()
                .getAllBeansOfType(AcmObjectToSolrDocTransformer.class).values();

        Map<Class<?>, List<AcmObjectToSolrDocTransformer>> typeTransformerMap = transformers.stream()
                .collect(Collectors.groupingBy(AcmObjectToSolrDocTransformer::getAcmObjectTypeSupported));

        toSolrDocuments(typeTransformerMap, changes.getAddedObjects(), addOrUpdateSolrAdvancedSearch,
                addOrUpdateSolrContentFile);
        toSolrDocuments(typeTransformerMap, changes.getUpdatedObjects(), addOrUpdateSolrAdvancedSearch,
                addOrUpdateSolrContentFile);
        toSolrDocuments(typeTransformerMap, changes.getDeletedObjects(), deleteFromSolrAdvancedSearch,
                deleteFromSolrContentFile);

        if (!addOrUpdateSolrAdvancedSearch.isEmpty())
        {
            getSendToSolr().sendSolrAdvancedSearchDocuments(addOrUpdateSolrAdvancedSearch);
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
            List<SolrAdvancedSearchDocument> solrAdvancedSearchDocs, List<SolrContentDocument> solrContentFileDocs)
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
                        // transformers can return null if they don't want to add to the advanced repo...

                        try
                        {
                            SolrAdvancedSearchDocument advancedSearchDocument = transformer.toSolrAdvancedSearch(jpaObject);
                            if (advancedSearchDocument != null)
                            {
                                solrAdvancedSearchDocs.add(advancedSearchDocument);
                            }
                        }
                        catch (Exception e)
                        {
                            log.error("[{}]: unable to generate Advanced search document for [{}]. Reason: [{}]", transformer.getClass(),
                                    jpaObject.toString(), e.getMessage());
                        }

                        try
                        {
                            JSONArray docUpdates = transformer.childrenUpdatesToSolr(jpaObject);
                            if (docUpdates != null && docUpdates.length() != 0)
                            {
                                getSendToSolr().sendSolrDocuments("solrAdvancedSearch.in", docUpdates.toString());
                            }
                        }
                        catch (Exception e)
                        {
                            log.error("[{}]: unable to add index updates for [{}]. Reason: [{}]", transformer.getClass(),
                                    jpaObject.toString(), e.getMessage());
                        }

                        try
                        {
                            SolrContentDocument contentFileDocument = transformer.toContentFileIndex(jpaObject);
                            if (contentFileDocument != null)
                            {
                                solrContentFileDocs.add(contentFileDocument);
                            }
                        }
                        catch (Exception e)
                        {
                            log.error("[{}]: unable to generate Content file index for [{}]. Reason: [{}]", transformer.getClass(),
                                    jpaObject.toString(), e.getMessage());
                        }
                    }
                }
            }
        }
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public SendDocumentsToSolr getSendToSolr()
    {
        return sendToSolr;
    }

    public void setSendToSolr(SendDocumentsToSolr sendToSolr)
    {
        this.sendToSolr = sendToSolr;
    }
}
