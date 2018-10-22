package gov.foia.pipeline.presave;

import com.armedia.acm.plugins.admin.service.HolidayConfigurationService;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIARequest;

public class FOIAExtensionHandler implements PipelineHandler<FOIARequest, CaseFilePipelineContext>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private FOIARequestDao foiaRequestDao;
    private HolidayConfigurationService holidayConfigurationService;
    private ArkPermissionEvaluator arkPermissionEvaluator;
    private int extensionWorkingDays;

    @Override
    public void execute(FOIARequest entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("FOIARequest extension pre save handler called for RequestId={}", entity.getId());

        if (entity.getId() != null)
        {
            FOIARequest originalRequest = getFoiaRequestDao().find(entity.getId());

            if (originalRequest.getExtensionFlag() && !entity.getExtensionFlag())
            {
                throw new PipelineProcessException("Request extension cannot be removed!");
            }

            if (!originalRequest.getExtensionFlag() && entity.getExtensionFlag())
            {
                if (!getArkPermissionEvaluator().hasPermission(pipelineContext.getAuthentication(), entity.getId(), "CASE_FILE",
                        "requestDueDateExtension"))
                {
                    throw new PipelineProcessException(
                            "The user {" + pipelineContext.getAuthentication().getName() + "} is not allowed to extend request due date!");
                }

                entity.setDueDate(
                        getHolidayConfigurationService().addWorkingDaysToDate(originalRequest.getDueDate(), extensionWorkingDays));

                // we set this property, so we can send a correspondence email to the requester in the postsave
                // FOIAExtensionEmailHandler
                pipelineContext.addProperty(FOIAConstants.FOIA_PIPELINE_EXTENSION_PROPERTY_KEY, Boolean.TRUE);
            }
        }

        log.debug("FOIARequest extension pre save handler ended for RequestId={}", entity.getId());
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

    public int getExtensionWorkingDays()
    {
        return extensionWorkingDays;
    }

    public void setExtensionWorkingDays(int extensionWorkingDays)
    {
        this.extensionWorkingDays = extensionWorkingDays;
    }

    public ArkPermissionEvaluator getArkPermissionEvaluator()
    {
        return arkPermissionEvaluator;
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }
}
