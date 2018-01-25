package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileHistoryListener implements ApplicationListener<CaseEvent>
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final String OBJECT_TYPE = "CASE_FILE";

    private AcmObjectHistoryService acmObjectHistoryService;

    private List<String> nonHistoryGeneratingEvents;

    @Override
    public void onApplicationEvent(CaseEvent event)
    {
        LOG.debug("Case File event raised. Start adding it to the object history ...");

        if (event != null)
        {
            if (!getNonHistoryGeneratingEvents().contains(event.getEventType()))
            {

                CaseFile caseFile = (CaseFile) event.getSource();

                getAcmObjectHistoryService().save(event.getUserId(), event.getEventType(), caseFile, caseFile.getId(), OBJECT_TYPE,
                        event.getEventDate(), event.getIpAddress());

                LOG.debug("Case File History added to database.");
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
