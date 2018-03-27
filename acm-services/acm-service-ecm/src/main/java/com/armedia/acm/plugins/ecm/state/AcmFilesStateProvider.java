package com.armedia.acm.plugins.ecm.state;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileVersionDao;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;

public class AcmFilesStateProvider implements StateOfModuleProvider
{
    private EcmFileVersionDao ecmFileVersionDao;
    private EcmFileDao ecmFileDao;

    @Override
    public String getModuleName()
    {
        return "acm-files";
    }

    @Override
    public StateOfModule getModuleState()
    {
        AcmFilesState acmFilesState = new AcmFilesState();
        acmFilesState.setNumberOfDocuments(ecmFileDao.getFilesCount());
        acmFilesState.setSizeOfRepository(ecmFileVersionDao.getTotalSizeOfFiles());
        return acmFilesState;
    }

    public void setEcmFileVersionDao(EcmFileVersionDao ecmFileVersionDao)
    {
        this.ecmFileVersionDao = ecmFileVersionDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }
}
