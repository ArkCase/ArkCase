package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.labels.service.TranslationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

import java.util.Date;
import java.util.List;
import java.util.Map;

import gov.privacy.model.SARConstants;
import gov.privacy.model.SubjectAccessRequest;

/**
 * @author sasko.tanaskoski
 *
 */
public class SaveSARService
{

    private final Logger log = LogManager.getLogger(getClass());
    private SARService SARService;
    private CaseFileEventUtility caseFileEventUtility;
    private PersonDao personDao;
    private LookupDao lookupDao;
    private TranslationService translationService;

    public CaseFile saveSAR(CaseFile in, Map<String, List<MultipartFile>> filesMap, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException
    {
        CaseFile oldCaseFile = null;
        if (in.getId() != null)
        {
            oldCaseFile = getSARService().getSARById(in.getId());
        }

        String ipAddress = (String) session.getAttribute("acm_ip_address");
        CaseFile saved = getSARService().saveRequest(in, filesMap, auth, ipAddress);
        raiseCaseEvent(in.getId() == null, saved, null, auth,
                ipAddress, oldCaseFile);
        return saved;
    }

    public CaseFile saveSAR(CaseFile in, Map<String, List<MultipartFile>> filesMap, Authentication auth, String ipAddress)
            throws AcmCreateObjectFailedException
    {
        CaseFile oldCaseFile = null;
        if (in.getId() != null)
        {
            oldCaseFile = getSARService().getSARById(in.getId());
        }

        CaseFile saved = getSARService().saveRequest(in, filesMap, auth, ipAddress);
        raiseCaseEvent(in.getId() == null, saved, null, auth,
                ipAddress, oldCaseFile);
        return saved;
    }

    public CaseFile savePortalRequest(CaseFile in, Map<String, List<MultipartFile>> filesMap, Authentication auth, String ipAddress)
            throws AcmCreateObjectFailedException
    {
        CaseFile saved = getSARService().saveRequest(in, filesMap, auth, ipAddress);
        raiseCaseEvent(in.getId() == null, saved, null, auth, ipAddress, null);
        return saved;
    }

    private void raiseCaseEvent(boolean isNew, CaseFile saved, String dispositionValue, Authentication auth, String ipAddress,
            CaseFile oldCaseFile)
    {
        if (isNew)
        {
            caseFileEventUtility.raiseEvent(saved, "created", new Date(), ipAddress, auth.getName(), auth);
            caseFileEventUtility.raiseEvent(saved, saved.getStatus(), new Date(), ipAddress, auth.getName(), auth);
        }
        else
        {
            caseFileEventUtility.raiseEvent(saved, "updated", new Date(), ipAddress, auth.getName(), auth);
            if (saved instanceof SubjectAccessRequest)
            {
                SubjectAccessRequest request = (SubjectAccessRequest) saved;
                {
                    if (!saved.getQueue().getName().equals(SARConstants.INTAKE_QUEUE)
                            && !request.getQueue().getName().equals(SARConstants.FULFILL_QUEUE))
                    {
                        caseFileEventUtility.raiseCustomEvent(saved, "disposition", dispositionValue + " Removed", new Date(),
                                ipAddress, auth.getName(), auth);
                    }
                }
            }
        }
    }

    /**
     * @return the SARService
     */
    public SARService getSARService()
    {
        return SARService;
    }

    /**
     * @param SARService
     *            the SARService to set
     */
    public void setSARService(SARService SARService)
    {
        this.SARService = SARService;
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

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }

    public TranslationService getTranslationService()
    {
        return translationService;
    }

    public void setTranslationService(TranslationService translationService)
    {
        this.translationService = translationService;
    }
}