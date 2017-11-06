/**
 * 
 */
package com.armedia.acm.service.objecthistory.service;

import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.service.objecthistory.dao.AcmObjectHistoryDao;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class AcmObjectHistoryServiceImpl implements AcmObjectHistoryService
{

    private AcmObjectHistoryDao acmObjectHistoryDao;
    private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;
    private ObjectConverter objectConverter;

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
        AcmMarshaller converter = getObjectConverter().getJsonMarshaller();
        String json = converter.marshal(obj);

        // Set JSON representation of the Object
        acmObjectHistory.setObjectString(json);

        // Set date
        acmObjectHistory.setDate(date);

        // Save object history
        acmObjectHistory = save(acmObjectHistory, ipAddress);

        return acmObjectHistory;
    }

    @Override
    public AcmObjectHistory getAcmObjectHistory(Long objectId, String objectType)
    {
        return getAcmObjectHistoryDao().safeFindLastInsertedByObjectIdAndObjectType(objectId, objectType);
    }

    public AcmObjectHistoryDao getAcmObjectHistoryDao()
    {
        return acmObjectHistoryDao;
    }

    public void setAcmObjectHistoryDao(AcmObjectHistoryDao acmObjectHistoryDao)
    {
        this.acmObjectHistoryDao = acmObjectHistoryDao;
    }

    public AcmObjectHistoryEventPublisher getAcmObjectHistoryEventPublisher()
    {
        return acmObjectHistoryEventPublisher;
    }

    public void setAcmObjectHistoryEventPublisher(AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher)
    {
        this.acmObjectHistoryEventPublisher = acmObjectHistoryEventPublisher;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }
}
