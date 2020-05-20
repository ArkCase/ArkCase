package gov.foia.service;

/*-
 * #%L
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

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.auth.AcmAuthenticationManager;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.labels.service.TranslationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import gov.foia.model.DispositionReason;
import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIARequest;
import gov.foia.model.FOIARequesterAssociation;
import gov.foia.model.FoiaConfig;

/**
 * Created by ivana.shekerova
 */

public class FOIARequestEventListener implements ApplicationListener<AcmObjectHistoryEvent>
{
    private final Logger log = LogManager.getLogger(getClass());

    private AcmObjectHistoryService acmObjectHistoryService;
    private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;
    private FOIARequestEventUtility foiaRequestEventUtility;
    private ObjectConverter objectConverter;
    private LookupDao lookupDao;
    private TranslationService translationService;
    private FoiaConfig foiaConfig;
    private AcmAuthenticationManager authenticationManager;
    private SaveFOIARequestService saveFOIARequestService;

    @Override
    public void onApplicationEvent(AcmObjectHistoryEvent event)
    {
        if (event != null)
        {
            AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();

            String principal = event.getUserId();
            AcmAuthentication authentication;
            try
            {
                authentication = authenticationManager.getAcmAuthentication(
                        new UsernamePasswordAuthenticationToken(principal, principal));
            }
            catch (AuthenticationServiceException e)
            {
                authentication = new AcmAuthentication(Collections.emptySet(), principal, "",
                        true, principal);
            }

            boolean isCaseFile = checkExecution(acmObjectHistory.getObjectType());

            if (isCaseFile)
            {
                // Converter for JSON string to Object
                AcmUnmarshaller converter = getObjectConverter().getJsonUnmarshaller();

                String jsonUpdatedCaseFile = acmObjectHistory.getObjectString();
                FOIARequest updatedCaseFile = converter.unmarshall(jsonUpdatedCaseFile, FOIARequest.class);

                if (updatedCaseFile.getRequestType().equals(FOIAConstants.APPEAL_REQUEST_TYPE))
                {

                    AcmObjectHistory acmObjectHistoryExisting = getAcmObjectHistoryService().getAcmObjectHistory(updatedCaseFile.getId(),
                            CaseFileConstants.OBJECT_TYPE);

                    if (acmObjectHistoryExisting != null)
                    {

                        String json = acmObjectHistoryExisting.getObjectString();
                        FOIARequest existing = converter.unmarshall(json, FOIARequest.class);

                        if (existing != null)
                        {
                            checkDispositionReasons(existing, updatedCaseFile, event.getIpAddress());

                            if (foiaConfig.getAutomaticCreationOfRequestWhenAppealIsRemandedEnabled()
                                    && isStatusChangedToClosed(existing, updatedCaseFile))
                            {
                                if (updatedCaseFile.getDisposition() != null
                                        && (updatedCaseFile.getDisposition().equals("partially-affirmed")
                                                || updatedCaseFile.getDisposition().equals("completely-reversed")))
                                {
                                    if (!updatedCaseFile.getChildObjects().isEmpty())
                                    {
                                        Long initialRequestId = updatedCaseFile.getChildObjects().stream().findFirst().get().getTargetId();
                                        FOIARequest initialRequest = (FOIARequest) getSaveFOIARequestService().getFoiaRequestService()
                                                .getCaseFileDao().find(initialRequestId);
                                        FOIARequest newRequest = populateRequest(initialRequest, updatedCaseFile);
                                        FOIARequest saved = null;
                                        try
                                        {
                                            saved = (FOIARequest) getSaveFOIARequestService()
                                                    .saveFOIARequest(newRequest, null, authentication, event.getIpAddress());
                                        }
                                        catch (AcmCreateObjectFailedException e)
                                        {
                                            log.error("Can't save Remanded Foia Request for Request with id [{}]", initialRequestId, e);
                                        }

                                        log.debug("Remanded FOIA Request: {}", saved);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void checkDispositionReasons(FOIARequest request, FOIARequest updatedRequest, String ipAddress)
    {
        List<DispositionReason> existing = request.getDispositionReasons();
        List<DispositionReason> updated = updatedRequest.getDispositionReasons();

        List<StandardLookupEntry> lookupList = (List<StandardLookupEntry>) getLookupDao()
                .getLookupByName("dispositionReasons").getEntries();

        for (DispositionReason dispositionReason : existing)
        {
            if (!updated.contains(dispositionReason))
            {
                StandardLookupEntry dispositionLookup = lookupList.stream()
                        .filter(entry -> dispositionReason.getReason().equalsIgnoreCase(entry.getKey())).findFirst()
                        .orElse(null);
                String dispReasonValue = dispositionLookup != null
                        ? getTranslationService().translate(dispositionLookup.getValue())
                        : dispositionReason.getReason();
                foiaRequestEventUtility.raiseDispositionReasonModifiedInCaseFile(
                        dispositionReason,
                        updatedRequest,
                        ipAddress,
                        dispReasonValue + " Removed");

            }
        }

        for (DispositionReason dispositionReason : updated)
        {
            if (!existing.contains(dispositionReason))
            {
                StandardLookupEntry dispositionLookup = lookupList.stream()
                        .filter(entry -> dispositionReason.getReason().equalsIgnoreCase(entry.getKey())).findFirst()
                        .orElse(null);
                String dispReasonValue = dispositionLookup != null
                        ? getTranslationService().translate(dispositionLookup.getValue())
                        : dispositionReason.getReason();
                foiaRequestEventUtility.raiseDispositionReasonModifiedInCaseFile(
                        dispositionReason,
                        updatedRequest,
                        ipAddress,
                        dispReasonValue + " Added");
            }
        }
    }

    private boolean isStatusChangedToClosed(CaseFile caseFile, CaseFile updatedCaseFile)
    {
        String updatedStatus = updatedCaseFile.getStatus();
        String status = caseFile.getStatus();
        return !Objects.equals(updatedStatus, status) && updatedStatus.equalsIgnoreCase("Closed");
    }

    public FOIARequest populateRequest(FOIARequest initialRequest, FOIARequest appeal)
    {
        FOIARequest request = new FOIARequest();

        request.setRequestType(initialRequest.getRequestType());
        request.setOriginalRequestNumber(initialRequest.getOriginalRequestNumber());
        request.setFoiaConfiguration(initialRequest.getFoiaConfiguration());
        request.setRequestCategory(initialRequest.getRequestCategory());
        request.setDeliveryMethodOfResponse(initialRequest.getDeliveryMethodOfResponse());

        request.setTitle(initialRequest.getTitle() + " - Remanded");

        request.setDetails(initialRequest.getDetails());
        request.setRequestTrack(initialRequest.getRequestTrack());
        request.setOtherReason(initialRequest.getOtherReason());
        request.setComponentAgency(initialRequest.getComponentAgency());
        request.setNotificationGroup(initialRequest.getNotificationGroup());

        FOIARequesterAssociation requesterAssociation = new FOIARequesterAssociation();
        PersonAssociation originator = initialRequest.getOriginator();
        requesterAssociation.setPerson(originator.getPerson());
        requesterAssociation.setPersonType(originator.getPersonType());
        requesterAssociation.setParentType(originator.getParentType());

        request.setOriginator(requesterAssociation);

        request = (FOIARequest) getSaveFOIARequestService().getFoiaRequestService().createReference(request, initialRequest);
        request = (FOIARequest) getSaveFOIARequestService().getFoiaRequestService().createReference(request, appeal);

        request.setRecordSearchDateFrom(initialRequest.getRecordSearchDateFrom());
        request.setRecordSearchDateTo(initialRequest.getRecordSearchDateTo());

        request.setProcessingFeeWaive(initialRequest.getProcessingFeeWaive());
        request.setFeeWaiverFlag(initialRequest.getFeeWaiverFlag());
        request.setRequestFeeWaiveReason(initialRequest.getRequestFeeWaiveReason());

        request.setPayFee(initialRequest.getPayFee());

        request.setExpediteFlag(initialRequest.getExpediteFlag());
        request.setRequestExpediteReason(initialRequest.getRequestExpediteReason());

        return request;
    }

    private boolean checkExecution(String objectType)
    {

        return objectType.equals(CaseFileConstants.OBJECT_TYPE);
    }

    public AcmObjectHistoryService getAcmObjectHistoryService()
    {

        return acmObjectHistoryService;
    }

    public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService)
    {

        this.acmObjectHistoryService = acmObjectHistoryService;
    }

    public FOIARequestEventUtility getFoiaRequestEventUtility()
    {
        return foiaRequestEventUtility;
    }

    public void setFoiaRequestEventUtility(FOIARequestEventUtility foiaRequestEventUtility)
    {
        this.foiaRequestEventUtility = foiaRequestEventUtility;
    }

    public AcmObjectHistoryEventPublisher getAcmObjectHistoryEventPublisher()
    {
        return acmObjectHistoryEventPublisher;
    }

    public void setAcmObjectHistoryEventPublisher(AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher)
    {
        this.acmObjectHistoryEventPublisher = acmObjectHistoryEventPublisher;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
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

    public FoiaConfig getFoiaConfig()
    {
        return foiaConfig;
    }

    public void setFoiaConfig(FoiaConfig foiaConfig)
    {
        this.foiaConfig = foiaConfig;
    }

    public AcmAuthenticationManager getAuthenticationManager()
    {
        return authenticationManager;
    }

    public void setAuthenticationManager(AcmAuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }

    public SaveFOIARequestService getSaveFOIARequestService()
    {
        return saveFOIARequestService;
    }

    public void setSaveFOIARequestService(SaveFOIARequestService saveFOIARequestService)
    {
        this.saveFOIARequestService = saveFOIARequestService;
    }
}
