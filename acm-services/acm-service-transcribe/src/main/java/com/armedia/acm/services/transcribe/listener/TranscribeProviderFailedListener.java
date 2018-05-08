package com.armedia.acm.services.transcribe.listener;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.transcribe.exception.SaveTranscribeException;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeConstants;
import com.armedia.acm.services.transcribe.model.TranscribeProviderFailedEvent;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 04/02/2018
 */
public class TranscribeProviderFailedListener implements ApplicationListener<TranscribeProviderFailedEvent>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private ArkCaseTranscribeService arkCaseTranscribeService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public void onApplicationEvent(TranscribeProviderFailedEvent event)
    {
        if (event != null && event.isSucceeded())
        {
            getAuditPropertyEntityAdapter().setUserId(TranscribeConstants.TRANSCRIBE_SYSTEM_USER);

            Transcribe transcribe = event.getSource();
            try
            {
                getArkCaseTranscribeService().fail(transcribe.getId());
            }
            catch (SaveTranscribeException e)
            {
                LOG.error("Could not set as failed Transcribe with ID=[{}]. REASON=[{}]", transcribe.getId(), e.getMessage());
            }
        }
    }

    public ArkCaseTranscribeService getArkCaseTranscribeService()
    {
        return arkCaseTranscribeService;
    }

    public void setArkCaseTranscribeService(ArkCaseTranscribeService arkCaseTranscribeService)
    {
        this.arkCaseTranscribeService = arkCaseTranscribeService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
