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
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.exception.SaveTranscribeException;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeActionType;
import com.armedia.acm.services.transcribe.model.TranscribeBusinessProcessVariableKey;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeConstants;
import com.armedia.acm.services.transcribe.model.TranscribeStatusType;
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
public class TranscribeCheckStatusDelegate implements JavaDelegate
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
            // Because all IDs are follow the same business process (Transcribe objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many), check if we need to proceed, and proceed if yes.
            // After that, the business process will update all IDs

            try
            {
                Transcribe transcribe = getArkCaseTranscribeService().get(ids.get(0));
                if (TranscribeStatusType.PROCESSING.toString().equals(transcribe.getStatus()))
                {
                    TranscribeConfiguration configuration = getArkCaseTranscribeService().getConfiguration();
                    Transcribe providerTranscribe = getArkCaseTranscribeService().getTranscribeServiceFactory()
                            .getService(configuration.getProvider()).get(transcribe.getRemoteId());

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
            }
            catch (GetTranscribeException | GetConfigurationException e)
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
