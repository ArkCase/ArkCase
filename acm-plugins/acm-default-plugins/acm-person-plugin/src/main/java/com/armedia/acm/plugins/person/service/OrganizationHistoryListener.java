package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.OrganizationEvent;
import com.armedia.acm.plugins.person.model.PersonOrganizationConstants;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 * Created by nick.ferguson on 6/1/2017.
 */
public class OrganizationHistoryListener implements ApplicationListener<OrganizationEvent>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AcmObjectHistoryService acmObjectHistoryService;
    private List<String> nonHistoryGeneratingEvents;

    @Override
    public void onApplicationEvent(OrganizationEvent event)
    {
        log.debug("Organization event raised. Start adding to Object History...");
        if (event != null)
        {
            if (!getNonHistoryGeneratingEvents().contains(event.getEventType()))
            {
                Organization organization = (Organization) event.getSource();
                getAcmObjectHistoryService().save(event.getUserId(), event.getEventType(), organization, organization.getId(), PersonOrganizationConstants.ORGANIZATION_OBJECT_TYPE, event.getEventDate(), event.getIpAddress());

                log.debug("Organization History added to database.");
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