/**
 * 
 */
package com.armedia.acm.service.objecthistory.service;

/*-
 * #%L
 * ACM Service: Object History Service
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
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.service.objecthistory.dao.AcmObjectHistoryDao;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class AcmObjectHistoryServiceImplTest extends EasyMockSupport
{

    private AcmObjectHistoryServiceImpl acmObjectHistoryService;
    private AcmObjectHistoryDao mockObjectHistoryDao;
    private AcmObjectHistoryEventPublisher mockAcmObjectHistoryEventPublisher;

    @Before
    public void setUp() throws Exception
    {
        mockObjectHistoryDao = createMock(AcmObjectHistoryDao.class);
        mockAcmObjectHistoryEventPublisher = createMock(AcmObjectHistoryEventPublisher.class);

        acmObjectHistoryService = new AcmObjectHistoryServiceImpl();
        acmObjectHistoryService.setAcmObjectHistoryDao(mockObjectHistoryDao);
        acmObjectHistoryService.setAcmObjectHistoryEventPublisher(mockAcmObjectHistoryEventPublisher);
        acmObjectHistoryService.setObjectConverter(ObjectConverter.createObjectConverterForTests());
    }

    @Test
    public void testSave()
    {
        String userId = "user-id";
        String type = "type";
        Object obj = new Object();
        Long objectId = 1L;
        String objectType = "object-type";
        Date date = new Date();
        String ipAddress = "ip-address";

        AcmMarshaller converter = ObjectConverter.createJSONMarshallerForTests();
        String json = converter.marshal(obj);

        AcmObjectHistory history = new AcmObjectHistory();

        history.setId(9L);
        history.setUserId(userId);
        history.setType(type);
        history.setObjectId(objectId);
        history.setObjectType(objectType);
        history.setObjectString(json);
        history.setDate(date);

        Capture<AcmObjectHistory> found = EasyMock.newCapture();

        expect(mockObjectHistoryDao.save(capture(found))).andReturn(history);
        mockAcmObjectHistoryEventPublisher.publishCreatedEvent(history, ipAddress);
        expectLastCall().anyTimes();

        replayAll();

        AcmObjectHistory saved = acmObjectHistoryService.save(history, ipAddress);

        assertEquals(found.getValue().getObjectId(), saved.getObjectId());
    }

    @Test
    public void testSave_withParameters()
    {
        String userId = "user-id";
        String type = "type";
        Object obj = new Object();
        Long objectId = 1L;
        String objectType = "object-type";
        Date date = new Date();
        String ipAddress = "ip-address";

        AcmMarshaller converter = ObjectConverter.createJSONMarshallerForTests();
        String json = converter.marshal(obj);

        AcmObjectHistory history = new AcmObjectHistory();

        history.setId(9L);
        history.setUserId(userId);
        history.setType(type);
        history.setObjectId(objectId);
        history.setObjectType(objectType);
        history.setObjectString(json);
        history.setDate(date);

        Capture<AcmObjectHistory> found = EasyMock.newCapture();

        expect(mockObjectHistoryDao.save(capture(found))).andReturn(history);
        mockAcmObjectHistoryEventPublisher.publishCreatedEvent(history, ipAddress);
        expectLastCall().anyTimes();

        replayAll();

        AcmObjectHistory saved = acmObjectHistoryService.save(userId, type, obj, objectId, objectType, date, ipAddress, true);

        assertEquals(found.getValue().getObjectId(), saved.getObjectId());
    }

}
