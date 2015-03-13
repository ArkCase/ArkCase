package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.AcmContainerFolder;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;
import java.util.Properties;

/**
 * Created by armdev on 3/11/15.
 */
@Repository
public class AcmContainerFolderDao extends AcmAbstractDao<AcmContainerFolder>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;

    @Override
    protected Class<AcmContainerFolder> getPersistenceClass()
    {
        return AcmContainerFolder.class;
    }

    @Transactional
    public AcmContainerFolder getOrCreateContainerFolder(String objectType, Long objectId) throws AcmCreateObjectFailedException
    {
        log.info("Finding folder for object " + objectType + " id " + objectId);

        String queryText = "SELECT e FROM AcmContainerFolder e WHERE e.containerObjectId = :objectId AND e.containerObjectType = :objectType";

        Query query = getEm().createQuery(queryText, getPersistenceClass());

        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);

        List<AcmContainerFolder> found = query.getResultList();

        if ( !found.isEmpty() )
        {
            log.info("Found existing folder " + found.get(0).getCmisFolderId() + "for object " + objectType + " id " + objectId);
            return found.get(0);
        }

        // objects should really have a folder already.  Since we got here the object does not actually have one.
        // the application doesn't really care where the folder is, so we'll just create a folder in a sensible
        // location.

        // TODO: get from configuration
        Properties p = new Properties();
        p.setProperty("defaultBasePath", "/Sites/acm/documentLibrary");
        p.setProperty("defaultPath.COMPLAINT", "/Complaints");
        p.setProperty("defaultPath.TASK", "/Tasks");
        p.setProperty("defaultPath.CASE_FILE", "/Case Files");

        log.info("Creating new folder for object " + objectType + " id " + objectId);

        String path = p.getProperty("defaultBasePath");
        path += p.getProperty("defaultPath." + objectType);
        path += "/" + objectId;

        String cmisFolderId = getEcmFileService().createFolder(path);

        log.info("Created new folder " + cmisFolderId + "for object " + objectType + " id " + objectId);

        AcmContainerFolder newFolder = new AcmContainerFolder();
        newFolder.setContainerObjectId(objectId);
        newFolder.setContainerObjectType(objectType);
        newFolder.setCmisFolderId(cmisFolderId);

        newFolder = save(newFolder);

        return newFolder;
    }


    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
}
