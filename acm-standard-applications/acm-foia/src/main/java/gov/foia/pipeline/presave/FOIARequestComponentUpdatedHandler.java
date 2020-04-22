package gov.foia.pipeline.presave;

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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIARequest;
import gov.foia.model.FoiaConfig;
import gov.foia.model.event.RequestComponentAgencyChangedEvent;
import gov.foia.service.QueuesTimeToCompleteService;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Apr, 2020
 */
public class FOIARequestComponentUpdatedHandler
        implements ApplicationEventPublisherAware, PipelineHandler<FOIARequest, CaseFilePipelineContext>
{

    private transient final Logger log = LogManager.getLogger(getClass());

    private FOIARequestDao foiaRequestDao;
    private HolidayConfigurationService holidayConfigurationService;
    private FoiaConfig foiaConfig;
    private QueuesTimeToCompleteService queuesTimeToCompleteService;
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void execute(FOIARequest entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("FOIARequestComponentUpdated pre save handler called for RequestId={}", entity.getId());

        if (entity.getId() != null && entity.getRequestType().equals(FOIAConstants.NEW_REQUEST_TYPE)
                && !pipelineContext.isNewCase() && !entity.getStatus().equalsIgnoreCase("Closed"))
        {
            FOIARequest originalRequest = getFoiaRequestDao().find(entity.getId());
            if (!originalRequest.getComponentAgency().equals(entity.getComponentAgency()))
            {
                if (holidayConfigurationService.getHolidayConfiguration().getIncludeWeekends())
                {
                    entity.setPerfectedDate(LocalDateTime.now());
                }
                else
                {
                    entity.setPerfectedDate(holidayConfigurationService.getNextWorkingDay(LocalDate.now()).atTime(LocalTime.now()));
                }

                if (getFoiaConfig().getRedirectFunctionalityCalculationEnabled())
                {
                    LocalDate originalPerfectedDate = originalRequest.getPerfectedDate().toLocalDate();

                    Integer TTC = queuesTimeToCompleteService.getTimeToComplete().getRequest().getTotalTimeToComplete();
                    Integer elapsedDays = holidayConfigurationService.countWorkingDates(originalPerfectedDate, LocalDate.now());
                    Integer elapsedTTC = originalRequest.getTimeToComplete() - elapsedDays;

                    Integer calculatedTTC = elapsedTTC + TTC / 2;
                    calculatedTTC = calculatedTTC > TTC ? TTC : calculatedTTC;
                    entity.setDueDate(holidayConfigurationService.addWorkingDaysToDate(
                            Date.from(entity.getPerfectedDate().toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            calculatedTTC));

                    entity.setTimeToComplete(elapsedTTC);

                    sendAuditEvents(entity, originalRequest, elapsedTTC, calculatedTTC);
                }
            }
        }
        log.debug("FOIARequestComponentUpdated pre save handler ended for RequestId={}", entity.getId());
    }

    private void sendAuditEvents(FOIARequest entity, FOIARequest originalRequest, int elapsedTTC, int calculatedTTC)
    {
        String redirectComponent = String.format("from %S to %S", originalRequest.getComponentAgency(),
                entity.getComponentAgency());
        RequestComponentAgencyChangedEvent event = new RequestComponentAgencyChangedEvent(entity, originalRequest,
                redirectComponent);
        applicationEventPublisher.publishEvent(event);

        if (calculatedTTC != elapsedTTC)
        {
            String updatedTTC = String.format("which updates the Time to Complete from %s days to %s days",
                    elapsedTTC, calculatedTTC);
            event = new RequestComponentAgencyChangedEvent(entity, originalRequest, updatedTTC);
            applicationEventPublisher.publishEvent(event);
        }

        if (!originalRequest.getDueDate().equals(entity.getDueDate()))
        {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM-dd-yyyy");
            String updatedDueDate = String.format("which updates the Due Date from %s to %s",
                    dateFormatter.format(originalRequest.getDueDate()),
                    dateFormatter.format(entity.getDueDate()));
            event = new RequestComponentAgencyChangedEvent(entity, originalRequest, updatedDueDate);
            applicationEventPublisher.publishEvent(event);
        }
    }

    @Override
    public void rollback(FOIARequest entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {

    }

    public FOIARequestDao getFoiaRequestDao()
    {
        return foiaRequestDao;
    }

    public void setFoiaRequestDao(FOIARequestDao foiaRequestDao)
    {
        this.foiaRequestDao = foiaRequestDao;
    }

    public HolidayConfigurationService getHolidayConfigurationService()
    {
        return holidayConfigurationService;
    }

    public void setHolidayConfigurationService(HolidayConfigurationService holidayConfigurationService)
    {
        this.holidayConfigurationService = holidayConfigurationService;
    }

    public FoiaConfig getFoiaConfig()
    {
        return foiaConfig;
    }

    public void setFoiaConfig(FoiaConfig foiaConfig)
    {
        this.foiaConfig = foiaConfig;
    }

    public QueuesTimeToCompleteService getQueuesTimeToCompleteService()
    {
        return queuesTimeToCompleteService;
    }

    public void setQueuesTimeToCompleteService(QueuesTimeToCompleteService queuesTimeToCompleteService)
    {
        this.queuesTimeToCompleteService = queuesTimeToCompleteService;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}