/**
 * 
 */
package com.armedia.acm.services.costsheet.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;

/**
 * @author riste.tutureski
 *
 */
public class AcmCostsheetDao extends AcmAbstractDao<AcmCostsheet> {

	@Override
	protected Class<AcmCostsheet> getPersistenceClass() 
	{
		return AcmCostsheet.class;
	}

}
