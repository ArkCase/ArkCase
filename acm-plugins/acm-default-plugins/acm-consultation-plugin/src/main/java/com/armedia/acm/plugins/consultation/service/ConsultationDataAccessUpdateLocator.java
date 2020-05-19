package com.armedia.acm.plugins.consultation.service;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.services.dataaccess.service.AcmObjectDataAccessBatchUpdateLocator;

import java.util.Date;
import java.util.List;

public class ConsultationDataAccessUpdateLocator implements AcmObjectDataAccessBatchUpdateLocator<Consultation>
{
    private ConsultationDao consultationDao;

    @Override
    public List<Consultation> getObjectsModifiedSince(Date lastUpdate, int start, int pageSize)
    {
        return getConsultationDao().findModifiedSince(lastUpdate, start, pageSize);
    }

    @Override
    public void save(Consultation assignedObject) throws AcmAccessControlException
    {
        getConsultationDao().save(assignedObject);
    }

    public ConsultationDao getConsultationDao() {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao) {
        this.consultationDao = consultationDao;
    }
}
