package gov.foia.pipeline.presave;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.armedia.acm.services.holiday.service.HolidayConfigurationService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZoneId;
import java.util.Date;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIARequest;
import gov.foia.model.FoiaConfig;
import gov.foia.service.QueuesTimeToCompleteService;

public class FOIAExtensionHandler implements PipelineHandler<FOIARequest, CaseFilePipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private FOIARequestDao foiaRequestDao;
    private HolidayConfigurationService holidayConfigurationService;
    private QueuesTimeToCompleteService queuesTimeToCompleteService;
    private ArkPermissionEvaluator arkPermissionEvaluator;
    private FoiaConfig foiaConfig;

    @Override
    public void execute(FOIARequest entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("FOIARequest extension pre save handler called for RequestId={}", entity.getId());

        if (foiaConfig.getRequestExtensionWorkingDaysEnabled())
        {
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
                                "The user {" + pipelineContext.getAuthentication().getName()
                                        + "} is not allowed to extend request due date!");
                    }

                    if (FOIAConstants.EXPEDITE_REQUEST_TRACK.equalsIgnoreCase(originalRequest.getRequestTrack()) && originalRequest.getPerfectedDate() != null)
                    {
                        // We need to calculate the extended due date starting from the total time to complete due date
                        // from before the request was expedited
                        Date baseTTCDueDate = getQueuesTimeToCompleteService().addWorkingDaysToDate(
                                Date.from(originalRequest.getPerfectedDate().atZone(ZoneId.systemDefault()).toInstant()),
                                originalRequest.getRequestType());
                        entity.setDueDate(getHolidayConfigurationService().addWorkingDaysToDate(baseTTCDueDate,
                                foiaConfig.getRequestExtensionWorkingDays()));
                    } else
                    {
                        entity.setDueDate(
                                getHolidayConfigurationService().addWorkingDaysToDateWithBusinessHours(originalRequest.getDueDate(),
                                foiaConfig.getRequestExtensionWorkingDays()));
                    }

                    // we set this property, so we can send a correspondence email to the requester in the postsave
                    // FOIAExtensionEmailHandler
                    pipelineContext.addProperty(FOIAConstants.FOIA_PIPELINE_EXTENSION_PROPERTY_KEY, Boolean.TRUE);
                }
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

    public QueuesTimeToCompleteService getQueuesTimeToCompleteService()
    {
        return queuesTimeToCompleteService;
    }

    public void setQueuesTimeToCompleteService(QueuesTimeToCompleteService queuesTimeToCompleteService)
    {
        this.queuesTimeToCompleteService = queuesTimeToCompleteService;
    }

    public ArkPermissionEvaluator getArkPermissionEvaluator()
    {
        return arkPermissionEvaluator;
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }

    public FoiaConfig getFoiaConfig()
    {
        return foiaConfig;
    }

    public void setFoiaConfig(FoiaConfig foiaConfig)
    {
        this.foiaConfig = foiaConfig;
    }
}
