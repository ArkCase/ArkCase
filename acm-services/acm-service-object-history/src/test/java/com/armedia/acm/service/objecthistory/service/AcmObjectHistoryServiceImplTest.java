/**
 * 
 */
package com.armedia.acm.service.objecthistory.service;


import java.util.Date;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.capture;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.service.objecthistory.dao.AcmObjectHistoryDao;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;

/**
 * @author riste.tutureski
 *
 */
public class AcmObjectHistoryServiceImplTest extends EasyMockSupport {

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
		
		AcmMarshaller converter = ObjectConverter.createJSONMarshaller();
		String json = converter.marshal(obj);
		
		AcmObjectHistory history = new AcmObjectHistory();
		
		history.setId(9L);
		history.setUserId(userId);
		history.setType(type);
		history.setObjectId(objectId);
		history.setObjectType(objectType);
		history.setObjectString(json);
		history.setDate(date);
		
		Capture<AcmObjectHistory> found = new Capture<AcmObjectHistory>();
		
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
		
		AcmMarshaller converter = ObjectConverter.createJSONMarshaller();
		String json = converter.marshal(obj);
		
		AcmObjectHistory history = new AcmObjectHistory();
		
		history.setId(9L);
		history.setUserId(userId);
		history.setType(type);
		history.setObjectId(objectId);
		history.setObjectType(objectType);
		history.setObjectString(json);
		history.setDate(date);
		
		Capture<AcmObjectHistory> found = new Capture<AcmObjectHistory>();
		
		expect(mockObjectHistoryDao.save(capture(found))).andReturn(history);
		mockAcmObjectHistoryEventPublisher.publishCreatedEvent(history, ipAddress);
		expectLastCall().anyTimes();
		
		replayAll();
		
		AcmObjectHistory saved = acmObjectHistoryService.save(userId, type, obj, objectId, objectType, date, ipAddress);
		
		assertEquals(found.getValue().getObjectId(), saved.getObjectId());
	}

}
