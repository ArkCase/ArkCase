package com.armedia.acm.plugins.objectassociation.service;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.objectassociation.model.AcmChildObjectEntity;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.spring.SpringContextHolder;

import java.util.Map;

/**
 * 
 * @author vladimir.radeski
 *
 */

public class ObjectAssociationServiceImpl implements ObjectAssociationService
{
    private SpringContextHolder springContextHolder;

    @Override
    public void addReference(Long id, String number, String type, String title, Long targetId, String targetType)
    {
        AcmAbstractDao<AcmChildObjectEntity> dao = getDaoForChildObjectEntity(targetType);
        if (dao != null)
        {
            AcmChildObjectEntity entity = dao.find(targetId);
            if (entity != null)
            {
                ObjectAssociation oa = makeObjectAssociation(targetId, number, targetType, title);
                entity.addChildObject(oa);
                dao.save(entity);
            }
        }
    }

    @Override
    public AcmAbstractDao<AcmChildObjectEntity> getDaoForChildObjectEntity(String objectType)
    {
        if (objectType != null)
        {
            Map<String, AcmAbstractDao> daos = getSpringContextHolder().getAllBeansOfType(AcmAbstractDao.class);

            if (daos != null)
            {
                for (AcmAbstractDao<AcmChildObjectEntity> dao : daos.values())
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

    private ObjectAssociation makeObjectAssociation(Long id, String number, String type, String title)
    {
        ObjectAssociation oa = new ObjectAssociation();

        oa.setTargetId(id);
        oa.setTargetName(number);
        oa.setTargetType(type);
        oa.setTargetTitle(title);
        oa.setAssociationType("REFERENCE");

        return oa;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

}
