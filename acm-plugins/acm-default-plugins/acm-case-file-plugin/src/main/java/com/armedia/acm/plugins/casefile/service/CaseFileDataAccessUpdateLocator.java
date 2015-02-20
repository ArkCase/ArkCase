package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.services.dataaccess.service.AcmObjectDataAccessBatchUpdateLocator;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 2/17/15.
 */
public class CaseFileDataAccessUpdateLocator implements AcmObjectDataAccessBatchUpdateLocator<CaseFile>
{
    private CaseFileDao caseFileDao;

    @Override
    public List<CaseFile> getObjectsModifiedSince(Date lastUpdate, int start, int pageSize)
    {
        return getCaseFileDao().findModifiedSince(lastUpdate, start, pageSize);
    }

    @Override
    public void save(CaseFile assignedObject)
    {
        getCaseFileDao().save(assignedObject);
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}
