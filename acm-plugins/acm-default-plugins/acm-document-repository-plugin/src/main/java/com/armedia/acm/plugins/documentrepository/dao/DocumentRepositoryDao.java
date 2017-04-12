package com.armedia.acm.plugins.documentrepository.dao;

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepositoryConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;


@Transactional
public class DocumentRepositoryDao extends AcmAbstractDao<DocumentRepository> implements AcmNotificationDao
{
    final private Logger log = LoggerFactory.getLogger(getClass());

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

    public DocumentRepository findByName(String name)
    {
        String queryString = "SELECT repo FROM  DocumentRepository repo WHERE repo.nameUpperCase = :name";
        TypedQuery<DocumentRepository> query = getEm().createQuery(queryString, DocumentRepository.class);
        query.setParameter("name", name.toUpperCase());

        try
        {
            return query.getSingleResult();
        } catch (NoResultException e)
        {
            log.debug("Document Repository with name: [{}] not found.", name);
            return null;
        }
    }
}
