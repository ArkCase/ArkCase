/**
 * 
 */
package com.armedia.acm.services.timesheet.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;

/**
 * @author riste.tutureski
 *
 */
public class AcmTimesheetDao extends AcmAbstractDao<AcmTimesheet> {

	@Override
	protected Class<AcmTimesheet> getPersistenceClass() 
	{
		return AcmTimesheet.class;
	}

}
