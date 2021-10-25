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

import com.armedia.acm.plugins.casefile.dao.AcmQueueDao;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.QueuedEvent;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.pipeline.postsave.CaseFileRulesHandler;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by armdev on 8/26/15.
 */
public class QueueCaseServiceImpl implements QueueCaseService, ApplicationEventPublisherAware
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private PipelineManager<CaseFile, CaseFilePipelineContext> queuePipelineManager;
    private CaseFileDao caseFileDao;
    private UserTrackerService userTrackerService;
    private AcmQueueDao acmQueueDao;
    private CaseFileRulesHandler rulesHandler;
    private ApplicationEventPublisher applicationPublisher;

    String eventType = "com.armedia.acm.casefile.queued";

    @Override
    @Transactional
    public CaseFile enqueue(Long caseFileId, String queueName, Authentication auth, String ipAddress) throws PipelineProcessException
    {
        log.debug("Case file {} is enqueuing to {}", caseFileId, queueName);

        // somehow the normal find and save DAO methods aren't working for me here. Changes to CaseFile itself
        // don't get persisted. But if I skip detach and add persist and flush, all seems well.
        CaseFile caseFile = getCaseFileDao().getEm().find(CaseFile.class, caseFileId);
        getCaseFileDao().getEm().refresh(caseFile);

        CaseFilePipelineContext ctx = new CaseFilePipelineContext();
        if (caseFile.getQueue() != null)
        {
            ctx.setQueueName(caseFile.getQueue().getName());
        }
        ctx.setEnqueueName(queueName);
        ctx.setAuthentication(auth);
        ctx.setIpAddress(ipAddress);

        return getQueuePipelineManager().executeOperation(caseFile, ctx, () -> {
            CaseFile merged = getCaseFileDao().getEm().merge(caseFile);
            getCaseFileDao().getEm().persist(merged);

            getCaseFileDao().getEm().flush();

            log.debug("Case file state: {}, queue: {}", merged.getStatus(),
                    merged.getQueue() == null ? "null" : merged.getQueue().getName());

            applicationPublisher.publishEvent(new QueuedEvent(merged, auth.getName(), eventType, new Date()));

            return merged;
        });

    }

    @Override
    public CaseFile enqueue(Long caseFileId, String queueName) throws PipelineProcessException
    {
        log.debug("Case file {} is enqueuing to {}", caseFileId, queueName);

        CaseFile caseFile;

        try
        {
            caseFile = getCaseFileDao().getEm().find(CaseFile.class, caseFileId);
        }
        catch (EntityNotFoundException e)
        {
            // try and flush our SQL in case we are trying to operate on a brand new object
            getCaseFileDao().getEm().flush();
            caseFile = getCaseFileDao().getEm().find(CaseFile.class, caseFileId);
        }

        // this version of enqueue is to be called from Activiti processes that do their own orchestration, so
        // we will not execute a pipeline here.
        AcmQueue queue = getAcmQueueDao().findByName(queueName);
        caseFile.setPreviousQueue(caseFile.getQueue());
        caseFile.setQueue(queue);
        caseFile.setQueueEnterDate(LocalDateTime.now());

        caseFile = getCaseFileDao().save(caseFile);

        CaseFilePipelineContext ctx = new CaseFilePipelineContext();
        rulesHandler.execute(caseFile, ctx);

        // flush in case another handler needs to see our changes
        getCaseFileDao().getEm().flush();

        log.debug("Case file state: {}, queue: {}", caseFile.getStatus(),
                caseFile.getQueue() == null ? "null" : caseFile.getQueue().getName());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
        {
            String userId = MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY);
            applicationPublisher.publishEvent(new QueuedEvent(caseFile, userId, eventType, new Date()));
        }
        else
        {
            applicationPublisher.publishEvent(new QueuedEvent(caseFile, auth.getName(), eventType, new Date()));
        }

        return caseFile;
    }

    public PipelineManager<CaseFile, CaseFilePipelineContext> getQueuePipelineManager()
    {
        return queuePipelineManager;
    }

    public void setQueuePipelineManager(PipelineManager<CaseFile, CaseFilePipelineContext> queuePipelineManager)
    {
        this.queuePipelineManager = queuePipelineManager;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public UserTrackerService getUserTrackerService()
    {
        return userTrackerService;
    }

    public void setUserTrackerService(UserTrackerService userTrackerService)
    {
        this.userTrackerService = userTrackerService;
    }

    public AcmQueueDao getAcmQueueDao()
    {
        return acmQueueDao;
    }

    public void setAcmQueueDao(AcmQueueDao acmQueueDao)
    {
        this.acmQueueDao = acmQueueDao;
    }

    public void setRulesHandler(CaseFileRulesHandler rulesHandler)
    {
        this.rulesHandler = rulesHandler;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher(org.springframework.
     * context.ApplicationEventPublisher)
     */

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationPublisher = applicationEventPublisher;
    }

}
