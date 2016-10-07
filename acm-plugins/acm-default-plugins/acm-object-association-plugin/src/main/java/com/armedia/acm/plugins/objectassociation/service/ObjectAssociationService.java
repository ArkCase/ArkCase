package com.armedia.acm.plugins.objectassociation.service;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.objectassociation.model.AcmChildObjectEntity;


public interface ObjectAssociationService
{
    void addReference(Long id, String number, String type, String title, String status, Long parentId, String parentType);

    AcmAbstractDao<AcmChildObjectEntity> getDaoForChildObjectEntity(String objectType);

}
