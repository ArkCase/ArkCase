package com.armedia.acm.services.transcribe.delegate;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeBusinessProcessVariableKey;
import com.armedia.acm.services.transcribe.model.TranscribeConstants;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 04/30/2018
 */
public class TranscribeRemoveProcessIdDelegate implements JavaDelegate
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private ArkCaseTranscribeService arkCaseTranscribeService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception
    {
        LOG.debug("Remove Process ID Transcribe information");

        getAuditPropertyEntityAdapter().setUserId(TranscribeConstants.TRANSCRIBE_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(TranscribeBusinessProcessVariableKey.IDS.toString());

        if (ids != null && !ids.isEmpty())
        {
            ids.forEach(id -> {
                try
                {
                    Transcribe transcribe = getArkCaseTranscribeService().get(id);
                    transcribe.setProcessId(null);
                    getArkCaseTranscribeService().save(transcribe);
                }
                catch (Exception e)
                {
                    LOG.error("Could not remove process id from Transcribe. REASON=[{}]", e.getMessage());
                }
            });
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
