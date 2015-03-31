/**
 * 
 */
package com.armedia.acm.objectchangestatus.service;

import com.armedia.acm.objectchangestatus.model.AcmObjectStatusEvent;

/**
 * @author riste.tutureski
 *
 */
public interface ChangeObjectStatusService {

	public void change(Long objectId, String objectType, String status, String userId);
	public boolean isRequiredObject(AcmObjectStatusEvent event, String objectType);
	
}
