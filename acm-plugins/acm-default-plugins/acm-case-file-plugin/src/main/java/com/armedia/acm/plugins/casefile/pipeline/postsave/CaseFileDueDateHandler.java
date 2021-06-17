package com.armedia.acm.plugins.casefile.pipeline.postsave;

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
            caseFile.setDueDate(holidayConfigurationService.addWorkingDaysAndWorkingHoursToDateWithBusinessHours(
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
