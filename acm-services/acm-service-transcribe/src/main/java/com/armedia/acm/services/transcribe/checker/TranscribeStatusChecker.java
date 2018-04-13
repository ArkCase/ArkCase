package com.armedia.acm.services.transcribe.checker;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.exception.SaveTranscribeException;
import com.armedia.acm.services.transcribe.model.*;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;
import com.armedia.acm.services.transcribe.utils.TranscribeUtils;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/08/2018
 */
public class TranscribeStatusChecker implements JavaDelegate
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private ArkCaseTranscribeService arkCaseTranscribeService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception
    {
        LOG.debug("Check the status");

        getAuditPropertyEntityAdapter().setUserId(TranscribeConstants.TRANSCRIBE_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(TranscribeBusinessProcessVariableKey.IDS.toString());
        String action = TranscribeActionType.PROCESSING.toString();
        String previousAction = (String) delegateExecution.getVariable(TranscribeBusinessProcessVariableKey.ACTION.toString());

        if (ids != null && ids.size() > 0 && action.equalsIgnoreCase(previousAction))
        {
            // Because all IDs are follow the same business process (Transcribe objects for these IDS have the same information, just different ids),
            // we need just to take one of them (if there are many), check if we need to proceed, and proceed if yes.
            // After that, the business process will update all IDs

            try
            {
                Transcribe transcribe = getArkCaseTranscribeService().get(ids.get(0));
                if (TranscribeStatusType.PROCESSING.toString().equals(transcribe.getStatus()))
                {
                    TranscribeConfiguration configuration = getArkCaseTranscribeService().getConfiguration();
                    Transcribe providerTranscribe = getArkCaseTranscribeService().getTranscribeServiceFactory().getService(configuration.getProvider()).get(transcribe.getRemoteId());

                    if (providerTranscribe != null && !TranscribeStatusType.PROCESSING.toString().equals(providerTranscribe.getStatus()))
                    {
                        String status = providerTranscribe.getStatus();

                        switch (TranscribeStatusType.valueOf(providerTranscribe.getStatus()))
                        {
                            case PROCESSING:
                                action = TranscribeActionType.PROCESSING.toString();
                                break;
                            case COMPLETED:
                                action = TranscribeActionType.COMPLETED.toString();
                                ids.forEach(id -> {
                                    try
                                    {
                                        Transcribe t = getArkCaseTranscribeService().get(id);
                                        t.setTranscribeItems(TranscribeUtils.clone(providerTranscribe.getTranscribeItems()));
                                        getArkCaseTranscribeService().save(t);
                                    }
                                    catch (GetTranscribeException | SaveTranscribeException e)
                                    {
                                        LOG.warn("Taking items for Transcribe with ID=[{}] failed. REASON=[{}]", id, e.getMessage());
                                    }
                                });
                                break;
                            case FAILED:
                                action = TranscribeActionType.FAILED.toString();
                                break;
                        }

                        delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.STATUS.toString(), status);
                        delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(), action);
                    }
                }
            } catch (GetTranscribeException | GetConfigurationException e)
            {
                LOG.warn("Could not check if Transcribe should be completed. REASON=[{}]", e.getMessage());
            }

            delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(), action);
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
