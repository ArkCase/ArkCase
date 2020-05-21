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

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStateContants;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.service.ChangeConsultationStateService;
import com.armedia.acm.plugins.consultation.utility.ConsultationEventUtility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
@Controller
@RequestMapping({ "/api/v1/plugin/consultation", "/api/latest/plugin/consultation" })
public class ChangeConsultationStatusApiController
{
    private Logger log = LogManager.getLogger(getClass());

    private ChangeConsultationStateService changeConsultationStateService;
    private ConsultationEventUtility consultationEventUtility;
    private ConsultationDao consultationDao;

    @RequestMapping(value = "/change/status/{mode}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> changeConsultationState(
            @PathVariable("mode") String mode, @RequestBody ChangeConsultationStatus form, Authentication auth,
            HttpServletRequest request, HttpSession session) throws AcmAppErrorJsonMsg
    {
        log.info("Changing consultation status with id [{}]...", form.getConsultationId());

        Map<String, String> message = null;

        try
        {
            message = new HashMap<>();
            changeConsultationStateService.save(form, auth, "");

            if (form.isChangeConsultationStatusFlow())
            {
                message.put("info", "The consultation is in approval mode");
            }
            else
            {
                message.put("info", "The consultation status has changed");
                Consultation consultation = getConsultationDao().find(form.getConsultationId());
                // Allow Solr to index Change Consultation data before raising event
                Thread.sleep(3000);
                getConsultationEventUtility().raiseEvent(consultation, consultation.getStatus(), consultation.getModified(),
                        ((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress(), auth.getName(), auth);
            }
        }
        catch (Exception e)
        {
            log.error("Changing consultation status with id [{}] failed", form.getConsultationId(), e);
            if (message != null)
            {
                message.put("info", e.getMessage());
            }
            AcmAppErrorJsonMsg acmAppErrorJsonMsg = new AcmAppErrorJsonMsg("Changing consultation status with id %d failed",
                    ChangeConsultationStateContants.CHANGE_CONSULTATION_STATUS,
                    form.getConsultationId().toString(), e);
            throw acmAppErrorJsonMsg;
        }

        if (message.isEmpty())
        {
            message.put("info", "Changing consultation status with id " + form.getConsultationId() + " failed");
        }
        return message;
    }

    public ChangeConsultationStateService getChangeConsultationStateService() {
        return changeConsultationStateService;
    }

    public void setChangeConsultationStateService(ChangeConsultationStateService changeConsultationStateService) {
        this.changeConsultationStateService = changeConsultationStateService;
    }

    public ConsultationEventUtility getConsultationEventUtility() {
        return consultationEventUtility;
    }

    public void setConsultationEventUtility(ConsultationEventUtility consultationEventUtility) {
        this.consultationEventUtility = consultationEventUtility;
    }

    public ConsultationDao getConsultationDao() {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao) {
        this.consultationDao = consultationDao;
    }
}
