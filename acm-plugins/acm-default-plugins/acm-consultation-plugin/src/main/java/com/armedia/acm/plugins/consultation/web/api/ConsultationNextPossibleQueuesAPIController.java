package com.armedia.acm.plugins.consultation.web.api;

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

import com.armedia.acm.plugins.businessprocess.model.NextPossibleQueuesModel;
import com.armedia.acm.plugins.businessprocess.service.QueueService;
import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationNextPossibleQueuesResponse;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.consultation.service.ConsultationNextPossibleQueuesBusinessRule;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
@Controller
@RequestMapping({ "/api/v1/plugin/consultation", "/api/latest/plugin/consultation" })
public class ConsultationNextPossibleQueuesAPIController
{

    private QueueService queueService;

    private ConsultationNextPossibleQueuesBusinessRule businessRule;

    private ConsultationDao consultationDao;

    @RequestMapping(value = "/nextPossibleQueues/{consultationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ConsultationNextPossibleQueuesResponse nextPossibleQueues(@PathVariable("consultationId") Long consultationId,
            HttpSession session,
            Authentication auth)
    {

        Consultation consultation = consultationDao.find(consultationId);

        if (consultation == null)
        {
            return new ConsultationNextPossibleQueuesResponse("", "", "", new ArrayList<>());
        }

        ConsultationPipelineContext context = new ConsultationPipelineContext();
        context.setNewConsultation(consultation.getId() == null);
        context.setAuthentication(auth);
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        context.setIpAddress(ipAddress);
        context.setQueueName(consultation.getQueue().getName());

        NextPossibleQueuesModel<Consultation, ConsultationPipelineContext> nextPossibleQueues = queueService.nextPossibleQueues(
                consultation, context,
                businessRule);
        return new ConsultationNextPossibleQueuesResponse(nextPossibleQueues.getDefaultNextQueue(),
                nextPossibleQueues.getDefaultReturnQueue(),
                nextPossibleQueues.getDefaultDenyQueue(), nextPossibleQueues.getNextPossibleQueues());

    }

    public QueueService getQueueService()
    {
        return queueService;
    }

    public void setQueueService(QueueService queueService)
    {
        this.queueService = queueService;
    }

    public ConsultationNextPossibleQueuesBusinessRule getBusinessRule()
    {
        return businessRule;
    }

    public void setBusinessRule(ConsultationNextPossibleQueuesBusinessRule businessRule)
    {
        this.businessRule = businessRule;
    }

    public ConsultationDao getConsultationDao()
    {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao)
    {
        this.consultationDao = consultationDao;
    }
}
