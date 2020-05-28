package gov.foia.pipeline.postsave;

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

public class FOIARequestCreatedHandler implements PipelineHandler<FOIARequest, CaseFilePipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private HolidayConfigurationService holidayConfigurationService;
    private QueuesTimeToCompleteService queuesTimeToCompleteService;

    @Override
    public void execute(FOIARequest entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("Entering FOIARequest post save pipeline handler for object: [{}]", entity);

        if (entity.getId() != null && pipelineContext.isNewCase())
        {
            LocalDateTime receivedDate = entity.getReceivedDate() != null ? entity.getReceivedDate()
                    : entity.getCreated().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (entity.getRequestType().equals(FOIAConstants.NEW_REQUEST_TYPE)
                    && holidayConfigurationService.getHolidayConfiguration().getIncludeWeekends())
            {
                entity.setPerfectedDate(receivedDate);
            }
            else
            {
                entity.setPerfectedDate(
                        holidayConfigurationService.getNextWorkingDay(receivedDate.toLocalDate()).atTime(receivedDate.toLocalTime()));
            }

            Integer TTC = queuesTimeToCompleteService.getTimeToComplete().getRequest().getTotalTimeToComplete();
            entity.setDueDate(holidayConfigurationService
                    .addWorkingDaysToDate(Date.from(entity.getPerfectedDate().atZone(ZoneId.systemDefault()).toInstant()), TTC - 1));
            entity.setTimeToComplete(TTC);

            log.debug("Updated FOIARequest perfectedDate to : [{}] and DueDate to : [{}]", entity.getPerfectedDate(),
                    entity.getDueDate());
        }

        log.debug("Exiting FOIARequest post save pipeline handler for object: [{}]", entity);
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
