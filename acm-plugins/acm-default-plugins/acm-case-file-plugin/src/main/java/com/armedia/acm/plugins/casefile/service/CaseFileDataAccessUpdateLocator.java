package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.dataaccess.service.AcmObjectDataAccessBatchUpdateLocator;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 2/17/15.
 */
public class CaseFileDataAccessUpdateLocator implements AcmObjectDataAccessBatchUpdateLocator<CaseFile>
{
    private CaseFileDao caseFileDao;
    private EcmFileParticipantService fileParticipantService;

    @Override
    public List<CaseFile> getObjectsModifiedSince(Date lastUpdate, int start, int pageSize)
    {
        return getCaseFileDao().findModifiedSince(lastUpdate, start, pageSize);
    }

    @Override
    public void save(CaseFile assignedObject) throws AcmAccessControlException
    {
        CaseFile originalCaseFile = caseFileDao.find(assignedObject.getId());
        getCaseFileDao().save(assignedObject);
        getFileParticipantService().inheritParticipantsFromAssignedObject(assignedObject.getParticipants(),
                originalCaseFile.getParticipants(), assignedObject.getContainer().getFolder());
        getFileParticipantService().inheritParticipantsFromAssignedObject(assignedObject.getParticipants(),
                originalCaseFile.getParticipants(), assignedObject.getContainer().getAttachmentFolder());
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }
}
