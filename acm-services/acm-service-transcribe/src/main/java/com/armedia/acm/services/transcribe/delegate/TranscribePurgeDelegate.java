package com.armedia.acm.services.transcribe.delegate;

/*-
 * #%L
 * ACM Service: Transcribe
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeActionType;
import com.armedia.acm.services.transcribe.model.TranscribeBusinessProcessVariableKey;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
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
            // Because all IDs are follow the same business process (Transcribe objects for these IDS have the same
            // information, just different ids),
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
                    boolean purged = getArkCaseTranscribeService().getTranscribeServiceFactory().getService(configuration.getProvider())
                            .purge(transcribe);

                    if (purged)
                    {
                        LOG.debug("Transcribe information for Transcribe with REMOTE_ID=[{}] on provider side are purged.",
                                transcribe.getRemoteId());
                        delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(),
                                TranscribeActionType.PURGE_SUCCESS.toString());
                    }
                    else
                    {
                        LOG.warn("Transcribe information for Transcribe with REMOTE_ID=[{}] on provider side are not purged.",
                                transcribe.getRemoteId());
                        delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(),
                                TranscribeActionType.PURGE_FAILED.toString());
                    }

                    delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.PURGE_ATTEMPTS.toString(), purgeAttempts + 1);
                }
                else
                {
                    LOG.warn("Purging attempts for Transcribe with REMOTE_ID=[{}] exceeded. Terminating purge job.",
                            transcribe.getRemoteId());
                    delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(),
                            TranscribeActionType.PURGE_TERMINATE.toString());
                }
            }
            catch (Exception e)
            {
                LOG.warn("Could not purge Transcribe information on provider side. REASON=[{}]", e.getMessage());
                delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(),
                        TranscribeActionType.PURGE_TERMINATE.toString());
            }
        }
        else
        {
            LOG.warn("Purging job cannot proceed because there is no Transcribe. Terminating purge job.");
            delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(),
                    TranscribeActionType.PURGE_TERMINATE.toString());
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
