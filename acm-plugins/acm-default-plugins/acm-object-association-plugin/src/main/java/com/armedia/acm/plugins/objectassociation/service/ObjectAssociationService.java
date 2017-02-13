package com.armedia.acm.plugins.objectassociation.service;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.objectassociation.model.AcmChildObjectEntity;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;

import java.util.List;


public interface ObjectAssociationService
{
    void addReference(Long id, String number, String type, String title, String status, Long parentId, String parentType) throws Exception;

    AcmAbstractDao<AcmChildObjectEntity> getDaoForChildObjectEntity(String objectType);

    ObjectAssociation saveObjectAssociation(ObjectAssociation oa);

    List<ObjectAssociation> findByParentTypeAndId(String type, Long id);
}
