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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import gov.foia.broker.FOIARequestFileBrokerClient;
import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIARequest;

/**
 * @author sasko.tanaskoski
 *
 */
public class FOIARequestService
{

    private final Logger log = LoggerFactory.getLogger(getClass());
    private SaveCaseService saveCaseService;
    private ResponseFolderCompressorService responseFolderCompressorService;
    private FOIARequestFileBrokerClient foiaRequestFileBrokerClient;
    private FOIARequestDao foiaRequestDao;
    private CaseFileDao caseFileDao;
    private EcmFileDao ecmFileDao;
    private EcmFileService ecmFileService;
    private NotificationSender notificationSender;
    private String originalRequestFolderNameFormat;
    private String appealTitleFormat;
    private QueuesTimeToCompleteService queuesTimeToCompleteService;
    private FoiaConfigurationService foiaConfigurationService;

    @Transactional
    public CaseFile saveRequest(CaseFile in, Map<String, List<MultipartFile>> filesMap, Authentication auth, String ipAddress)
            throws AcmCreateObjectFailedException
    {
        log.trace("Saving FOIARequest with Request Number: [{}], Request title: [{}], Request ID: [{}]", in.getCaseNumber(), in.getTitle(),
                in.getId());

        // explicitly set modifier and modified to trigger transformer to reindex data
        // fixes problem when some child objects are changed (e.g participants) and solr document is not updated
        in.setModifier(AuthenticationUtils.getUsername());
        in.setModified(new Date());
        try
        {
            CaseFile saved = null;

            if (in != null && in instanceof FOIARequest)
            {
                FOIARequest foiaRequest = (FOIARequest) in;

                if (in.getTitle() == null || in.getTitle().length() == 0)
                {
                    if (((FOIARequest) in).getRequestType().equals(FOIAConstants.NEW_REQUEST_TYPE))
                    {
                        in.setTitle(FOIAConstants.NEW_REQUEST_TITLE);
                    }
                    else
                    {
                        in.setTitle("Appeal of " + ((FOIARequest) in).getOriginalRequestNumber());
                    }
                }

                if (foiaRequest.getId() != null && foiaRequest.getStatus().equalsIgnoreCase("In Review") && !foiaConfigurationService.readConfiguration().getReceivedDateEnabled())
                {
                    if (foiaRequest.getReceivedDate() != null)
                    {
                        in.setDueDate(getQueuesTimeToCompleteService().addWorkingDaysToDate(
                                Date.from(foiaRequest.getReceivedDate().atZone(ZoneId.systemDefault()).toInstant()),
                                foiaRequest.getRequestType()));
                    }
                }
                else if(foiaRequest.getId() == null && foiaConfigurationService.readConfiguration().getReceivedDateEnabled())
                {
                    // calculate due date from time to complete configuration
                    // override if any due date is set from UI
                    in.setDueDate(getQueuesTimeToCompleteService().addWorkingDaysToDate(new Date(), foiaRequest.getRequestType()));
                }

                if (in.getId() == null && foiaRequest.getRequestType().equals(FOIAConstants.APPEAL_REQUEST_TYPE)
                        && foiaRequest.getOriginalRequestNumber() != null
                        && !foiaRequest.getOriginalRequestNumber().isEmpty())
                {
                    FOIARequest originalRequest = (FOIARequest) getCaseFileDao()
                            .findByCaseNumber(foiaRequest.getOriginalRequestNumber());
                    if (originalRequest != null && !originalRequest.getRequestType().equals(FOIAConstants.APPEAL_REQUEST_TYPE)
                            && originalRequest.getStatus().toUpperCase().equals("RELEASED"))
                    {
                        in = populateAppealFromOriginalRequest(in, originalRequest);
                        in = createReference(in, originalRequest);
                        saved = getSaveCaseService().saveCase(in, filesMap, auth, ipAddress);
                        copyOriginalRequestFiles(saved, originalRequest, auth);
                    }
                }
                else
                {
                    setDefaultPhoneAndEmailIfAny(in);
                    saved = getSaveCaseService().saveCase(in, filesMap, auth, ipAddress);

                }
            }
            return saved;
        }
        catch (PipelineProcessException | AcmUserActionFailedException | AcmObjectNotFoundException
                | AcmUpdateObjectFailedException | IOException | AcmListObjectsFailedException e)
        {
            throw new AcmCreateObjectFailedException("FOIARequest", e.getMessage(), e);
        }
    }

    private void setDefaultPhoneAndEmailIfAny(CaseFile saved)
    {
        Person person = saved.getOriginator().getPerson();
        for (ContactMethod contact : person.getContactMethods())
        {
            String type = contact.getType();
            String value = contact.getValue();
            if (type.toLowerCase().equals("phone") && value != null && !value.isEmpty())
            {
                person.setDefaultPhone(contact);
            }
            else if (type.toLowerCase().equals("email") && value != null && !value.isEmpty())
            {
                person.setDefaultEmail(contact);
            }
        }
    }

    private CaseFile populateAppealFromOriginalRequest(CaseFile in, CaseFile originalRequest)
    {

        in.getOriginator().getPerson().setTitle(originalRequest.getOriginator().getPerson().getTitle());
        in.getOriginator().getPerson().setGivenName(originalRequest.getOriginator().getPerson().getGivenName());
        in.getOriginator().getPerson().setMiddleName(originalRequest.getOriginator().getPerson().getMiddleName());
        in.getOriginator().getPerson().setFamilyName(originalRequest.getOriginator().getPerson().getFamilyName());
        in.getOriginator().getPerson().getContactMethods().get(2)
                .setValue(originalRequest.getOriginator().getPerson().getContactMethods().get(2).getValue());

        if (in.getTitle() == null || in.getTitle().isEmpty() || in.getTitle().equals(originalRequest.getTitle()))
        {
            in.setTitle(originalRequest.getTitle() + String.format(getAppealTitleFormat(), originalRequest.getCaseNumber()));
        }
        if (in.getOriginator().getPerson().getAddresses().get(0).getStreetAddress() == null
                || in.getOriginator().getPerson().getAddresses().get(0).getStreetAddress().isEmpty())
        {
            in.getOriginator().getPerson().getAddresses().get(0)
                    .setStreetAddress(originalRequest.getOriginator().getPerson().getAddresses().get(0).getStreetAddress());
        }
        if (in.getOriginator().getPerson().getAddresses().get(0).getStreetAddress2() == null
                || in.getOriginator().getPerson().getAddresses().get(0).getStreetAddress2().isEmpty())
        {
            in.getOriginator().getPerson().getAddresses().get(0)
                    .setCity(originalRequest.getOriginator().getPerson().getAddresses().get(0).getCity());
        }
        if (in.getOriginator().getPerson().getAddresses().get(0).getState() == null
                || in.getOriginator().getPerson().getAddresses().get(0).getState().isEmpty())
        {
            in.getOriginator().getPerson().getAddresses().get(0)
                    .setState(originalRequest.getOriginator().getPerson().getAddresses().get(0).getState());
        }
        if (in.getOriginator().getPerson().getAddresses().get(0).getCountry() == null
                || in.getOriginator().getPerson().getAddresses().get(0).getCountry().isEmpty())
        {
            in.getOriginator().getPerson().getAddresses().get(0)
                    .setCountry(originalRequest.getOriginator().getPerson().getAddresses().get(0).getCountry());
        }
        if (in.getOriginator().getPerson().getAddresses().get(0).getZip() == null
                || in.getOriginator().getPerson().getAddresses().get(0).getZip().isEmpty())
        {
            in.getOriginator().getPerson().getAddresses().get(0)
                    .setZip(originalRequest.getOriginator().getPerson().getAddresses().get(0).getZip());
        }

        return in;
    }

    private CaseFile createReference(CaseFile in, CaseFile originalRequest)
    {
        ObjectAssociation oa = new ObjectAssociation();
        oa.setTargetId(originalRequest.getId());
        oa.setTargetName(originalRequest.getCaseNumber());
        oa.setTargetType(originalRequest.getObjectType());
        oa.setTargetTitle(originalRequest.getTitle());
        oa.setAssociationType("REFERENCE");
        oa.setStatus("ACTIVE");

        in.addChildObject(oa);

        return in;
    }

    private void copyOriginalRequestFiles(CaseFile saved, CaseFile originalRequest, Authentication auth)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmListObjectsFailedException
    {

        AcmContainer container = getEcmFileService().getOrCreateContainer(originalRequest.getObjectType(), originalRequest.getId());
        AcmCmisObjectList files = getEcmFileService().allFilesForContainer(auth, container);

        AcmContainer containerCaseFile = getEcmFileService().getOrCreateContainer(saved.getObjectType(),
                saved.getId());

        String originalrequestFolderName = String.format(getOriginalRequestFolderNameFormat(),
                originalRequest.getCaseNumber());

        AcmFolder originalRootFolder = container.getFolder();
        originalRootFolder.setParentFolder(containerCaseFile.getFolder());
        originalRootFolder.setName(originalrequestFolderName);
        originalRootFolder.setStatus(EcmFileConstants.RECORD);

        setSubfoldersAsRecords(originalRootFolder);

        if (files != null && files.getChildren() != null)
        {
            for (AcmCmisObject file : files.getChildren())
            {
                EcmFile ecmFile = getEcmFileService().findById(file.getObjectId());
                ecmFile.setContainer(containerCaseFile);
                ecmFile.setStatus(EcmFileConstants.RECORD);

                getEcmFileDao().save(ecmFile);
            }
        }

    }

    private void setSubfoldersAsRecords(AcmFolder folder)
    {
        folder.getChildrenFolders().forEach(subfolder -> {
            subfolder.setStatus(EcmFileConstants.RECORD);
            setSubfoldersAsRecords(subfolder);
        });
    }

    /**
     * @return the saveCaseService
     */
    public SaveCaseService getSaveCaseService()
    {
        return saveCaseService;
    }

    /**
     * @param saveCaseService
     *            the saveCaseService to set
     */
    public void setSaveCaseService(SaveCaseService saveCaseService)
    {
        this.saveCaseService = saveCaseService;
    }

    /**
     * @return the responseFolderCompressorService
     */
    public ResponseFolderCompressorService getResponseFolderCompressorService()
    {
        return responseFolderCompressorService;
    }

    /**
     * @param responseFolderCompressorService
     *            the responseFolderCompressorService to set
     */
    public void setResponseFolderCompressorService(ResponseFolderCompressorService responseFolderCompressorService)
    {
        this.responseFolderCompressorService = responseFolderCompressorService;
    }

    /**
     * @return the foiaRequestFileBrokerClient
     */
    public FOIARequestFileBrokerClient getFoiaRequestFileBrokerClient()
    {
        return foiaRequestFileBrokerClient;
    }

    /**
     * @param foiaRequestFileBrokerClient
     *            the foiaRequestFileBrokerClient to set
     */
    public void setFoiaRequestFileBrokerClient(FOIARequestFileBrokerClient foiaRequestFileBrokerClient)
    {
        this.foiaRequestFileBrokerClient = foiaRequestFileBrokerClient;
    }

    /**
     * @return the foiaRequestDao
     */
    public FOIARequestDao getFoiaRequestDao()
    {
        return foiaRequestDao;
    }

    /**
     * @param foiaRequestDao
     *            the foiaRequestDao to set
     */
    public void setFoiaRequestDao(FOIARequestDao foiaRequestDao)
    {
        this.foiaRequestDao = foiaRequestDao;
    }

    /**
     * @return the caseFileDao
     */
    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    /**
     * @param caseFileDao
     *            the caseFileDao to set
     */
    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    /**
     * @return the ecmFileDao
     */
    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    /**
     * @param ecmFileDao
     *            the ecmFileDao to set
     */
    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    /**
     * @return the ecmFileService
     */
    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    /**
     * @param ecmFileService
     *            the ecmFileService to set
     */
    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    /**
     * @return the originalRequestFolderNameFormat
     */
    public String getOriginalRequestFolderNameFormat()
    {
        return originalRequestFolderNameFormat;
    }

    /**
     * @param originalRequestFolderNameFormat
     *            the originalRequestFolderNameFormat to set
     */
    public void setOriginalRequestFolderNameFormat(String originalRequestFolderNameFormat)
    {
        this.originalRequestFolderNameFormat = originalRequestFolderNameFormat;
    }

    /**
     * @return the notificationSender
     */
    public NotificationSender getNotificationSender()
    {
        return notificationSender;
    }

    /**
     * @param notificationSender
     *            the notificationSender to set
     */
    public void setNotificationSender(NotificationSender notificationSender)
    {
        this.notificationSender = notificationSender;
    }

    /**
     * @return the appealTitleFormat
     */
    public String getAppealTitleFormat()
    {
        return appealTitleFormat;
    }

    /**
     * @param appealTitleFormat
     *            the appealTitleFormat to set
     */
    public void setAppealTitleFormat(String appealTitleFormat)
    {
        this.appealTitleFormat = appealTitleFormat;
    }

    public void setQueuesTimeToCompleteService(QueuesTimeToCompleteService queuesTimeToCompleteService)
    {
        this.queuesTimeToCompleteService = queuesTimeToCompleteService;
    }

    public QueuesTimeToCompleteService getQueuesTimeToCompleteService()
    {
        return queuesTimeToCompleteService;
    }

    public FoiaConfigurationService getFoiaConfigurationService() {
        return foiaConfigurationService;
    }

    public void setFoiaConfigurationService(FoiaConfigurationService foiaConfigurationService) {
        this.foiaConfigurationService = foiaConfigurationService;
    }
}
