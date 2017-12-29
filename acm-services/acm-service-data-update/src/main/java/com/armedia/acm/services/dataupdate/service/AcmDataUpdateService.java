package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.services.dataupdate.dao.AcmDataUpdateDao;
import com.armedia.acm.services.dataupdate.model.AcmDataUpdateExecutorLog;

import java.time.LocalDate;
import java.util.List;

public class AcmDataUpdateService
{
    public static final String DATA_UPDATE_MODIFIER = "DATA_UPDATE";

    private AcmDataUpdateDao dataUpdateDao;

    public List<AcmDataUpdateExecutorLog> findAll()
    {
        return dataUpdateDao.findAll();
    }

    public AcmDataUpdateExecutorLog save(String executorId)
    {
        AcmDataUpdateExecutorLog dataUpdateLog = new AcmDataUpdateExecutorLog();
        dataUpdateLog.setExecutedOn(LocalDate.now());
        dataUpdateLog.setExecutorId(executorId);
        return dataUpdateDao.save(dataUpdateLog);
    }

    public void setDataUpdateDao(AcmDataUpdateDao dataUpdateDao)
    {
        this.dataUpdateDao = dataUpdateDao;
    }
}
