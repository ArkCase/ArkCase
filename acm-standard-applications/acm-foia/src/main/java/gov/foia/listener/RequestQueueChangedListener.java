package gov.foia.listener;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import org.springframework.context.ApplicationListener;

import com.armedia.acm.plugins.casefile.model.CaseFileModifiedEvent;

import gov.foia.model.FOIARequest;
import gov.foia.model.FoiaConfig;
import gov.foia.service.DeclareRequestAsRecordService;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on April, 2021
 */
public class RequestQueueChangedListener implements ApplicationListener<CaseFileModifiedEvent>
{
    private FoiaConfig foiaConfig;
    private DeclareRequestAsRecordService declareRequestAsRecordService;

    @Override
    public void onApplicationEvent(CaseFileModifiedEvent caseFileModifiedEvent)
    {
        if (isSuccessfulQueueChangeEvent(caseFileModifiedEvent))
        {
            FOIARequest request = (FOIARequest) caseFileModifiedEvent.getSource();
            if (isRequestInReleaseQueue(request) && shouldDeclareRequestAsRecordWithoutDelay())
            {
                declareRequestAsRecordService.declareRecords(request);
            }
        }
    }

    private boolean isSuccessfulQueueChangeEvent(CaseFileModifiedEvent caseFileModifiedEvent)
    {
        return caseFileModifiedEvent != null
                && caseFileModifiedEvent.isSucceeded()
                && caseFileModifiedEvent.getEventType().equals("com.armedia.acm.casefile.queue.changed")
                && caseFileModifiedEvent.getSource() != null;
    }

    private boolean isRequestInReleaseQueue(FOIARequest request)
    {
        return request.getQueue() != null && request.getQueue().getName().equals("Release");
    }

    private boolean shouldDeclareRequestAsRecordWithoutDelay()
    {
        return foiaConfig.getDeclareRequestAsRecordsEnabled() && foiaConfig.getDeclareRequestAsRecordsDaysDelay() == 0;
    }

    public FoiaConfig getFoiaConfig()
    {
        return foiaConfig;
    }

    public void setFoiaConfig(FoiaConfig foiaConfig)
    {
        this.foiaConfig = foiaConfig;
    }

    public DeclareRequestAsRecordService getDeclareRequestAsRecordService()
    {
        return declareRequestAsRecordService;
    }

    public void setDeclareRequestAsRecordService(DeclareRequestAsRecordService declareRequestAsRecordService)
    {
        this.declareRequestAsRecordService = declareRequestAsRecordService;
    }
}
