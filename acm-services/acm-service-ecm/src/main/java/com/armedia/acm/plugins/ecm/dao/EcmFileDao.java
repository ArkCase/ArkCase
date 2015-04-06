package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;

/**
 * Created by armdev on 4/22/14.
 */
public class EcmFileDao extends AcmAbstractDao<EcmFile>
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
    @Override
    protected Class<EcmFile> getPersistenceClass()
    {
        return EcmFile.class;
    }

    @Override
    public String getSupportedObjectType()
    {
        return EcmFileConstants.OBJECT_FILE_TYPE;
    }

    public List<EcmFile> findForContainer(Long containerId)
    {
        String jpql = "SELECT e " +
                "FROM EcmFile e " +
                "WHERE e.container.id = :containerId";
        Query query = getEm().createQuery(jpql);
        query.setParameter("containerId", containerId);

        List<EcmFile> results = query.getResultList();

        return results;
    }
    
    public EcmFile findForContainerFolderAndFileType(Long containerId, Long folderId, String fileType)
    {
        String jpql = "SELECT e " +
                "FROM EcmFile e " +
                "WHERE e.container.id = :containerId " +
                "AND e.container.folder.id = :folderId " + 
                "AND e.fileType = :fileType";
        
        Query query = getEm().createQuery(jpql);
        
        query.setParameter("containerId", containerId);
        query.setParameter("folderId", folderId);
        query.setParameter("fileType", fileType);

        EcmFile result = null;
        
        try
        {
        	result = (EcmFile) query.getSingleResult();
        }
        catch(NoResultException e)
        {
        	LOG.error("Cannot find EcmFile for containerId=" + containerId + ", folderId=" + folderId + " and fileType=" + fileType, e);
        }

        return result;
    }

    public EcmFile findByCmisFileId(String cmisFileId) {

        String jpql = "SELECT e FROM EcmFile e WHERE e.versionSeriesId = :cmisFileId";

        TypedQuery<EcmFile> query = getEm().createQuery(jpql, getPersistenceClass());

        query.setParameter("cmisFileId", cmisFileId);

        EcmFile file = query.getSingleResult();

        return file;
    }

    @Transactional
    public void deleteFile(EcmFile file) {
        getEm().remove(file);
    }

}
