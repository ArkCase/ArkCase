/**
 * 
 */
package com.armedia.acm.services.costsheet.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.costsheet.model.AcmCost;

/**
 * @author riste.tutureski
 *
 */
public class AcmCostDao extends AcmAbstractDao<AcmCost> {

	@Override
	protected Class<AcmCost> getPersistenceClass() 
	{
		return AcmCost.class;
	}

}
