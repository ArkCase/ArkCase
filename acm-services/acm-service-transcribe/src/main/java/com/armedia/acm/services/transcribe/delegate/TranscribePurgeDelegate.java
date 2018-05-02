package com.armedia.acm.services.transcribe.delegate;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.model.*;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 04/30/2018
 */
public class TranscribePurgeDelegate implements JavaDelegate
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private ArkCaseTranscribeService arkCaseTranscribeService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception
    {
        LOG.debug("Purge Transcribe information");

        getAuditPropertyEntityAdapter().setUserId(TranscribeConstants.TRANSCRIBE_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(TranscribeBusinessProcessVariableKey.IDS.toString());

        if (ids != null && !ids.isEmpty())
        {
            // Because all IDs are follow the same business process (Transcribe objects for these IDS have the same information, just different ids),
            // we need just to take one of them (if there are many)
            try
            {
                Transcribe transcribe = getArkCaseTranscribeService().get(ids.get(0));
                TranscribeConfiguration configuration = getArkCaseTranscribeService().getConfiguration();
                int purgeAttempts = 0;
                int purgeAttemptsInConfiguration = configuration.getProviderPurgeAttempts();
                if (delegateExecution.hasVariable(TranscribeBusinessProcessVariableKey.PURGE_ATTEMPTS.toString()))
                {
                    purgeAttempts = (int) delegateExecution.getVariable(TranscribeBusinessProcessVariableKey.PURGE_ATTEMPTS.toString());
                }

                if (purgeAttempts < purgeAttemptsInConfiguration)
                {
                    boolean purged = getArkCaseTranscribeService().getTranscribeServiceFactory().getService(configuration.getProvider()).purge(transcribe);

                    if (purged)
                    {
                        LOG.debug("Transcribe information for Transcribe with REMOTE_ID=[{}] on provider side are purged.", transcribe.getRemoteId());
                        delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(), TranscribeActionType.PURGE_SUCCESS.toString());
                    }
                    else
                    {
                        LOG.warn("Transcribe information for Transcribe with REMOTE_ID=[{}] on provider side are not purged.", transcribe.getRemoteId());
                        delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(), TranscribeActionType.PURGE_FAILED.toString());
                    }

                    delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.PURGE_ATTEMPTS.toString(), purgeAttempts + 1);
                }
                else
                {
                    LOG.warn("Purging attempts for Transcribe with REMOTE_ID=[{}] exceeded. Terminating purge job.", transcribe.getRemoteId());
                    delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(), TranscribeActionType.PURGE_TERMINATE.toString());
                }
            }catch (Exception e)
            {
                LOG.warn("Could not purge Transcribe information on provider side. REASON=[{}]", e.getMessage());
                delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(), TranscribeActionType.PURGE_TERMINATE.toString());
            }
        }
        else
        {
            LOG.warn("Purging job cannot proceed because there is no Transcribe. Terminating purge job.");
            delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(), TranscribeActionType.PURGE_TERMINATE.toString());
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
