package com.armedia.acm.plugins.consultation.listener;

import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatusConstants;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.task.model.BuckslipProcessStateEvent;

import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.Objects;

public class ChangeConsultationStatusOnBuckslipEventListener implements ApplicationListener<BuckslipProcessStateEvent>
{

    private ConsultationDao consultationDao;

    @Override
    public void onApplicationEvent(BuckslipProcessStateEvent buckslipProcessStateEvent)
    {
        Map<String, Object> processVariables = (Map<String, Object>) buckslipProcessStateEvent.getSource();

        String parentObjectType = (String) processVariables.getOrDefault(CaseFileConstants.PARENT_OBJECT_TYPE, "");
        Long parentObjectId = (Long) processVariables.getOrDefault(CaseFileConstants.PARENT_OBJECT_ID, null);

        if (CaseFileConstants.OBJECT_TYPE.equals(parentObjectType) && Objects.nonNull(parentObjectId))
        {
            Consultation consultation = getConsultationDao().find(parentObjectId);

            switch (buckslipProcessStateEvent.getBuckslipProcessState())
            {
            case INITIALIZED:
                consultation.setStatus(ChangeConsultationStatusConstants.STATUS_IN_APPROVAL);
                break;
            case WITHDRAWN:
                consultation.setStatus(ChangeConsultationStatusConstants.STATUS_DRAFT);
                break;
            case COMPLETED:
                consultation.setStatus(ChangeConsultationStatusConstants.STATUS_APPROVED);
                break;
            }

            getConsultationDao().save(consultation);
        }
    }

    public ConsultationDao getConsultationDao() {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao) {
        this.consultationDao = consultationDao;
    }
}
