package com.armedia.acm.plugins.documentrepository.dao;

/*-
 * #%L
 * ACM Default Plugin: Document Repository
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepositoryConstants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

@Transactional
public class DocumentRepositoryDao extends AcmAbstractDao<DocumentRepository> implements AcmNotificationDao
{
    final private Logger log = LogManager.getLogger(getClass());

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
        }
        catch (NoResultException e)
        {
            log.debug("Document Repository with name: [{}] not found.", name);
            return null;
        }
    }

    @Transactional
    public void deleteDocumentRepository(Long id)
    {
        DocumentRepository documentRepository = getEm().find(getPersistenceClass(), id);
        getEm().remove(documentRepository);
    }
}
