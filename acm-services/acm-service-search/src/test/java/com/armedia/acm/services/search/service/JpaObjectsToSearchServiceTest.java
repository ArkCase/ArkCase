package com.armedia.acm.services.search.service;

import com.armedia.acm.data.AcmDatabaseChangesEvent;
import com.armedia.acm.data.AcmObjectChangelist;
import com.armedia.acm.services.search.model.AcmObjectTypeOne;
import com.armedia.acm.services.search.model.AcmObjectTypeOneSolrConverter;
import com.armedia.acm.services.search.model.AcmObjectTypeTwo;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDeleteDocumentByIdRequest;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.spring.SpringContextHolder;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Created by armdev on 10/23/14.
 */
public class JpaObjectsToSearchServiceTest extends EasyMockSupport
{
    private JpaObjectsToSearchService service;

    private AcmObjectTypeOneSolrConverter typeOneSolrConverter;

    private SpringContextHolder mockContextHolder;
    private SendDocumentsToSolr mockSendToSolr;

    @Before
    public void setUp()
    {
        service = new JpaObjectsToSearchService();

        mockContextHolder = createMock(SpringContextHolder.class);
        mockSendToSolr = createMock(SendDocumentsToSolr.class);

        typeOneSolrConverter = new AcmObjectTypeOneSolrConverter();

        service.setSpringContextHolder(mockContextHolder);
        service.setSendToSolr(mockSendToSolr);
    }

    @Test
    public void jpaObjectsToSearch() throws Exception
    {
        AcmObjectTypeOne typeOne = new AcmObjectTypeOne();
        AcmObjectTypeOne deleteMe = new AcmObjectTypeOne();

        AcmObjectChangelist changes = new AcmObjectChangelist();
        changes.getAddedObjects().add(typeOne);
        changes.getAddedObjects().add(new AcmObjectTypeTwo());

        changes.getUpdatedObjects().add(new AcmObjectTypeTwo());

        changes.getDeletedObjects().add(deleteMe);

        AcmDatabaseChangesEvent event = new AcmDatabaseChangesEvent(changes);

        Map<String, AcmObjectToSolrDocTransformer> transformerMap = new HashMap<>();
        transformerMap.put("testConverter", typeOneSolrConverter);

        expect(mockContextHolder.getAllBeansOfType(AcmObjectToSolrDocTransformer.class)).andReturn(transformerMap);

        Capture<SolrAdvancedSearchDocument> capturedAdvancedSearch = new Capture<>();
        Capture<SolrDocument> capturedQuickSearch = new Capture<>();
        Capture<SolrAdvancedSearchDocument> capturedContentFileIndex = new Capture<>();
        Capture<SolrDeleteDocumentByIdRequest> capturedAdvancedDeleteRequest = new Capture<>();
        Capture<SolrDeleteDocumentByIdRequest> capturedQuickDeleteRequest = new Capture<>();
        Capture<SolrDeleteDocumentByIdRequest> capturedContentFileIndexDeleteRequest = new Capture<>();

        mockSendToSolr.sendSolrAdvancedSearchDocuments(Arrays.asList(capture(capturedAdvancedSearch)));
        mockSendToSolr.sendSolrQuickSearchDocuments(Arrays.asList(capture(capturedQuickSearch)));
        mockSendToSolr.sendSolrContentFileIndexDocuments(Arrays.asList(capture(capturedContentFileIndex)));
        mockSendToSolr.sendSolrAdvancedSearchDeletes(Arrays.asList(capture(capturedAdvancedDeleteRequest)));
        mockSendToSolr.sendSolrQuickSearchDeletes(Arrays.asList(capture(capturedQuickDeleteRequest)));
        mockSendToSolr.sendSolrContentFileIndexDeletes(Arrays.asList(capture(capturedContentFileIndexDeleteRequest)));

        replayAll();

        service.onApplicationEvent(event);

        verifyAll();

        assertEquals(4, typeOneSolrConverter.getHandledObjectsCount());
        assertEquals(2, typeOneSolrConverter.getHandledQuickSearchCount());

        assertNotNull(capturedAdvancedSearch.getValue());
        assertNotNull(capturedQuickSearch.getValue());

        assertNotNull(capturedAdvancedDeleteRequest.getValue());
        assertNotNull(capturedQuickDeleteRequest.getValue());


    }


}
