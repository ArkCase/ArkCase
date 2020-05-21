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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.consultation.dao.ChangeConsultationStatusDao;
import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.utility.ConsultationEventUtility;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
@Controller
@RequestMapping({ "/api/v1/plugin/consultation", "/api/latest/plugin/consultation" })
public class FindConsultationByIdAPIController
{
    private final Logger log = LogManager.getLogger(getClass());

    private ConsultationDao consultationDao;
    private ChangeConsultationStatusDao changeConsultationStatusDao;
    private ConsultationEventUtility consultationEventUtility;

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
            Consultation retval = getConsultationDao().find(id);
            if (retval == null)
            {
                throw new PersistenceException("No such consultation with id '" + id + "'");
            }

            ChangeConsultationStatus changeConsultationStatus = getChangeConsultationStatusDao().findByConsultationId(retval.getId());
            retval.setChangeConsultationStatus(changeConsultationStatus);

            consultationEventUtility.raiseConsultationViewed(retval, auth);
            return retval;
        }
        catch (PersistenceException e)
        {
            throw new AcmObjectNotFoundException("Consultation", id, e.getMessage(), e);
        }
    }

    public ConsultationDao getConsultationDao() {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao) {
        this.consultationDao = consultationDao;
    }

    public ChangeConsultationStatusDao getChangeConsultationStatusDao() {
        return changeConsultationStatusDao;
    }

    public void setChangeConsultationStatusDao(ChangeConsultationStatusDao changeConsultationStatusDao) {
        this.changeConsultationStatusDao = changeConsultationStatusDao;
    }

    public ConsultationEventUtility getConsultationEventUtility() {
        return consultationEventUtility;
    }

    public void setConsultationEventUtility(ConsultationEventUtility consultationEventUtility) {
        this.consultationEventUtility = consultationEventUtility;
    }
}
