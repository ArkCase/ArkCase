package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileModifiedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AcmCaseFileStatusChangedListener implements ApplicationListener<CaseFileModifiedEvent>, InitializingBean
{

    private transient Logger LOG = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;
    private List<String> caseClosedStatuses = new ArrayList<>();


    @Override
    public void onApplicationEvent(CaseFileModifiedEvent event)
    {
        boolean checkIntegrationEnabled = getAlfrescoRecordsService().checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY);

        if (!checkIntegrationEnabled)
        {
            return;
        }

        boolean shouldDeclareRecords = shouldDeclareRecords(event);

        if (shouldDeclareRecords)
        {
            CaseFile caseFile = (CaseFile) event.getSource();

            if (null != caseFile)
            {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(event.getUserId(), event.getUserId());
                getAlfrescoRecordsService().declareAllContainerFilesAsRecords(auth, caseFile.getContainer(),
                        event.getEventDate(), caseFile.getCaseNumber());

            }
        }
    }

    private boolean shouldDeclareRecords(CaseFileModifiedEvent event)
    {
        LOG.debug("Event: {}", event.getEventType());
        if (AlfrescoRmaPluginConstants.CASE_STATUS_CHANGED_EVENT.equals(event.getEventType().toLowerCase()))
        {
            CaseFile caseFile = (CaseFile) event.getSource();
            if (caseFile != null)
            {
                LOG.debug("Status: {}; status counts as closed: {}",
                        caseFile.getStatus(), getCaseClosedStatuses().contains(caseFile.getStatus()));
            }
            return caseFile != null && getCaseClosedStatuses().contains(caseFile.getStatus());
        }

        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.debug("Finding case closed statuses");
        String statuses = getAlfrescoRecordsService().getAlfrescoRmaProperties().getProperty("alfresco_rma_case_closed_statuses");
        if (statuses != null)
        {
            LOG.debug("Case closed statuses: {}", statuses);
            List<String> statusList = Arrays.asList(statuses.split(","));
            statusList = statusList.stream().filter(s -> s != null).filter(s -> !s.trim().isEmpty()).map(s -> s.trim()).collect(Collectors.toList());
            setCaseClosedStatuses(statusList);
        }
    }

    public AlfrescoRecordsService getAlfrescoRecordsService()
    {
        return alfrescoRecordsService;
    }

    public void setAlfrescoRecordsService(AlfrescoRecordsService alfrescoRecordsService)
    {
        this.alfrescoRecordsService = alfrescoRecordsService;
    }

    public List<String> getCaseClosedStatuses()
    {
        return caseClosedStatuses;
    }

    public void setCaseClosedStatuses(List<String> caseClosedStatuses)
    {
        this.caseClosedStatuses = caseClosedStatuses;
    }
}
