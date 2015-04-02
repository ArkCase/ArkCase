/**
 * 
 */
package com.armedia.acm.data.service;

import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmAbstractDao;

/**
 * @author riste.tutureski
 *
 */
public interface AcmDataService 
{
	public AcmAbstractDao<AcmStatefulEntity> getDaoByObjectType(String objectType);
}
