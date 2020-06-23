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

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.service.ConsultationService;
import com.armedia.acm.plugins.consultation.utility.ConsultationEventUtility;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
@Controller
@RequestMapping({ "/api/v1/plugin/consultation", "/api/latest/plugin/consultation" })
public class GetConsultationAPIController
{

    private final Logger log = LogManager.getLogger(getClass());
    private ConsultationService consultationService;
    private ConsultationEventUtility consultationEventUtility;
    private ArkPermissionEvaluator arkPermissionEvaluator;

    @RequestMapping(value = "/bynumber", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Consultation getConsultationByNumber(@RequestParam(value = "consultationNumber", required = true) String consultationNumber,
            Authentication auth)
            throws AcmAccessControlException
    {
        Consultation consultation = getConsultationService().getConsultationByNumber(consultationNumber);
        if (consultation != null && !getArkPermissionEvaluator().hasPermission(auth, consultation.getId(), "CONSULTATION", "read"))
        {
            throw new AcmAccessControlException(Arrays.asList(""),
                    "The user {" + auth.getName() + "} is not allowed to read consultation with id=" + consultation.getId());
        }
        return consultation;
    }

    @RequestMapping(value = "/forUser/{user:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Consultation> listConsultationsForUser(@PathVariable("user") String user, Authentication authentication,
            HttpSession session)
            throws AcmListObjectsFailedException, AcmObjectNotFoundException
    {
        if (log.isInfoEnabled())
        {
            log.info("Finding consultations assigned to the user '" + user + "'");
        }
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        try
        {
            List<Consultation> retval = getConsultationService().getNotClosedConsultationsByUser(user);
            for (Consultation cf : retval)
            {
                getConsultationEventUtility().raiseEvent(cf, "search", new Date(), ipAddress, user, authentication);
            }
            return retval;
        }
        catch (Exception e)
        {
            log.error("List Consultations Failed: " + e.getMessage(), e);
            throw new AcmListObjectsFailedException("consultation", e.getMessage(), e);
        }
    }

    @PreAuthorize("hasPermission(#id, 'CONSULTATION', 'viewConsultationDetailsPage')")
    @RequestMapping(method = RequestMethod.GET, value = "/byId/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @DecoratedAssignedObjectParticipants
    @ResponseBody
    public Consultation findConsultationById(
            @PathVariable(value = "id") Long id,
            Authentication auth) throws AcmObjectNotFoundException
    {
        try
        {
            Consultation retval = getConsultationService().getConsultationByIdWithChangeStatusIncluded(id);

            consultationEventUtility.raiseConsultationViewed(retval, auth);
            return retval;
        }
        catch (PersistenceException e)
        {
            throw new AcmObjectNotFoundException("Consultation", id, e.getMessage(), e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/byTitle/{title}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<List<Consultation>> findConsultationsByTitle(
            @PathVariable(value = "title") String title,
            Authentication auth) throws AcmObjectNotFoundException
    {
        log.info("Trying to fetch Consultations by Title {}", title);
        try
        {
            return new ResponseEntity<>(getConsultationService().getConsultationsByTitle(title), HttpStatus.OK);
        }
        catch (AcmObjectNotFoundException e)
        {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
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

    public ArkPermissionEvaluator getArkPermissionEvaluator()
    {
        return arkPermissionEvaluator;
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }
}
