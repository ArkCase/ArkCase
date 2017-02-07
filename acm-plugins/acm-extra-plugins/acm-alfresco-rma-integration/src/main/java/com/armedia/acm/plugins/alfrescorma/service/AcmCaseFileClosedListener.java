package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class AcmCaseFileClosedListener implements ApplicationListener<CaseEvent>
{

    private transient Logger LOG = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;


    @Override
    public void onApplicationEvent(CaseEvent event)
    {
        boolean checkIntegrationEnabled = getAlfrescoRecordsService().checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY);

        if (!checkIntegrationEnabled)
        {
            return;
        }

        boolean shouldDeclareRecords = shouldDeclareRecords(event);

        if (shouldDeclareRecords)
        {
            CaseFile caseFile = event.getCaseFile();

            if (null != caseFile)
            {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(event.getUserId(), event.getUserId());
                getAlfrescoRecordsService().declareAllContainerFilesAsRecords(auth, caseFile.getContainer(),
                        event.getEventDate(), caseFile.getCaseNumber());

            }
        }
    }

    private boolean shouldDeclareRecords(CaseEvent event)
    {
        return AlfrescoRmaPluginConstants.CASE_CLOSED_EVENT.equals(event.getEventType().toLowerCase());
    }


    public AlfrescoRecordsService getAlfrescoRecordsService()
    {
        return alfrescoRecordsService;
    }

    public void setAlfrescoRecordsService(AlfrescoRecordsService alfrescoRecordsService)
    {
        this.alfrescoRecordsService = alfrescoRecordsService;
    }
}
