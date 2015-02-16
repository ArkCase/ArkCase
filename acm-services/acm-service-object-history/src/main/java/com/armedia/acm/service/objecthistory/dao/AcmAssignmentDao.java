/**
 * 
 */
package com.armedia.acm.service.objecthistory.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;

/**
 * @author riste.tutureski
 *
 */
public class AcmAssignmentDao extends AcmAbstractDao<AcmAssignment> {


	@Override
	protected Class<AcmAssignment> getPersistenceClass() 
	{
		return AcmAssignment.class;
	}

}
