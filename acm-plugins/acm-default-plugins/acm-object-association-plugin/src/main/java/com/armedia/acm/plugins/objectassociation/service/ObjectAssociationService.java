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
    public void addReference(Long id, String number, String type, String title, String status, Long parentId, String parentType);

    public AcmAbstractDao<AcmChildObjectEntity> getDaoForChildObjectEntity(String objectType);

}
