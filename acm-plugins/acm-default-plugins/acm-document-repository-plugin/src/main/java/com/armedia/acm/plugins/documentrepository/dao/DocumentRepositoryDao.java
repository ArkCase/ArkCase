package com.armedia.acm.plugins.documentrepository.dao;

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmNameDao;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepositoryConstants;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class DocumentRepositoryDao extends AcmAbstractDao<DocumentRepository> implements AcmNotificationDao, AcmNameDao
{

    @Override
    protected Class<DocumentRepository> getPersistenceClass()
    {
        return DocumentRepository.class;
    }

    @Override
    public String getSupportedObjectType()
    {
        return DocumentRepositoryConstants.OBJECT_TYPE;
    }

    @Override
    public AcmNotifiableEntity findEntity(Long id)
    {
        return find(id);
    }

    @Override
    public String getSupportedNotifiableObjectType()
    {
        return DocumentRepositoryConstants.OBJECT_TYPE;
    }

    @Override
    public AcmObject findByName(String name)
    {
        return null;
    }
}
