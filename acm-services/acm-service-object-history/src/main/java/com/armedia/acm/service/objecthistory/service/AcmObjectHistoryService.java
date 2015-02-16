/**
 * 
 */
package com.armedia.acm.service.objecthistory.service;

import java.util.Date;

import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;

/**
 * @author riste.tutureski
 *
 */
public interface AcmObjectHistoryService {

	public AcmObjectHistory save(AcmObjectHistory acmObjectHistory, String ipAddress);
	public AcmObjectHistory save(String userId, String type, Object obj, Long objectId, String objectType, Date date, String ipAddress);
	
}
