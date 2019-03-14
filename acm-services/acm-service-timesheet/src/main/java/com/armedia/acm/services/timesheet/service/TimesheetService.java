package com.armedia.acm.services.timesheet.service;

/*-
 * #%L
 * ACM Service: Timesheet
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

import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;

import com.armedia.acm.services.timesheet.model.TimesheetConfig;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface TimesheetService
{

    TimesheetConfig getConfiguration();

    @Transactional
    AcmTimesheet save(AcmTimesheet timesheet) throws PipelineProcessException;

    @Transactional
    AcmTimesheet save(AcmTimesheet timesheet, Authentication authentication, String submissionName) throws PipelineProcessException;

    @Transactional
    AcmTimesheet save(AcmTimesheet timesheet, String submissionName) throws PipelineProcessException;

    AcmTimesheet get(Long id);

    List<AcmTimesheet> getByObjectIdAndType(Long objectId, String objectType, int startRow, int maxRows, String sortParams);

    String getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams,
            String searchQuery, String userId);

    String getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams,
            String userId);

    boolean checkWorkflowStartup(String type);

    String createName(AcmTimesheet timesheet);

    Map<String, AcmTime> accumulateTimesheetByTypeAndChangeCode(AcmTimesheet timesheet);
}
