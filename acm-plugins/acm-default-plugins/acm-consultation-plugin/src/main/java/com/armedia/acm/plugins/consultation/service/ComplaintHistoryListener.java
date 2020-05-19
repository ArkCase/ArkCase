package com.armedia.acm.plugins.consultation.service;

import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
import com.armedia.acm.plugins.consultation.model.ConsultationEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.util.List;

public class ComplaintHistoryListener implements ApplicationListener<ConsultationEvent>
{
    private final Logger LOG = LogManager.getLogger(getClass());
    private AcmObjectHistoryService acmObjectHistoryService;

    private List<String> nonHistoryGeneratingEvents;

    @Override
    public void onApplicationEvent(ConsultationEvent event)
    {
        LOG.debug("Consultation event raised. Start adding it to the object history ...");

        if (event != null)
        {
            if (!getNonHistoryGeneratingEvents().contains(event.getEventType()))
            {

                Consultation consultation = (Consultation) event.getSource();

                getAcmObjectHistoryService().save(event.getUserId(), event.getEventType(), consultation, consultation.getId(), ConsultationConstants.OBJECT_TYPE,
                        event.getEventDate(), event.getIpAddress());

                LOG.debug("Consultation History added to database.");
            }
        }
    }

    public AcmObjectHistoryService getAcmObjectHistoryService()
    {
        return acmObjectHistoryService;
    }

    public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService)
    {
        this.acmObjectHistoryService = acmObjectHistoryService;
    }

    public List<String> getNonHistoryGeneratingEvents()
    {
        return nonHistoryGeneratingEvents;
    }

    public void setNonHistoryGeneratingEvents(List<String> nonHistoryGeneratingEvents)
    {
        this.nonHistoryGeneratingEvents = nonHistoryGeneratingEvents;
    }
}
