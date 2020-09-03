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

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
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
import java.util.Objects;

import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIARequest;

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
    private LookupDao lookupDao;
    private TranslationService translationService;
    private FOIAExemptionService foiaExemptionService;

    public CaseFile saveFOIARequest(CaseFile in, Map<String, List<MultipartFile>> filesMap, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException, AcmAppErrorJsonMsg {
        CaseFile oldCaseFile = null;
        if (in.getId() != null)
        {
            oldCaseFile = getFoiaRequestService().getFoiaRequestById(in.getId());
        }

        String ipAddress = (String) session.getAttribute("acm_ip_address");
        CaseFile saved = getFoiaRequestService().saveRequest(in, filesMap, auth, ipAddress);
        raiseCaseEvent(in.getId() == null, saved, in instanceof FOIARequest ? ((FOIARequest) in).getDispositionValue() : null, auth,
                ipAddress, oldCaseFile);
        return saved;
    }

    public CaseFile saveFOIARequest(CaseFile in, Map<String, List<MultipartFile>> filesMap, Authentication auth, String ipAddress)
            throws AcmCreateObjectFailedException, AcmAppErrorJsonMsg {
        CaseFile oldCaseFile = null;
        if (in.getId() != null)
        {
            oldCaseFile = getFoiaRequestService().getFoiaRequestById(in.getId());
        }

        CaseFile saved = getFoiaRequestService().saveRequest(in, filesMap, auth, ipAddress);
        raiseCaseEvent(in.getId() == null, saved, in instanceof FOIARequest ? ((FOIARequest) in).getDispositionValue() : null, auth,
                ipAddress, oldCaseFile);
        return saved;
    }

    public CaseFile savePortalRequest(CaseFile in, Map<String, List<MultipartFile>> filesMap, Authentication auth, String ipAddress)
            throws AcmCreateObjectFailedException, AcmAppErrorJsonMsg {
        CaseFile saved = getFoiaRequestService().saveRequest(in, filesMap, auth, ipAddress);
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
            if (saved instanceof FOIARequest)
            {
                FOIARequest request = (FOIARequest) saved;
                if (request.getRequestType().equals(FOIAConstants.APPEAL_REQUEST_TYPE))
                {
                    if (oldCaseFile != null)
                    {
                        FOIARequest oldRequest = (FOIARequest) oldCaseFile;
                        if (!Objects.equals(request.getDisposition(), oldRequest.getDisposition()))
                        {
                            List<StandardLookupEntry> lookupList = (List<StandardLookupEntry>) getLookupDao()
                                    .getLookupByName("appealDispositionType").getEntries();

                            if (oldRequest.getDisposition() != null && request.getDisposition() == null)
                            {
                                StandardLookupEntry dispositionLookup = lookupList.stream()
                                        .filter(entry -> oldRequest.getDisposition().equalsIgnoreCase(entry.getKey())).findFirst()
                                        .orElse(null);
                                String dispValue = dispositionLookup != null
                                        ? getTranslationService().translate(dispositionLookup.getValue())
                                        : oldRequest.getDisposition();

                                caseFileEventUtility.raiseCustomEvent(saved, "disposition", dispValue + " Removed",
                                        new Date(),
                                        ipAddress, auth.getName(), auth);
                            }
                            else
                            {
                                StandardLookupEntry dispositionLookup = lookupList.stream()
                                        .filter(entry -> request.getDisposition().equalsIgnoreCase(entry.getKey())).findFirst()
                                        .orElse(null);
                                String dispValue = dispositionLookup != null
                                        ? getTranslationService().translate(dispositionLookup.getValue())
                                        : request.getDisposition();

                                caseFileEventUtility.raiseCustomEvent(saved, "disposition", dispValue + " Added",
                                        new Date(),
                                        ipAddress, auth.getName(), auth);
                            }
                        }

                        if (!Objects.equals(request.getOtherReason(), oldRequest.getOtherReason()))
                        {
                            List<StandardLookupEntry> lookupList = (List<StandardLookupEntry>) getLookupDao()
                                    .getLookupByName("appealOtherReasons").getEntries();

                            if (oldRequest.getOtherReason() != null && request.getOtherReason() == null)
                            {
                                StandardLookupEntry otherReasonLookup = lookupList.stream()
                                        .filter(entry -> oldRequest.getOtherReason().equalsIgnoreCase(entry.getKey())).findFirst()
                                        .orElse(null);
                                String otherReasonValue = otherReasonLookup != null
                                        ? getTranslationService().translate(otherReasonLookup.getValue())
                                        : oldRequest.getOtherReason();
                                caseFileEventUtility.raiseCustomEvent(saved, "other.reason", otherReasonValue + " Removed",
                                        new Date(),
                                        ipAddress, auth.getName(), auth);
                            }
                            else
                            {
                                StandardLookupEntry otherReasonLookup = lookupList.stream()
                                        .filter(entry -> request.getOtherReason().equalsIgnoreCase(entry.getKey())).findFirst()
                                        .orElse(null);
                                String otherReasonValue = otherReasonLookup != null
                                        ? getTranslationService().translate(otherReasonLookup.getValue())
                                        : request.getOtherReason();
                                caseFileEventUtility.raiseCustomEvent(saved, "other.reason", otherReasonValue + " Added",
                                        new Date(),
                                        ipAddress, auth.getName(), auth);
                            }
                        }
                    }
                }
                else
                {
                    if ((request.getQueue().getName().equals(FOIAConstants.FULFILL_QUEUE)
                            || request.getQueue().getName().equals(FOIAConstants.INTAKE_QUEUE))
                            && (request.getDisposition() != null && request.getDisposition().equals("grantedInFull")))
                    {
                        boolean hasManuallyAddedExemeptions = false;
                        try
                        {
                            hasManuallyAddedExemeptions = getFoiaExemptionService()
                                    .getExemptionCodes(request.getId(), request.getObjectType()).size() > 0 ? true : false;

                        }
                        catch (Exception e)
                        {
                            log.error("Error during looking for manually added exemptions on request with ID [{}]", request.getId());
                        }
                        if ((getFoiaExemptionService().hasExemptionOnAnyDocumentsOnRequest(request.getId(), request.getObjectType())
                                || hasManuallyAddedExemeptions))
                        {
                            caseFileEventUtility.raiseCustomEvent(saved, "disposition.exemption", "", new Date(), ipAddress, auth.getName(),
                                    auth);
                        }
                    }
                    if ((request.getQueue().getName().equals(FOIAConstants.INTAKE_QUEUE)
                            || request.getQueue().getName().equals(FOIAConstants.FULFILL_QUEUE))
                            && request.getDisposition() != null)
                    {
                        if (request.getQueue().getName().equals(FOIAConstants.FULFILL_QUEUE))
                        {
                            caseFileEventUtility.raiseCustomEvent(saved, "disposition", dispositionValue + " Added", new Date(),
                                    ipAddress, auth.getName(), auth);
                        }
                        else
                        {
                            caseFileEventUtility.raiseCustomEvent(saved, "disposition.reason", dispositionValue + " Added",
                                    new Date(), ipAddress, auth.getName(), auth);
                        }
                        if (request.getOtherReason() != null)
                        {
                            List<StandardLookupEntry> lookupList = (List<StandardLookupEntry>) getLookupDao()
                                    .getLookupByName("requestOtherReason").getEntries();
                            StandardLookupEntry otherReasonLookup = lookupList.stream()
                                    .filter(entry -> request.getOtherReason().equalsIgnoreCase(entry.getKey())).findFirst().orElse(null);
                            String otherReasonValue = otherReasonLookup != null
                                    ? getTranslationService().translate(otherReasonLookup.getValue())
                                    : request.getOtherReason();
                            caseFileEventUtility.raiseCustomEvent(saved, "other.reason", otherReasonValue + " Added", new Date(),
                                    ipAddress, auth.getName(), auth);
                        }
                    }
                    if (request.getDisposition() == null && !saved.getQueue().getName().equals(FOIAConstants.INTAKE_QUEUE)
                            && !request.getQueue().getName().equals(FOIAConstants.FULFILL_QUEUE))
                    {
                        caseFileEventUtility.raiseCustomEvent(saved, "disposition", dispositionValue + " Removed", new Date(),
                                ipAddress, auth.getName(), auth);
                    }
                }
            }
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

    public FOIAExemptionService getFoiaExemptionService()
    {
        return foiaExemptionService;
    }

    public void setFoiaExemptionService(FOIAExemptionService foiaExemptionService)
    {
        this.foiaExemptionService = foiaExemptionService;
    }
}