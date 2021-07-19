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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.service.ConsultationService;
import com.armedia.acm.plugins.consultation.utility.ConsultationEventUtility;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
@Controller
@RequestMapping({ "/api/v1/plugin/consultation", "/api/latest/plugin/consultation" })
public class SaveConsultationAPIController
{
    private final Logger log = LogManager.getLogger(getClass());

    private ConsultationService consultationService;

    private ConsultationEventUtility consultationEventUtility;

    private UserTrackerService userTrackerService;

    @PreAuthorize("#in.id == null or hasPermission(#in.id, 'CONSULTATION', 'saveConsultation')")
    @RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE })
    @ResponseBody
    public Consultation updateConsultation(@RequestBody Consultation in, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException,
            IOException
    {
        return saveConsultation(in, null, session, auth);
    }

    @PreAuthorize("#in.id == null or hasPermission(#in.id, 'CONSULTATION', 'saveConsultation')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Consultation createConsultationMutipart(@RequestPart(name = "consultation") Consultation in,
                                                   MultipartHttpServletRequest request, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException,
            IOException
    {
        Map<String, List<MultipartFile>> attachments = request.getMultiFileMap();
        Map<String, List<MultipartFile>> files = new HashMap<>();

        for (Map.Entry<String, List<MultipartFile>> entry : attachments.entrySet())
        {
            String type = entry.getKey();
            if (!"consultation".equalsIgnoreCase(type))
            {
                final List<MultipartFile> attachmentsList = entry.getValue();

                files.put(type, attachmentsList);
            }
        }
        return saveConsultation(in, files, session, auth);
    }

    private Consultation saveConsultation(Consultation in, Map<String, List<MultipartFile>> filesMap, HttpSession session,
            Authentication auth)
            throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException,
            IOException
    {
        log.trace("Got a consultation: [{}] ; consultation ID: [{}]", in, in.getId());
        String ipAddress = (String) session.getAttribute("acm_ip_address");

        userTrackerService.trackUser(ipAddress);

        try
        {
            boolean isNew = in.getId() == null;

            // explicitly set modifier and modified to trigger transformer to reindex data
            // fixes problem when some child objects are changed (e.g participants) and solr document is not updated
            in.setModifier(AuthenticationUtils.getUsername());
            in.setModified(new Date());

            Consultation saved = getConsultationService().saveConsultation(in, filesMap, auth, ipAddress);

            // since the approver list is not persisted to the database, we want to send them back to the caller...
            // the approver list is only here to send to the Activiti engine. After the workflow is started the
            // approvers are stored in Activiti.
            saved.setApprovers(in.getApprovers());

            if (isNew)
            {
                consultationEventUtility.raiseEvent(saved, "created", new Date(), ipAddress, auth.getName(), auth);
                consultationEventUtility.raiseEvent(saved, saved.getStatus(), new Date(), ipAddress, auth.getName(), auth);
            }
            else
            {
                consultationEventUtility.raiseEvent(saved, "updated", new Date(), ipAddress, auth.getName(), auth);
            }

            return saved;
        }
        catch (PipelineProcessException | PersistenceException e)
        {
            throw new AcmCreateObjectFailedException("Consultation", e.getMessage(), e);
        }
    }

    public ConsultationService getConsultationService()
    {
        return consultationService;
    }

    public void setConsultationService(ConsultationService consultationService)
    {
        this.consultationService = consultationService;
    }

    public ConsultationEventUtility getConsultationEventUtility()
    {
        return consultationEventUtility;
    }

    public void setConsultationEventUtility(ConsultationEventUtility consultationEventUtility)
    {
        this.consultationEventUtility = consultationEventUtility;
    }

    public UserTrackerService getUserTrackerService()
    {
        return userTrackerService;
    }

    public void setUserTrackerService(UserTrackerService userTrackerService)
    {
        this.userTrackerService = userTrackerService;
    }
}
