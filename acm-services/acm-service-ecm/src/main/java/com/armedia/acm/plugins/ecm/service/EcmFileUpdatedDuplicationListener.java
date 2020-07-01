package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.dao.EcmFileVersionDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.util.List;

public class EcmFileUpdatedDuplicationListener implements ApplicationListener<EcmFileUpdatedEvent>
{

    private final Logger LOG = LogManager.getLogger(getClass());
    private FolderAndFilesUtils folderAndFilesUtils;
    private EcmFileVersionDao ecmFileVersionDao;

    @Override
    public void onApplicationEvent(EcmFileUpdatedEvent event)
    {
        if (event != null && event.isSucceeded())
        {

            EcmFile ecmFile = (EcmFile) event.getSource();
            EcmFileVersion ecmFileVersion = getFolderAndFilesUtils().getVersion(ecmFile, ecmFile.getActiveVersionTag());

            List<EcmFileVersion> efvList = getEcmFileVersionDao().getEcmFileVersionWithSameHash(ecmFileVersion.getFileHash());
            if (efvList.size() > 1)
            {
                for (EcmFileVersion efv : efvList)
                {
                    EcmFile ef = efv.getFile();
                    if (!ef.isLink()) {
                        ef.setDuplicate(true);
                    }
                }
            }
        }
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public EcmFileVersionDao getEcmFileVersionDao()
    {
        return ecmFileVersionDao;
    }

    public void setEcmFileVersionDao(EcmFileVersionDao ecmFileVersionDao)
    {
        this.ecmFileVersionDao = ecmFileVersionDao;
    }
}
