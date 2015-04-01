/**
 * 
 */
package com.armedia.acm.data.service;

import java.util.Map;

import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.spring.SpringContextHolder;

/**
 * @author riste.tutureski
 *
 */
public class AcmDataServiceImpl implements AcmDataService {

	private SpringContextHolder springContextHolder;
	
	@SuppressWarnings("unchecked")
	@Override
	public AcmAbstractDao<AcmStatefulEntity> getDaoByObjectType(String objectType) 
	{
		if (objectType != null)
		{
			Map<String, AcmAbstractDao> daos = getSpringContextHolder().getAllBeansOfType(AcmAbstractDao.class);
			
			if (daos != null)
			{
				for (AcmAbstractDao<AcmStatefulEntity> dao : daos.values())
				{
					if (objectType.equals(dao.getSupportedObjectType()))
					{
						return dao;
					}
				}
			}
		}
		return null;
	}

	public SpringContextHolder getSpringContextHolder() {
		return springContextHolder;
	}

	public void setSpringContextHolder(SpringContextHolder springContextHolder) {
		this.springContextHolder = springContextHolder;
	}
}
