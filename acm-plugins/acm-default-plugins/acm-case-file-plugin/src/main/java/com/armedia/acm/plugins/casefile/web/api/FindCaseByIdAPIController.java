package com.armedia.acm.plugins.casefile.web.api;

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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.dao.ChangeCaseStatusDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class FindCaseByIdAPIController
{
    private final Logger log = LogManager.getLogger(getClass());

    private CaseFileDao caseFileDao;
    private ChangeCaseStatusDao changeCaseStatusDao;
    private CaseFileEventUtility caseFileEventUtility;

    @PreAuthorize("hasPermission(#id, 'CASE_FILE', 'viewCaseDetailsPage')")
    @RequestMapping(method = RequestMethod.GET, value = "/byId/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @DecoratedAssignedObjectParticipants
    @ResponseBody
    public CaseFile findCaseById(
            @PathVariable(value = "id") Long id,
            Authentication auth) throws AcmObjectNotFoundException
    {
        try
        {
            CaseFile retval = getCaseFileDao().find(id);
            if (retval == null)
            {
                throw new PersistenceException("No such case file with id '" + id + "'");
            }

            ChangeCaseStatus changeCaseStatus = getChangeCaseStatusDao().findByCaseId(retval.getId());
            retval.setChangeCaseStatus(changeCaseStatus);

            caseFileEventUtility.raiseCaseFileViewed(retval, auth);
            return retval;
        }
        catch (PersistenceException e)
        {
            throw new AcmObjectNotFoundException("Case File", id, e.getMessage(), e);
        }
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public ChangeCaseStatusDao getChangeCaseStatusDao()
    {
        return changeCaseStatusDao;
    }

    public void setChangeCaseStatusDao(ChangeCaseStatusDao changeCaseStatusDao)
    {
        this.changeCaseStatusDao = changeCaseStatusDao;
    }

    public CaseFileEventUtility getCaseFileEventUtility()
    {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {
        this.caseFileEventUtility = caseFileEventUtility;
    }
}
