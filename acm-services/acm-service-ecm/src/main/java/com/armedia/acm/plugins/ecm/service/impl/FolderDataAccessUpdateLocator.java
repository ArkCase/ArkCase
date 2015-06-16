package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.services.dataaccess.service.AcmObjectDataAccessBatchUpdateLocator;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 2/17/15.
 */
public class FolderDataAccessUpdateLocator implements AcmObjectDataAccessBatchUpdateLocator<AcmFolder>
{
    private AcmFolderDao folderDao;

    @Override
    public List<AcmFolder> getObjectsModifiedSince(Date lastUpdate, int start, int pageSize)
    {
        return getFolderDao().findModifiedSince(lastUpdate, start, pageSize);
    }

    @Override
    public void save(AcmFolder assignedObject)
    {
        getFolderDao().save(assignedObject);
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }
}
