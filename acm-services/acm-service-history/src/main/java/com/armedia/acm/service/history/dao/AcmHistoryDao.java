/**
 * 
 */
package com.armedia.acm.service.history.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.service.history.model.AcmHistory;

/**
 * @author riste.tutureski
 *
 */
public class AcmHistoryDao extends AcmAbstractDao<AcmHistory> {

	@Override
	protected Class<AcmHistory> getPersistenceClass() {
		return AcmHistory.class;
	}

}
