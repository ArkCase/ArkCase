package com.armedia.acm.plugins.casefile.pipeline.postsave;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.holiday.service.HolidayConfigurationService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Optional;

public class CaseFileDueDateHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private HolidayConfigurationService holidayConfigurationService;

    @Override
    public void execute(CaseFile caseFile, CaseFilePipelineContext ctx) throws PipelineProcessException
    {
        log.debug("Entering CaseFile Due Date pipeline handler for object: [{}]", caseFile);

        if (caseFile.getId() != null && ctx.isNewCase())
        {
            caseFile.setDueDate(holidayConfigurationService.addWorkingDaysToDateAndSetTimeToBusinessHours(
                    Optional.ofNullable(caseFile.getDueDate()).orElse(new Date()),
                    holidayConfigurationService.getBusinessHoursConfig().getDefaultDueDateGap()));

            log.debug("Updated CaseFile DueDate to : [{}]", caseFile.getDueDate());
        }

        log.debug("Exiting CaseFile Due Date pipeline handler for object: [{}]", caseFile);
    }

    @Override
    public void rollback(CaseFile caseFile, CaseFilePipelineContext ctx) throws PipelineProcessException
    {

    }

    public HolidayConfigurationService getHolidayConfigurationService()
    {
        return holidayConfigurationService;
    }

    public void setHolidayConfigurationService(HolidayConfigurationService holidayConfigurationService)
    {
        this.holidayConfigurationService = holidayConfigurationService;
    }
}
