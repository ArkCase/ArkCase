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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.data.AcmDatabaseChangesEvent;
import com.armedia.acm.data.AcmObjectChangelist;
import com.armedia.acm.services.search.model.AcmObjectTypeOne;
import com.armedia.acm.services.search.model.AcmObjectTypeOneSolrConverter;
import com.armedia.acm.services.search.model.AcmObjectTypeTwo;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
import com.armedia.acm.services.search.model.solr.SolrDeleteDocumentByIdRequest;
import com.armedia.acm.spring.SpringContextHolder;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

        Capture<SolrAdvancedSearchDocument> capturedAdvancedSearch = EasyMock.newCapture();
        Capture<SolrContentDocument> capturedContentFileIndex = EasyMock.newCapture();
        Capture<SolrDeleteDocumentByIdRequest> capturedAdvancedDeleteRequest = EasyMock.newCapture();
        Capture<SolrDeleteDocumentByIdRequest> capturedContentFileIndexDeleteRequest = EasyMock.newCapture();

        mockSendToSolr.sendSolrAdvancedSearchDocuments(Arrays.asList(capture(capturedAdvancedSearch)));
        mockSendToSolr.sendSolrContentFileIndexDocuments(Arrays.asList(capture(capturedContentFileIndex)));
        mockSendToSolr.sendSolrAdvancedSearchDeletes(Arrays.asList(capture(capturedAdvancedDeleteRequest)));
        mockSendToSolr.sendSolrContentFileIndexDeletes(Arrays.asList(capture(capturedContentFileIndexDeleteRequest)));

        replayAll();

        service.onApplicationEvent(event);

        verifyAll();

        assertEquals(4, typeOneSolrConverter.getHandledObjectsCount());

        assertNotNull(capturedAdvancedSearch.getValue());

        assertNotNull(capturedAdvancedDeleteRequest.getValue());

    }

}
