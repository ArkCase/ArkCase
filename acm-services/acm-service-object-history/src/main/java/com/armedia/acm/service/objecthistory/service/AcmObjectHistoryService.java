/**
 * 
 */
package com.armedia.acm.service.objecthistory.service;

import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public interface AcmObjectHistoryService
{

    public AcmObjectHistory save(AcmObjectHistory acmObjectHistory, String ipAddress);

    public AcmObjectHistory save(String userId, String type, Object obj, Long objectId, String objectType, Date date, String ipAddress);

    public AcmObjectHistory getAcmObjectHistory(Long objectId, String objectType);
}
