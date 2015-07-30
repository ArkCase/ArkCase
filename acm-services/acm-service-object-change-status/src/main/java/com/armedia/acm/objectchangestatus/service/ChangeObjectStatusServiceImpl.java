/**
 * 
 */
package com.armedia.acm.objectchangestatus.service;

import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataService;

/**
 * @author riste.tutureski
 *
 */
public class ChangeObjectStatusServiceImpl implements ChangeObjectStatusService {

	private AcmDataService acmDataService;
	
	@Override
	public void change(Long objectId, String objectType, String status) 
	{
		AcmAbstractDao<AcmStatefulEntity> dao = getAcmDataService().getDaoByObjectType(objectType);
		
		if (dao != null)
		{
			AcmStatefulEntity entity = dao.find(objectId);
			
			if (entity != null)
			{
				entity.setStatus(status);
				dao.save(entity);
			}
		}
	}

	public AcmDataService getAcmDataService() {
		return acmDataService;
	}

	public void setAcmDataService(AcmDataService acmDataService) {
		this.acmDataService = acmDataService;
	}
}
