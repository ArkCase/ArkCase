package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.objectdiff.service.AcmDiffService;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.plugins.person.dao.PersonDao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

import java.util.Date;
import java.util.List;
import java.util.Map;

import gov.foia.model.FOIAPerson;

/**
 * @author sasko.tanaskoski
 *
 */
public class SaveFOIARequestService
{

    private final Logger log = LogManager.getLogger(getClass());
    private FOIARequestService foiaRequestService;
    private CaseFileEventUtility caseFileEventUtility;
    private PersonDao personDao;
    private AcmDiffService acmDiffService;

    public CaseFile saveFOIARequest(CaseFile in, Map<String, List<MultipartFile>> filesMap, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException
    {
        checkPersonAssociation(in);
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        CaseFile saved = getFoiaRequestService().saveRequest(in, filesMap, auth, ipAddress);
        raiseCaseEvent(in.getId() == null, saved, auth, ipAddress);
        return saved;
    }

    private void checkPersonAssociation(CaseFile in)
    {
        if (in.getPersonAssociations().get(0).getPerson().getId() != null)
        {
            FOIAPerson updatedPerson = (FOIAPerson) in.getPersonAssociations().get(0).getPerson();
            FOIAPerson existingPerson = (FOIAPerson) getPersonDao().find(in.getPersonAssociations().get(0).getPerson().getId());

            Boolean isEmailEqual = existingPerson.getDefaultEmail().equals(updatedPerson.getDefaultEmail());

            if (isEmailEqual)
            {
                in.getPersonAssociations().get(0).setPerson(existingPerson);
            }
            else
            {
                updatedPerson.setId(null);
                if (updatedPerson.getDefaultPhone() != null && updatedPerson.getDefaultPhone().getId() != null)
                {
                    updatedPerson.getDefaultPhone().setId(null);
                }
                if (updatedPerson.getDefaultEmail() != null && updatedPerson.getDefaultEmail().getId() != null)
                {
                    updatedPerson.getDefaultEmail().setId(null);
                }
                if (updatedPerson.getDefaultUrl() != null && updatedPerson.getDefaultUrl().getId() != null)
                {
                    updatedPerson.getDefaultUrl().setId(null);
                }

            }
        }
    }

    public CaseFile savePortalRequest(CaseFile in, Map<String, List<MultipartFile>> filesMap, Authentication auth, String ipAddress)
            throws AcmCreateObjectFailedException
    {
        CaseFile saved = getFoiaRequestService().saveRequest(in, filesMap, auth, ipAddress);
        raiseCaseEvent(in.getId() == null, saved, auth, ipAddress);
        return saved;
    }

    private void raiseCaseEvent(boolean isNew, CaseFile saved, Authentication auth, String ipAddress)
    {
        if (isNew)
        {
            caseFileEventUtility.raiseEvent(saved, "created", new Date(), ipAddress, auth.getName(), auth);
            caseFileEventUtility.raiseEvent(saved, saved.getStatus(), new Date(), ipAddress, auth.getName(), auth);
        }
        else
        {
            caseFileEventUtility.raiseEvent(saved, "updated", new Date(), ipAddress, auth.getName(), auth);
        }
    }

    /**
     * @return the foiaRequestService
     */
    public FOIARequestService getFoiaRequestService()
    {
        return foiaRequestService;
    }

    /**
     * @param foiaRequestService
     *            the foiaRequestService to set
     */
    public void setFoiaRequestService(FOIARequestService foiaRequestService)
    {
        this.foiaRequestService = foiaRequestService;
    }

    /**
     * @return the caseFileEventUtility
     */
    public CaseFileEventUtility getCaseFileEventUtility()
    {
        return caseFileEventUtility;
    }

    /**
     * @param caseFileEventUtility
     *            the caseFileEventUtility to set
     */
    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {
        this.caseFileEventUtility = caseFileEventUtility;
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }

    public void setAcmDiffService(AcmDiffService acmDiffService)
    {
        this.acmDiffService = acmDiffService;
    }
}
