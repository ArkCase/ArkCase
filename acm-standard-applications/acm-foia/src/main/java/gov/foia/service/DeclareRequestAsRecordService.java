package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.auth.AcmAuthenticationMapper;
import com.armedia.acm.plugins.alfrescorma.service.AlfrescoRecordsService;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.impl.ActivitiTaskDao;
import com.armedia.acm.web.api.MDCConstants;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;
import gov.foia.model.FoiaConfig;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on April, 2021
 */
public class DeclareRequestAsRecordService
{
    private final Logger log = LogManager.getLogger(getClass());

    private AlfrescoRecordsService alfrescoRecordsService;
    private AcmAuthenticationMapper authenticationMapper;
    private FOIARequestDao requestDao;
    private ActivitiTaskDao activitiTaskDao;
    private FoiaConfig foiaConfig;

    public void executeJob()
    {
        if (foiaConfig.getDeclareRequestAsRecordsEnabled())
        {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime declareRequestAsRecordsDate = today.minusDays(foiaConfig.getDeclareRequestAsRecordsDaysDelay());
            List<FOIARequest> allReleasedRequestsAfterDate = getRequestDao()
                    .findAllReleasedNonRecordRequestsBeforeDate(declareRequestAsRecordsDate);

            for (FOIARequest request : allReleasedRequestsAfterDate)
            {
                declareRecords(request);
            }
        }
    }

    public void declareRecordsIfEnabled(Long requestId)
    {
        FOIARequest request = requestDao.find(requestId);

        if (request != null && shouldDeclareRequestAsRecordWithoutDelay())
        {
            declareRecords(request);
        }
    }

    private boolean shouldDeclareRequestAsRecordWithoutDelay()
    {
        return foiaConfig.getDeclareRequestAsRecordsEnabled() && foiaConfig.getDeclareRequestAsRecordsDaysDelay() == 0;
    }

    public void declareRecords(FOIARequest request)
    {
        if (isRmaEnabled())
        {
            Authentication auth = getAuthentication();

            log.info("Declaring all files as records for request [{}]", request.getId());
            getAlfrescoRecordsService().declareAllContainerFilesAsRecords(auth, request.getContainer(),
                    new Date(), request.getCaseNumber());

            List<Long> tasksIds = activitiTaskDao.findTasksIdsForParentObjectIdAndParentObjectType(request.getObjectType(),
                    request.getId());

            log.info("Declaring all associated task files as records for request [{}]", request.getId());

            for (Long taskId : tasksIds)
            {
                AcmTask acmTask = getActivitiTaskDao().find(taskId);
                AcmContainer taskContainer = acmTask.getContainer();

                getAlfrescoRecordsService().declareAllContainerFilesAsRecords(auth, taskContainer,
                        new Date(), String.valueOf(acmTask.getTaskId()));
            }
            request.setDeclaredAsRecord(true);
            getRequestDao().save(request);
        }
    }

    private Authentication getAuthentication()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null)
        {
            String principal = MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY);
            try
            {
                auth = authenticationMapper
                        .getAcmAuthentication(new UsernamePasswordAuthenticationToken(principal, principal));
            }
            catch (AuthenticationServiceException e)
            {
                auth = new AcmAuthentication(Collections.emptySet(), principal, "",
                        true, principal);
            }
        }
        return auth;
    }

    private boolean isRmaEnabled()
    {
        return alfrescoRecordsService.getRmaConfig().getIntegrationEnabled()
                && alfrescoRecordsService.getRmaConfig().getDeclareRecordsOnCaseClose();
    }

    public AlfrescoRecordsService getAlfrescoRecordsService()
    {
        return alfrescoRecordsService;
    }

    public void setAlfrescoRecordsService(AlfrescoRecordsService alfrescoRecordsService)
    {
        this.alfrescoRecordsService = alfrescoRecordsService;
    }

    public AcmAuthenticationMapper getAuthenticationMapper()
    {
        return authenticationMapper;
    }

    public void setAuthenticationMapper(AcmAuthenticationMapper authenticationMapper)
    {
        this.authenticationMapper = authenticationMapper;
    }

    public FOIARequestDao getRequestDao()
    {
        return requestDao;
    }

    public void setRequestDao(FOIARequestDao requestDao)
    {
        this.requestDao = requestDao;
    }

    public ActivitiTaskDao getActivitiTaskDao()
    {
        return activitiTaskDao;
    }

    public void setActivitiTaskDao(ActivitiTaskDao activitiTaskDao)
    {
        this.activitiTaskDao = activitiTaskDao;
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
