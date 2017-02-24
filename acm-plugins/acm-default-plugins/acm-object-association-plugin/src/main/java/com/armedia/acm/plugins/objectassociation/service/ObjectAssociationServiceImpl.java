package com.armedia.acm.plugins.objectassociation.service;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.objectassociation.model.AcmChildObjectEntity;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationConstants;
import com.armedia.acm.spring.SpringContextHolder;

import java.util.List;
import java.util.Map;


public class ObjectAssociationServiceImpl implements ObjectAssociationService
{
    private SpringContextHolder springContextHolder;

    private ObjectAssociationDao objectAssociationDao;

    @Override
    public void addReference(Long id, String number, String type, String title, String status, Long parentId, String parentType) throws Exception
    {
        if (id.equals(parentId) && type.equals(parentType))
        {
            throw new Exception("Cannot reference the object itself.");
        }
        if (findByParentTypeAndId(parentType, parentId).stream().filter(o -> (o.getTargetId().equals(id) && o.getTargetType().equals(type))).findAny().isPresent())
        {
            throw new Exception("Selected object is already referenced.");
        }
        AcmAbstractDao<AcmChildObjectEntity> dao = getDaoForChildObjectEntity(parentType);
        if (dao != null)
        {
            AcmChildObjectEntity entity = dao.find(parentId);
            if (entity != null)
            {
                ObjectAssociation oa = makeObjectAssociation(id, number, type, title, status);
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

    @Override
    public ObjectAssociation saveObjectAssociation(ObjectAssociation oa)
    {
        return getObjectAssociationDao().save(oa);
    }

    @Override
    public List<ObjectAssociation> findByParentTypeAndId(String type, Long id)
    {
        return getObjectAssociationDao().findByParentTypeAndId(type, id);
    }

    private ObjectAssociation makeObjectAssociation(Long id, String number, String type, String title, String status)
    {
        ObjectAssociation oa = new ObjectAssociation();
        oa.setTargetId(id);
        oa.setTargetName(number);
        oa.setTargetType(type);
        oa.setTargetTitle(title);
        oa.setStatus(status);
        oa.setAssociationType(ObjectAssociationConstants.OBJECT_TYPE);
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

    public ObjectAssociationDao getObjectAssociationDao()
    {
        return objectAssociationDao;
    }

    public void setObjectAssociationDao(ObjectAssociationDao objectAssociationDao)
    {
        this.objectAssociationDao = objectAssociationDao;
    }
}
