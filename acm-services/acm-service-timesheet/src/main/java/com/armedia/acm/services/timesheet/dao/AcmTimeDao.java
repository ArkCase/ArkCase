/**
 * 
 */
package com.armedia.acm.services.timesheet.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.timesheet.model.AcmTime;

/**
 * @author riste.tutureski
 *
 */
public class AcmTimeDao extends AcmAbstractDao<AcmTime> {

	@Override
	protected Class<AcmTime> getPersistenceClass() 
	{
		return AcmTime.class;
	}

}
