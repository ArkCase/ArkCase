/**
 * 
 */
package com.armedia.acm.service.objecthistory.service;

import java.util.Date;

import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.service.objecthistory.dao.AcmObjectHistoryDao;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;

/**
 * @author riste.tutureski
 *
 */
public class AcmObjectHistoryServiceImpl implements AcmObjectHistoryService {

	private AcmObjectHistoryDao acmObjectHistoryDao;
	private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;
	
	@Override
	public AcmObjectHistory save(AcmObjectHistory acmObjectHistory, String ipAddress) 
	{
		// Save Object History to the database
		acmObjectHistory = getAcmObjectHistoryDao().save(acmObjectHistory);
		
		// Raise Object History created event
		getAcmObjectHistoryEventPublisher().publishCreatedEvent(acmObjectHistory, ipAddress);
		
		return acmObjectHistory;
	}
	
	@Override
	public AcmObjectHistory save(String userId, String type, Object obj, Long objectId, String objectType, Date date, String ipAddress) 
	{
		// Create object history
		AcmObjectHistory acmObjectHistory = new AcmObjectHistory();
		
		// Set username from the user who perform the action
		acmObjectHistory.setUserId(userId);
		acmObjectHistory.setType(type);
		
		// Set object id and type
		acmObjectHistory.setObjectId(objectId);
		acmObjectHistory.setObjectType(objectType);
		
		// Convert Object to JSON string
		AcmMarshaller converter = ObjectConverter.createJSONMarshaller();
		String json = converter.marshal(obj);
		
		// Set JSON representation of the Object
		acmObjectHistory.setObjectString(json);
		
		// Set date
		acmObjectHistory.setDate(date);
		
		// Save object history
		acmObjectHistory = save(acmObjectHistory, ipAddress);
		
		return acmObjectHistory;
	}

	public AcmObjectHistoryDao getAcmObjectHistoryDao() {
		return acmObjectHistoryDao;
	}

	public void setAcmObjectHistoryDao(AcmObjectHistoryDao acmObjectHistoryDao) {
		this.acmObjectHistoryDao = acmObjectHistoryDao;
	}

	public AcmObjectHistoryEventPublisher getAcmObjectHistoryEventPublisher() {
		return acmObjectHistoryEventPublisher;
	}

	public void setAcmObjectHistoryEventPublisher(AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher) {
		this.acmObjectHistoryEventPublisher = acmObjectHistoryEventPublisher;
	}

}
