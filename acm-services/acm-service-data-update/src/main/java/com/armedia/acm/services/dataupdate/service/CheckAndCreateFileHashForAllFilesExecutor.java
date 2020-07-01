package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.EcmFileVersionDao;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CheckAndCreateFileHashForAllFilesExecutor implements AcmDataUpdateExecutor
{

    private EcmFileVersionDao ecmFileVersionDao;
    private EcmFileService ecmFileService;

    @Override
    public String getUpdateId()
    {
        return "check_and_create_fileHash_for_all_files";
    }

    @Override
    @Async
    public void execute()
    {

        List<EcmFileVersion> allFilesWithoutHash = getEcmFileVersionDao().getAllEcmFileVersion();

        if (allFilesWithoutHash != null || !allFilesWithoutHash.isEmpty()) {

            for (EcmFileVersion file : allFilesWithoutHash) {
                try {
                    InputStream is = getEcmFileService().downloadAsInputStream(file.getFile());
                    String fileHash = DigestUtils.md5Hex(is);
                    file.setFileHash(fileHash);
                } catch (AcmUserActionFailedException | IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }

    public EcmFileVersionDao getEcmFileVersionDao() {
        return ecmFileVersionDao;
    }

    public void setEcmFileVersionDao(EcmFileVersionDao ecmFileVersionDao) {
        this.ecmFileVersionDao = ecmFileVersionDao;
    }
}
