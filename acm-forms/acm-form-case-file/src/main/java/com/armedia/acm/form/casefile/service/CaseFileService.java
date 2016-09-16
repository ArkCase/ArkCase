/**
 *
 */
package com.armedia.acm.form.casefile.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.form.casefile.model.CaseFileFormConstants;
import com.armedia.acm.form.config.xml.OwningGroupItem;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.dao.IdentificationDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.xml.InitiatorPerson;
import com.armedia.acm.plugins.person.model.xml.PeoplePerson;
import com.armedia.acm.service.history.dao.AcmHistoryDao;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserActionName;

import org.activiti.engine.RuntimeService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author riste.tutureski
 */
public class CaseFileService extends FrevvoFormAbstractService
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private CaseFileFactory caseFileFactory;
    private SaveCaseService saveCaseService;
    private AcmHistoryDao acmHistoryDao;
    private CaseFileDao caseFileDao;
    private IdentificationDao identificationDao;
    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private CaseFileEventUtility caseFileEventUtility;

    private RuntimeService activitiRuntimeService;

    private CaseFile caseFile;

    private FunctionalAccessService functionalAccessService;

    /*
     * (non-Javadoc)
     * 
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
     * 
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

        // Save Reference (Reinvestigation)
        form = saveReference(form);

        // Create Frevvo form from CaseFile
        form = getCaseFileFactory().asFrevvoCaseFile(getCaseFile(), form, this);

        // Save Attachments
        FrevvoUploadedFiles frevvoFiles = saveAttachments(getAttachmentFileType(form), attachments, form.getCmisFolderId(),
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
            workflowListener.handleNewCaseFile(getCaseFile(), frevvoFiles, getActivitiRuntimeService(), getFileWorkflowBusinessRule(),
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
        } catch (PipelineProcessException | PersistenceException e)
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
     * 
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
        caseFileForm.setCaseTypes(convertToList((String) getProperties().get(getFormName() + ".types"), ","));

        // Init Initiator information
        caseFileForm.setInitiator(initInitiator());

        // Init People information
        caseFileForm.setPeople(initPeople());

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

    private InitiatorPerson initInitiator()
    {
        InitiatorPerson initiator = new InitiatorPerson();

        List<String> titles = convertToList((String) getProperties().get(getFormName() + ".titles"), ",");
        initiator.setTitles(titles);
        initiator.setContactMethods(initContactMethods());
        initiator.setOrganizations(initOrganizations());
        initiator.setAddresses(initAddresses());
        initiator.setType(CaseFileFormConstants.PERSON_TYPE_INITIATOR);
        initiator.setTypes(convertToList((String) getProperties().get(getFormName() + ".personTypes"), ","));

        return initiator;
    }

    private List<Person> initPeople()
    {
        List<Person> people = new ArrayList<>();

        PeoplePerson peoplePerson = new PeoplePerson();

        List<String> titles = convertToList((String) getProperties().get(getFormName() + ".titles"), ",");
        peoplePerson.setTitles(titles);
        peoplePerson.setContactMethods(initContactMethods());
        peoplePerson.setOrganizations(initOrganizations());
        peoplePerson.setAddresses(initAddresses());

        List<String> types = convertToList((String) getProperties().get(getFormName() + ".personTypes"), ",");

        // Remove "Initiator". It's first in the list
        if (types != null && types.size() > 0)
        {
            types.remove(0);
        }

        peoplePerson.setTypes(types);

        people.add(peoplePerson);

        return people;
    }

    private JSONObject initParticipantsAndGroupsInfo()
    {
        CaseFileForm form = new CaseFileForm();

        // Init Participant types
        List<String> participantTypes = convertToList((String) getProperties().get(getFormName() + ".participantTypes"), ",");
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

    private List<ContactMethod> initContactMethods()
    {
        List<ContactMethod> contactMethods = new ArrayList<>();
        List<String> contactMethodTypes = convertToList((String) getProperties().get(getFormName() + ".deviceTypes"), ",");

        ContactMethod contactMethod = new ContactMethod();

        contactMethod.setTypes(contactMethodTypes);
        contactMethod.setCreated(new Date());
        contactMethod.setCreator(getUserFullName());

        contactMethods.add(contactMethod);

        return contactMethods;
    }

    private List<Organization> initOrganizations()
    {
        List<Organization> organizations = new ArrayList<>();
        List<String> organizationsTypes = convertToList((String) getProperties().get(getFormName() + ".organizationTypes"), ",");

        Organization organization = new Organization();

        organization.setOrganizationTypes(organizationsTypes);
        organization.setCreated(new Date());
        organization.setCreator(getUserFullName());

        organizations.add(organization);

        return organizations;
    }

    private List<PostalAddress> initAddresses()
    {
        List<PostalAddress> locations = new ArrayList<>();
        List<String> locationTypes = convertToList((String) getProperties().get(getFormName() + ".locationTypes"), ",");

        PostalAddress location = new PostalAddress();

        location.setTypes(locationTypes);
        location.setCreated(new Date());
        location.setCreator(getUserFullName());

        locations.add(location);

        return locations;
    }

    private String getUserFullName()
    {
        String fullName = null;

        String userId = getAuthentication().getName();

        if (userId != null)
        {
            AcmUser user = getUserDao().findByUserId(userId);

            if (user != null)
            {
                return user.getFullName();
            }
        }

        return fullName;
    }

    private CaseFileForm saveReference(CaseFileForm form)
    {
        String mode = getRequest().getParameter("mode");
        if (mode != null && "reinvestigate".equals(mode))
        {
            LOG.info("Saving reference ...");

            String oldCaseIdAsString = getRequest().getParameter("caseId");
            String oldCaseNumber = getRequest().getParameter("caseNumber");
            String oldCaseTitle = getRequest().getParameter("caseTitle");
            Long oldCaseId = null;
            try
            {
                oldCaseId = Long.parseLong(oldCaseIdAsString);
            } catch (Exception e)
            {
                LOG.error("Cannot parse String oldCaseId=" + oldCaseIdAsString + " to Long.", e);
            }

            if (oldCaseId != null && oldCaseNumber != null && form.getId() != null && form.getCaseNumber() != null)
            {
                String status = CaseFileFormConstants.STATUS_ACTIVE;
                CaseFile caseFile = getCaseFileDao().find(oldCaseId);
                if (caseFile != null && caseFile.getStatus() != null)
                {
                    status = caseFile.getStatus();
                }

                ObjectAssociation objectAssociation = new ObjectAssociation();

                objectAssociation.setStatus(status);
                objectAssociation.setParentType(getFormName().toUpperCase());
                objectAssociation.setParentId(form.getId());
                objectAssociation.setParentName(form.getCaseNumber());
                objectAssociation.setTargetType(getFormName().toUpperCase());
                objectAssociation.setTargetId(oldCaseId);
                objectAssociation.setTargetName(oldCaseNumber);
                objectAssociation.setTargetTitle(oldCaseTitle);
                objectAssociation.setAssociationType(CaseFileFormConstants.ASSOCIATION_TYPE_REFERENCE);

                getObjectAssociationDao().save(objectAssociation);
            }
        }

        return form;
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
                getCaseFileEventUtility().raiseEvent(getCaseFile(), "updated", new Date(), ipAddress, userId, getAuthentication());
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

    @Override
    public FunctionalAccessService getFunctionalAccessService()
    {
        return functionalAccessService;
    }

    @Override
    public void setFunctionalAccessService(FunctionalAccessService functionalAccessService)
    {
        this.functionalAccessService = functionalAccessService;
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
