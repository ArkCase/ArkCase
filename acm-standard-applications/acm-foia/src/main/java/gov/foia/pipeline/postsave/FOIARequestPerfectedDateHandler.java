package gov.foia.pipeline.postsave;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.holiday.service.HolidayConfigurationService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIARequest;
import gov.foia.service.QueuesTimeToCompleteService;

public class FOIARequestPerfectedDateHandler implements PipelineHandler<FOIARequest, CaseFilePipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private HolidayConfigurationService holidayConfigurationService;
    private QueuesTimeToCompleteService queuesTimeToCompleteService;

    @Override
    public void execute(FOIARequest entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("Entering FOIARequest perfected date pipeline handler for object: [{}]", entity);

        if (entity.getId() != null && pipelineContext.isNewCase() && entity.getRequestType().equals(FOIAConstants.NEW_REQUEST_TYPE))
        {
            LocalDateTime receivedDate = entity.getReceivedDate();
            if (holidayConfigurationService.getHolidayConfiguration().getIncludeWeekends())
            {
                entity.setPerfectedDate(receivedDate);
                entity.setRedirectedDate(receivedDate);
            }
            else
            {
                entity.setPerfectedDate(
                        holidayConfigurationService.getNextWorkingDay(receivedDate.toLocalDate()).atTime(receivedDate.toLocalTime()));
                entity.setRedirectedDate(
                        holidayConfigurationService.getNextWorkingDay(receivedDate.toLocalDate()).atTime(receivedDate.toLocalTime()));
            }

            Integer TTC = queuesTimeToCompleteService.getTimeToComplete().getRequest().getTotalTimeToComplete();
            entity.setDueDate(holidayConfigurationService
                    .addWorkingDaysToDate(Date.from(entity.getPerfectedDate().atZone(ZoneId.systemDefault()).toInstant()), TTC));
            entity.setTtcOnLastRedirection(TTC);

            log.debug("Updated FOIARequest perfectedDate to : [{}] and DueDate to : [{}]", entity.getPerfectedDate(),
                    entity.getDueDate());
        }

        log.debug("Exiting FOIARequest perfected date pipeline handler for object: [{}]", entity);
    }

    @Override
    public void rollback(FOIARequest entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
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

    public QueuesTimeToCompleteService getQueuesTimeToCompleteService()
    {
        return queuesTimeToCompleteService;
    }

    public void setQueuesTimeToCompleteService(QueuesTimeToCompleteService queuesTimeToCompleteService)
    {
        this.queuesTimeToCompleteService = queuesTimeToCompleteService;
    }
}
