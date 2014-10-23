package com.armedia.acm.services.search.service;

import com.armedia.acm.data.AcmDatabaseChangesEvent;
import com.armedia.acm.data.AcmObjectChangelist;
import com.armedia.acm.services.search.model.AcmObjectTypeOne;
import com.armedia.acm.services.search.model.AcmObjectTypeOneSolrConverter;
import com.armedia.acm.services.search.model.AcmObjectTypeTwo;
import com.armedia.acm.services.search.model.solr.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
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

        AcmObjectChangelist changes = new AcmObjectChangelist();
        changes.getAddedObjects().add(typeOne);
        changes.getAddedObjects().add(new AcmObjectTypeTwo());

        changes.getUpdatedObjects().add(new AcmObjectTypeTwo());

        AcmDatabaseChangesEvent event = new AcmDatabaseChangesEvent(changes);

        Map<String, AcmObjectToSolrDocTransformer> transformerMap = new HashMap<>();
        transformerMap.put("testConverter", typeOneSolrConverter);

        expect(mockContextHolder.getAllBeansOfType(AcmObjectToSolrDocTransformer.class)).andReturn(transformerMap);

        Capture<SolrAdvancedSearchDocument> capturedAdvancedSearch = new Capture<>();
        Capture<SolrDocument> capturedQuickSearch = new Capture<>();
        mockSendToSolr.sendSolrAdvancedSearchDocuments(Arrays.asList(capture(capturedAdvancedSearch)));
        mockSendToSolr.sendSolrQuickSearchDocuments(Arrays.asList(capture(capturedQuickSearch)));

        replayAll();

        service.onApplicationEvent(event);

        verifyAll();

        assertEquals(1, typeOneSolrConverter.getHandledObjectsCount());
        assertEquals(1, typeOneSolrConverter.getHandledQuickSearchCount());

        assertNotNull(capturedAdvancedSearch.getValue());
        assertNotNull(capturedQuickSearch.getValue());



    }


}
