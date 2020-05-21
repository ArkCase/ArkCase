package com.armedia.acm.plugins.consultation.service;

/*-
 * #%L
 * ACM Default Plugin: Consultation
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

import com.armedia.acm.plugins.casefile.dao.AcmQueueDao;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.casefile.model.QueuedEvent;
import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.consultation.pipeline.postsave.ConsultationRulesHandler;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.util.Date;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class QueueConsultationServiceImpl implements QueueConsultationService, ApplicationEventPublisherAware
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private PipelineManager<Consultation, ConsultationPipelineContext> queuePipelineManager;
    private ConsultationDao consultationDao;
    private UserTrackerService userTrackerService;
    private AcmQueueDao acmQueueDao;
    private ConsultationRulesHandler rulesHandler;
    private ApplicationEventPublisher applicationPublisher;

    String eventType = "com.armedia.acm.consultation.queued";

    @Override
    @Transactional
    public Consultation enqueue(Long consultationId, String queueName, Authentication auth, String ipAddress) throws PipelineProcessException
    {
        log.debug("Consultation {} is enqueuing to {}", consultationId, queueName);

        // somehow the normal find and save DAO methods aren't working for me here. Changes to Consultation itself
        // don't get persisted. But if I skip detach and add persist and flush, all seems well.
        Consultation consultation = getConsultationDao().getEm().find(Consultation.class, consultationId);
        getConsultationDao().getEm().refresh(consultation);

        ConsultationPipelineContext ctx = new ConsultationPipelineContext();
        if (consultation.getQueue() != null)
        {
            ctx.setQueueName(consultation.getQueue().getName());
        }
        ctx.setEnqueueName(queueName);
        ctx.setAuthentication(auth);
        ctx.setIpAddress(ipAddress);

        return getQueuePipelineManager().executeOperation(consultation, ctx, () -> {
            Consultation merged = getConsultationDao().getEm().merge(consultation);
            getConsultationDao().getEm().persist(merged);

            getConsultationDao().getEm().flush();

            log.debug("Consultation state: {}, queue: {}", merged.getStatus(),
                    merged.getQueue() == null ? "null" : merged.getQueue().getName());

            applicationPublisher.publishEvent(new QueuedEvent(merged, auth.getName(), eventType, new Date()));

            return merged;
        });

    }

    @Override
    public Consultation enqueue(Long consultationId, String queueName) throws PipelineProcessException
    {
        log.debug("Consultation {} is enqueuing to {}", consultationId, queueName);

        Consultation consultation;

        try
        {
            consultation = getConsultationDao().getEm().find(Consultation.class, consultationId);
        }
        catch (EntityNotFoundException e)
        {
            // try and flush our SQL in case we are trying to operate on a brand new object
            getConsultationDao().getEm().flush();
            consultation = getConsultationDao().getEm().find(Consultation.class, consultationId);
        }

        // this version of enqueue is to be called from Activiti processes that do their own orchestration, so
        // we will not execute a pipeline here.
        AcmQueue queue = getAcmQueueDao().findByName(queueName);
        consultation.setPreviousQueue(consultation.getQueue());
        consultation.setQueue(queue);

        consultation = getConsultationDao().save(consultation);

        ConsultationPipelineContext ctx = new ConsultationPipelineContext();
        rulesHandler.execute(consultation, ctx);

        // flush in case another handler needs to see our changes
        getConsultationDao().getEm().flush();

        log.debug("Consultation state: {}, queue: {}", consultation.getStatus(),
                consultation.getQueue() == null ? "null" : consultation.getQueue().getName());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
        {
            String userId = MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY);
            applicationPublisher.publishEvent(new QueuedEvent(consultation, userId, eventType, new Date()));
        }
        else
        {
            applicationPublisher.publishEvent(new QueuedEvent(consultation, auth.getName(), eventType, new Date()));
        }

        return consultation;
    }

    public PipelineManager<Consultation, ConsultationPipelineContext> getQueuePipelineManager() {
        return queuePipelineManager;
    }

    public void setQueuePipelineManager(PipelineManager<Consultation, ConsultationPipelineContext> queuePipelineManager) {
        this.queuePipelineManager = queuePipelineManager;
    }

    public ConsultationDao getConsultationDao() {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao) {
        this.consultationDao = consultationDao;
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

    public ConsultationRulesHandler getRulesHandler() {
        return rulesHandler;
    }

    public void setRulesHandler(ConsultationRulesHandler rulesHandler) {
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
