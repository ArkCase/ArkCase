package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileHistoryListener implements ApplicationListener<CaseEvent>
{

    private static final String OBJECT_TYPE = "CASE_FILE";
    private final Logger LOG = LogManager.getLogger(getClass());
    private AcmObjectHistoryService acmObjectHistoryService;

    private List<String> nonHistoryGeneratingEvents;

    @Override
    public void onApplicationEvent(CaseEvent event)
    {
        LOG.debug("Case File event raised. Start adding it to the object history ...");

        if (event != null)
        {
            if (!getNonHistoryGeneratingEvents().contains(event.getEventType()))
            {
                CaseFile caseFile = (CaseFile) event.getSource();

                getAcmObjectHistoryService().save(event.getUserId(), event.getEventType(), caseFile, caseFile.getId(), OBJECT_TYPE,
                        event.getEventDate(), event.getIpAddress(), event.isSucceeded());
            }
        }
    }

    public AcmObjectHistoryService getAcmObjectHistoryService()
    {
        return acmObjectHistoryService;
    }

    public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService)
    {
        this.acmObjectHistoryService = acmObjectHistoryService;
    }

    public List<String> getNonHistoryGeneratingEvents()
    {
        return nonHistoryGeneratingEvents;
    }

    public void setNonHistoryGeneratingEvents(List<String> nonHistoryGeneratingEvents)
    {
        this.nonHistoryGeneratingEvents = nonHistoryGeneratingEvents;
    }
}
