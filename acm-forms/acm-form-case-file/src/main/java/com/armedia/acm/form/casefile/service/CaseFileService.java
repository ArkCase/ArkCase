/**
 *
 */
package com.armedia.acm.form.casefile.service;

/*-
 * #%L
 * ACM Forms: Case File
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
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.form.casefile.model.CaseFileFormConstants;
import com.armedia.acm.form.config.xml.OwningGroupItem;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.dao.IdentificationDao;
import com.armedia.acm.service.history.dao.AcmHistoryDao;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.users.model.AcmUserActionName;

import org.activiti.engine.RuntimeService;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;

import java.util.Date;
import java.util.List;

/**
 * @author riste.tutureski
 */
public class CaseFileService extends FrevvoFormAbstractService
{

    private Logger LOG = LogManager.getLogger(getClass());
    private CaseFileFactory caseFileFactory;
    private SaveCaseService saveCaseService;
    private AcmHistoryDao acmHistoryDao;
    private CaseFileDao caseFileDao;
    private IdentificationDao identificationDao;
    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private CaseFileEventUtility caseFileEventUtility;
    private String caseFolderNameFormat;
    private RuntimeService activitiRuntimeService;

    private CaseFile caseFile;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.frevvo.config.FrevvoFormService#get(java.lang.String)
     */
    @Override
    public Object get(String action)
    {
        Object result = null;

        if (action != null)
        {
            if ("init-form-data".equals(action))
            {
                result = initFormData();
            }

            if ("init-participants-groups".equals(action))
            {
                result = initParticipantsAndGroupsInfo();
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.frevvo.config.FrevvoFormService#save(java.lang.String,
     * org.springframework.util.MultiValueMap)
     */
    @Override
    public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception
    {
        CaseFile saved = saveCaseFileFromXml(xml, attachments);
        return saved != null;
    }

    public CaseFile saveCaseFileFromXml(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception
    {
        // Convert XML to Object
        CaseFileForm form = (CaseFileForm) convertFromXMLToObject(cleanXML(xml), getFormClass());
        if (form == null)
        {
            LOG.warn("Cannot unmarshall Case Form.");
            return null;
        }

        // Save Case File to the database
        saveCaseFile(form);

        // Handle Case Reinvestigation
        form = handleCaseReinvestigation(form);

        // Create Frevvo form from CaseFile
        form = getCaseFileFactory().asFrevvoCaseFile(getCaseFile(), form, this);

        updateXMLAttachment(attachments, getFormName(), form);

        // Save Attachments
        UploadedFiles uploadedFiles = saveAttachments(getAttachmentFileType(form), attachments, form.getCmisFolderId(),
                FrevvoFormName.CASE_FILE.toUpperCase(), form.getId());

        // Log the last user action
        if (null != form && null != form.getId())
        {
            getUserActionExecutor().execute(form.getId(), AcmUserActionName.LAST_CASE_CREATED, getAuthentication().getName());
        }

        String mode = getRequest().getParameter("mode");
        if (!"edit".equals(mode))
        {
            CaseFileWorkflowListener workflowListener = new CaseFileWorkflowListener();
            workflowListener.handleNewCaseFile(getCaseFile(), uploadedFiles, getActivitiRuntimeService(), getFileWorkflowBusinessRule(),
                    this);
        }

        raiseCaseEvent();

        return getCaseFile();
    }

    private CaseFileForm saveCaseFile(CaseFileForm form) throws AcmCreateObjectFailedException
    {
        LOG.info("Saving case file ...");

        CaseFile caseFile = null;

        // Edit mode
        String mode = getRequest().getParameter("mode");
        if (mode != null && "edit".equals(mode) && form.getId() != null)
        {
            caseFile = getCaseFileDao().find(form.getId());
        }

        caseFile = getCaseFileFactory().asAcmCaseFile(form, caseFile);

        // Save Case file
        try
        {
            caseFile = getSaveCaseService().saveCase(caseFile, getAuthentication(), getUserIpAddress());
        }
        catch (PipelineProcessException | PersistenceException e)
        {
            throw new AcmCreateObjectFailedException("Case File", e.getMessage(), e);
        }

        // Add id's and other information to the Frevvo form
        form.setId(caseFile.getId());
        form.setCaseNumber(caseFile.getCaseNumber());

        setCaseFile(caseFile);

        return form;
    }

    @Override
    public Object convertToFrevvoForm(Object obj, Object form)
    {
        return getCaseFileFactory().asFrevvoCaseFile((CaseFile) obj, (CaseFileForm) form, this);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.frevvo.config.FrevvoFormService#getFormName()
     */
    @Override
    public String getFormName()
    {
        return FrevvoFormName.CASE_FILE;
    }

    @Override
    public Class<?> getFormClass()
    {
        return CaseFileForm.class;
    }

    private Object initFormData()
    {
        CaseFileForm caseFileForm = new CaseFileForm();

        // Init Case File types
        caseFileForm.setCaseTypes(getStandardLookupEntries("caseFileTypes"));

        JSONObject json = createResponse(caseFileForm);

        return json;
    }

    public IdentificationDao getIdentificationDao()
    {
        return identificationDao;
    }

    public void setIdentificationDao(IdentificationDao personIdentificationDao)
    {
        this.identificationDao = personIdentificationDao;
    }

    protected JSONObject initParticipantsAndGroupsInfo()
    {
        CaseFileForm form = new CaseFileForm();

        // Init Participant types
        List<String> participantTypes = getStandardLookupEntries("caseFileParticipantTypes");
        form.setParticipantsTypeOptions(participantTypes);
        form.setParticipantsPrivilegeTypes(getParticipantsPrivilegeTypes(participantTypes, getFormName()));

        // Init Owning Group information
        String owningGroupType = (String) getProperties().get(getFormName() + ".owningGroupType");
        OwningGroupItem owningGroupItem = new OwningGroupItem();
        owningGroupItem.setType(owningGroupType);

        form.setOwningGroup(owningGroupItem);
        form.setOwningGroupOptions(getOwningGroups(owningGroupType, getFormName()));

        JSONObject json = createResponse(form);

        return json;
    }

    private CaseFileForm handleCaseReinvestigation(CaseFileForm form)
    {
        String mode = getRequest().getParameter("mode");
        if (mode != null && "reinvestigate".equals(mode))
        {
            String oldCaseIdAsString = getRequest().getParameter("caseId");
            String oldCaseNumber = getRequest().getParameter("caseNumber");
            Long oldCaseId = null;
            try
            {
                oldCaseId = Long.parseLong(oldCaseIdAsString);
            }
            catch (Exception e)
            {
                LOG.error("Cannot parse String oldCaseId={} to Long.", oldCaseIdAsString, e);
            }

            if (oldCaseId != null && oldCaseNumber != null && form.getId() != null && form.getCaseNumber() != null)
            {
                CaseFile oldCaseFile = getCaseFileDao().find(oldCaseId);
                if (oldCaseFile != null)
                {
                    form = saveReference(form, oldCaseFile);
                    copyCaseDocuments(oldCaseFile, getCaseFile());
                }
            }
        }
        return form;
    }

    private CaseFileForm saveReference(CaseFileForm form, CaseFile oldCaseFile)
    {

        LOG.info("Saving reference ...");

        String status = CaseFileFormConstants.STATUS_ACTIVE;
        String oldCaseTitle = "REINVESTIGATE_" + oldCaseFile.getCaseNumber();

        if (oldCaseFile != null)
        {
            if (oldCaseFile.getStatus() != null)
            {
                status = oldCaseFile.getStatus();
            }
            if (oldCaseFile.getTitle() != null)
            {
                oldCaseTitle = oldCaseFile.getTitle();
            }
        }

        ObjectAssociation objectAssociation = new ObjectAssociation();

        objectAssociation.setStatus(status);
        objectAssociation.setParentType(getFormName().toUpperCase());
        objectAssociation.setParentId(form.getId());
        objectAssociation.setParentName(form.getCaseNumber());
        objectAssociation.setTargetType(getFormName().toUpperCase());
        objectAssociation.setTargetId(oldCaseFile.getId());
        objectAssociation.setTargetName(oldCaseFile.getCaseNumber());
        objectAssociation.setTargetTitle(oldCaseTitle);
        objectAssociation.setAssociationType(CaseFileFormConstants.ASSOCIATION_TYPE_REFERENCE);

        getObjectAssociationDao().save(objectAssociation);

        return form;

    }

    private void copyCaseDocuments(CaseFile oldCase, CaseFile newCase)
    {
        try
        {
            if (oldCase != null && newCase != null)
            {
                AcmContainer oldContainer = getEcmFileService().getOrCreateContainer(oldCase.getObjectType(), oldCase.getId());
                AcmCmisObjectList files = getEcmFileService().allFilesForContainer(getAuthentication(), oldContainer);

                AcmContainer newContainer = getEcmFileService().getOrCreateContainer(newCase.getObjectType(), newCase.getId());

                String caseFolderName = String.format(getCaseFolderNameFormat(), oldCase.getCaseNumber());

                oldContainer.getFolder().setParentFolder(newContainer.getFolder());
                oldContainer.getFolder().setName(caseFolderName);

                if (files != null && files.getChildren() != null)
                {
                    for (AcmCmisObject file : files.getChildren())
                    {
                        EcmFile ecmFile = getEcmFileService().findById(file.getObjectId());
                        if (!ecmFile.getFileType().equals("case_file_xml"))
                        {
                            ecmFile.setContainer(newContainer);
                            ecmFile.setStatus(EcmFileConstants.RECORD);

                            getEcmFileDao().save(ecmFile);
                        }
                    }
                }
            }
        }
        catch (AcmListObjectsFailedException | AcmCreateObjectFailedException | AcmUserActionFailedException e)
        {
            LOG.error("Cannot save old case documents.", e);
        }
    }

    private void raiseCaseEvent()
    {
        // Take user id and ip address
        String userId = getAuthentication().getName();
        String ipAddress = (String) getRequest().getSession().getAttribute("acm_ip_address");
        CaseFile caseFile = getCaseFile();
        if (caseFile != null)
        {
            getCaseFileEventUtility().raiseEvent(getCaseFile(), getCaseFile().getStatus(), new Date(), ipAddress, userId,
                    getAuthentication());

            String mode = getRequest().getParameter("mode");
            if (!"edit".equals(mode))
            {
                getCaseFileEventUtility().raiseEvent(getCaseFile(), "created", new Date(), ipAddress, userId, getAuthentication());
            }
        }
    }

    public CaseFileFactory getCaseFileFactory()
    {
        return caseFileFactory;
    }

    public void setCaseFileFactory(CaseFileFactory caseFileFactory)
    {
        this.caseFileFactory = caseFileFactory;
    }

    public SaveCaseService getSaveCaseService()
    {
        return saveCaseService;
    }

    public void setSaveCaseService(SaveCaseService saveCaseService)
    {
        this.saveCaseService = saveCaseService;
    }

    public AcmHistoryDao getAcmHistoryDao()
    {
        return acmHistoryDao;
    }

    public void setAcmHistoryDao(AcmHistoryDao acmHistoryDao)
    {
        this.acmHistoryDao = acmHistoryDao;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public FileWorkflowBusinessRule getFileWorkflowBusinessRule()
    {
        return fileWorkflowBusinessRule;
    }

    public void setFileWorkflowBusinessRule(FileWorkflowBusinessRule fileWorkflowBusinessRule)
    {
        this.fileWorkflowBusinessRule = fileWorkflowBusinessRule;
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public CaseFile getCaseFile()
    {
        return caseFile;
    }

    public void setCaseFile(CaseFile caseFile)
    {
        this.caseFile = caseFile;
    }

    public CaseFileEventUtility getCaseFileEventUtility()
    {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {
        this.caseFileEventUtility = caseFileEventUtility;
    }

    public String getCaseFolderNameFormat()
    {
        return caseFolderNameFormat;
    }

    public void setCaseFolderNameFormat(String caseFolderNameFormat)
    {
        this.caseFolderNameFormat = caseFolderNameFormat;
    }
}
