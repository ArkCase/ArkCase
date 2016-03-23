package com.armedia.acm.plugins.objectassociation.service;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.objectassociation.model.AcmChildObjectEntity;

/**
 * 
 * @author vladimir.radeski
 *
 */

public interface ObjectAssociationService
{
    public void addReference(Long id, String number, String type, String title, Long targetId, String targetType);

    public AcmAbstractDao<AcmChildObjectEntity> getDaoForChildObjectEntity(String objectType);

}
