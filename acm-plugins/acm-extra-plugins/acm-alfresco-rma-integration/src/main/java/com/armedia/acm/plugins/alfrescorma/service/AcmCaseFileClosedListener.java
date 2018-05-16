package com.armedia.acm.plugins.alfrescorma.service;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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
        boolean checkIntegrationEnabled = getAlfrescoRecordsService()
                .checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY);

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
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(event.getUserId(), event.getUserId());
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
