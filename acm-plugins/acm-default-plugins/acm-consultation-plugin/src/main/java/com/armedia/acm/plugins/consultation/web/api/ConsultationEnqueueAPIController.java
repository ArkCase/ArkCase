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

import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationEnqueueResponse;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.consultation.service.EnqueueConsultationFileService;
import com.armedia.acm.plugins.consultation.utility.ConsultationEventUtility;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.Date;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
@Controller
@RequestMapping({ "/api/v1/plugin/consultation", "/api/latest/plugin/consultation" })
public class ConsultationEnqueueAPIController
{

    private EnqueueConsultationFileService enqueueConsultationService;
    private UserTrackerService userTrackerService;
    private ConsultationDao consultationDao;

    private ConsultationEventUtility consultationEventUtility;

    @RequestMapping(value = "/enqueue/{consultationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ConsultationEnqueueResponse enqueue(@PathVariable("consultationId") Long consultationId,
                                               @RequestParam(value = "nextQueue", required = true) String nextQueue,
                                               @RequestParam(value = "nextQueueAction") String nextQueueAction,
                                               HttpSession session, Authentication auth)
    {

        ConsultationPipelineContext context = new ConsultationPipelineContext();
        context.setAuthentication(auth);
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        context.setIpAddress(ipAddress);
        context.setEnqueueName(nextQueue);

        getUserTrackerService().trackUser(ipAddress);

        ConsultationEnqueueResponse response = getEnqueueConsultationService().enqueueConsultation(consultationId, nextQueue, nextQueueAction, context);

        if (response.isSuccess())
        {
            // be sure to send back the updated consultation - the service does not flush the SQL and might not know
            // about any changes made in the Activiti layer
            Consultation updated = getConsultationDao().find(consultationId);
            response.setConsultation(updated);
            consultationEventUtility.raiseEvent(updated, "updated", new Date(), ipAddress, auth.getName(), auth);
        }

        return response;
    }

    public UserTrackerService getUserTrackerService()
    {
        return userTrackerService;
    }

    public void setUserTrackerService(UserTrackerService userTrackerService)
    {
        this.userTrackerService = userTrackerService;
    }

    public EnqueueConsultationFileService getEnqueueConsultationService() {
        return enqueueConsultationService;
    }

    public void setEnqueueConsultationService(EnqueueConsultationFileService enqueueConsultationService) {
        this.enqueueConsultationService = enqueueConsultationService;
    }

    public ConsultationDao getConsultationDao() {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao) {
        this.consultationDao = consultationDao;
    }

    public ConsultationEventUtility getConsultationEventUtility() {
        return consultationEventUtility;
    }

    public void setConsultationEventUtility(ConsultationEventUtility consultationEventUtility) {
        this.consultationEventUtility = consultationEventUtility;
    }
}
