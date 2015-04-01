/**
 * 
 */
package com.armedia.acm.objectchangestatus.service;

/**
 * @author riste.tutureski
 *
 */
public interface ChangeObjectStatusService {

	public void change(Long objectId, String objectType, String status);
	
}
