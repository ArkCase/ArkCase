
/**
 *
 */
package com.armedia.acm.service.frevvo.forms.web.api;

/*-
 * #%L
 * ACM Service: Frevvo Forms Service
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

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.form.casefile.service.CaseFileFactory;
import com.armedia.acm.form.cost.service.CostFactory;
import com.armedia.acm.form.plainconfiguration.service.PlainConfigurationFormFactory;
import com.armedia.acm.form.project.service.ProjectFactory;
import com.armedia.acm.form.time.service.TimeFactory;
import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.frevvo.config.FrevvoService;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.dao.ChangeCaseStatusDao;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import com.armedia.acm.plugins.complaint.service.ComplaintFactory;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.person.dao.IdentificationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.service.frevvo.forms.factory.FrevvoFormServiceFactory;
import com.armedia.acm.service.history.dao.AcmHistoryDao;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.service.CostsheetEventPublisher;
import com.armedia.acm.services.costsheet.service.CostsheetService;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.service.NotificationEventPublisher;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.service.TimesheetEventPublisher;
import com.armedia.acm.services.timesheet.service.TimesheetService;
import com.armedia.acm.services.users.dao.UserActionDao;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.service.ldap.AcmUserActionExecutor;

import org.activiti.engine.RuntimeService;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author riste.tutureski
 */
@Controller
@RequestMapping("/api/v1/forms/crud/acm")
public class FrevvoFormController implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    private Logger LOG = LogManager.getLogger(FrevvoFormController.class);

    private Map<String, Object> properties;
    private AuthenticationTokenService authenticationTokenService;
    private UserDao userDao;
    private UserActionDao userActionDao;
    private ComplaintDao complaintDao;
    private CaseFileDao caseFileDao;
    private CloseComplaintRequestDao closeComplaintRequestDao;
    private ChangeCaseStatusDao changeCaseStatusDao;
    private PersonDao personDao;
    private EcmFileDao ecmFileDao;
    private CaseFileFactory caseFileFactory;
    private AcmUserActionExecutor userActionExecutor;
    private LookupDao lookupDao;
    private TranslationService translationService;

    private SaveComplaintTransaction saveComplaintTransaction;
    private EcmFileService ecmFileService;

    private SaveCaseService saveCaseService;

    private AcmHistoryDao acmHistoryDao;

    private ObjectAssociationDao objectAssociationDao;
    private IdentificationDao identificationDao;

    private FileWorkflowBusinessRule fileWorkflowBusinessRule;

    private RuntimeService activitiRuntimeService;

    private ComplaintEventPublisher complaintEventPublisher;

    private TimesheetService timesheetService;
    private AcmTimesheetDao acmTimesheetDao;
    private TimeFactory timeFactory;

    private CostsheetService costsheetService;
    private AcmCostsheetDao acmCostsheetDao;
    private CostFactory costFactory;

    private SearchResults searchResults;

    private AcmContainerDao acmContainerDao;

    private TimesheetEventPublisher timesheetEventPublisher;
    private CostsheetEventPublisher costsheetEventPublisher;

    private FunctionalAccessService functionalAccessService;

    private ComplaintFactory complaintFactory;

    private ProjectFactory projectFactory;

    private FolderAndFilesUtils folderAndFilesUtils;
    private AcmFolderService acmFolderService;

    private FrevvoService frevvoService;

    private PlainConfigurationFormFactory plainConfigurationFormFactory;

    private String plainFormPropertiesLocation;

    private PropertyFileManager propertyFileManager;

    private NotificationDao notificationDao;
    private NotificationEventPublisher notificationEventPublisher;

    private FrevvoFormServiceFactory frevvoFormServiceFactory;

    @RequestMapping(value = "/{formName}/init")
    public void doInit(Authentication authentication, @PathVariable("formName") String formName, HttpServletRequest request,
            HttpServletResponse response)
    {

        LOG.info("Initialization form \"" + formName + "\"");

        // Create and initialize appropriate service for given form name
        FrevvoFormService frevvoFormService = getFrevvoFormServiceFactory().getService(formName, this, request, authentication);

        // Initialize some data that should be shown on the form (if there should be any) - this is happen while form is
        // loading for the
        // first time
        String result = (String) frevvoFormService.init();
        try
        {
            if (result != null)
            {
                response.setContentType("text/xml");
                response.getOutputStream().write(result.getBytes(Charset.forName("UTF-8")));
                response.getOutputStream().flush();
            }
            else
            {
                LOG.warn("Empty response.");
            }
        }
        catch (Exception e)
        {
            LOG.error("The output cannot be returned.", e);
        }

    }

    @RequestMapping(value = "/{formName}/get/{action}")
    public void doGet(Authentication authentication, @PathVariable("formName") String formName, @PathVariable("action") String action,
            HttpServletRequest request, HttpServletResponse response)
    {

        LOG.info("Execute action \"" + action + "\" for form \"" + formName + "\"");

        // Create and initialize appropriate service for given form name
        FrevvoFormService frevvoFormService = getFrevvoFormServiceFactory().getService(formName, this, request, authentication);

        // Initialize some data that should be shown on the form (if there should be any) - this is happening after form
        // is loaded
        Object result = frevvoFormService.get(action);
        try
        {
            if (result != null)
            {
                if (result instanceof String)
                {
                    response.setContentType("text/xml");
                    response.getOutputStream().write(((String) result).getBytes(Charset.forName("UTF-8")));
                    response.getOutputStream().flush();
                }
                else if (result instanceof JSONObject)
                {
                    response.addHeader("X-JSON", result.toString());
                    response.setContentType("application/json");
                    response.getOutputStream().write((result.toString()).getBytes(Charset.forName("UTF-8")));
                    response.getOutputStream().flush();
                }
                else
                {
                    LOG.warn("Unknown response type for action '" + action + "', response type is: " + result.getClass().getName());
                }
            }
            else
            {
                LOG.warn("Empty response.");
            }

        }
        catch (Exception e)
        {
            LOG.error("The output cannot be returned.", e);
        }

    }

    @RequestMapping(value = "/{formName}/save")
    public void doSave(Authentication authentication, @PathVariable("formName") String formName, HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
    {

        LOG.info("Save form \"" + formName + "\"");

        // Create and initialize appropriate service for given form name
        FrevvoFormService frevvoFormService = getFrevvoFormServiceFactory().getService(formName, this, request, authentication);
        frevvoFormService.setUserIpAddress((String) session.getAttribute("acm_ip_address"));

        try
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

            if (multipartRequest != null && multipartRequest.getFileMap() != null)
            {
                MultipartFile formDataFile = multipartRequest.getFileMap().get("form_" + formName);
                if (formDataFile != null)
                {
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(formDataFile.getInputStream(), writer, Charset.forName("UTF-8"));
                    String xml = writer.toString();

                    frevvoFormService.save(xml, multipartRequest.getMultiFileMap());
                }
            }
        }
        catch (Exception e)
        {
            LOG.error("Cannot read request information.", e);
        }
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
    }

    /**
     * @return the authenticationTokenService
     */
    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    /**
     * @param authenticationTokenService
     *            the authenticationTokenService to set
     */
    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

    /**
     * @return the userDao
     */
    public UserDao getUserDao()
    {
        return userDao;
    }

    /**
     * @param userDao
     *            the userDao to set
     */
    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    /**
     * @return the userActionDao
     */
    public UserActionDao getUserActionDao()
    {
        return userActionDao;
    }

    /**
     * @param userActionDao
     *            the userActionDao to set
     */
    public void setUserActionDao(UserActionDao userActionDao)
    {
        this.userActionDao = userActionDao;
    }

    /**
     * @return the complaintDao
     */
    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    /**
     * @param complaintDao
     *            the complaintDao to set
     */
    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
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

    public SaveComplaintTransaction getSaveComplaintTransaction()
    {
        return saveComplaintTransaction;
    }

    public void setSaveComplaintTransaction(SaveComplaintTransaction saveComplaintTransaction)
    {
        this.saveComplaintTransaction = saveComplaintTransaction;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public CloseComplaintRequestDao getCloseComplaintRequestDao()
    {
        return closeComplaintRequestDao;
    }

    public void setCloseComplaintRequestDao(CloseComplaintRequestDao closeComplaintRequestDao)
    {
        this.closeComplaintRequestDao = closeComplaintRequestDao;
    }

    public ChangeCaseStatusDao getChangeCaseStatusDao()
    {
        return changeCaseStatusDao;
    }

    public void setChangeCaseStatusDao(ChangeCaseStatusDao changeCaseStatusDao)
    {
        this.changeCaseStatusDao = changeCaseStatusDao;
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public CaseFileFactory getCaseFileFactory()
    {
        return caseFileFactory;
    }

    public void setCaseFileFactory(CaseFileFactory caseFileFactory)
    {
        this.caseFileFactory = caseFileFactory;
    }

    public AcmUserActionExecutor getUserActionExecutor()
    {
        return userActionExecutor;
    }

    public void setUserActionExecutor(AcmUserActionExecutor userActionExecutor)
    {
        this.userActionExecutor = userActionExecutor;
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

    public ObjectAssociationDao getObjectAssociationDao()
    {
        return objectAssociationDao;
    }

    public void setObjectAssociationDao(ObjectAssociationDao objectAssociationDao)
    {
        this.objectAssociationDao = objectAssociationDao;
    }

    public IdentificationDao getIdentificationDao()
    {
        return identificationDao;
    }

    public void setIdentificationDao(IdentificationDao identificationDao)
    {
        this.identificationDao = identificationDao;
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

    public ComplaintEventPublisher getComplaintEventPublisher()
    {
        return complaintEventPublisher;
    }

    public void setComplaintEventPublisher(ComplaintEventPublisher complaintEventPublisher)
    {
        this.complaintEventPublisher = complaintEventPublisher;
    }

    public TimesheetService getTimesheetService()
    {
        return timesheetService;
    }

    public void setTimesheetService(TimesheetService timesheetService)
    {
        this.timesheetService = timesheetService;
    }

    public AcmTimesheetDao getAcmTimesheetDao()
    {
        return acmTimesheetDao;
    }

    public void setAcmTimesheetDao(AcmTimesheetDao acmTimesheetDao)
    {
        this.acmTimesheetDao = acmTimesheetDao;
    }

    public TimeFactory getTimeFactory()
    {
        return timeFactory;
    }

    public void setTimeFactory(TimeFactory timeFactory)
    {
        this.timeFactory = timeFactory;
    }

    public CostsheetService getCostsheetService()
    {
        return costsheetService;
    }

    public void setCostsheetService(CostsheetService costsheetService)
    {
        this.costsheetService = costsheetService;
    }

    public AcmCostsheetDao getAcmCostsheetDao()
    {
        return acmCostsheetDao;
    }

    public void setAcmCostsheetDao(AcmCostsheetDao acmCostsheetDao)
    {
        this.acmCostsheetDao = acmCostsheetDao;
    }

    public CostFactory getCostFactory()
    {
        return costFactory;
    }

    public void setCostFactory(CostFactory costFactory)
    {
        this.costFactory = costFactory;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }

    public AcmContainerDao getAcmContainerDao()
    {
        return acmContainerDao;
    }

    public void setAcmContainerDao(AcmContainerDao acmContainerDao)
    {
        this.acmContainerDao = acmContainerDao;
    }

    public TimesheetEventPublisher getTimesheetEventPublisher()
    {
        return timesheetEventPublisher;
    }

    public void setTimesheetEventPublisher(TimesheetEventPublisher timesheetEventPublisher)
    {
        this.timesheetEventPublisher = timesheetEventPublisher;
    }

    public CostsheetEventPublisher getCostsheetEventPublisher()
    {
        return costsheetEventPublisher;
    }

    public void setCostsheetEventPublisher(CostsheetEventPublisher costsheetEventPublisher)
    {
        this.costsheetEventPublisher = costsheetEventPublisher;
    }

    public FunctionalAccessService getFunctionalAccessService()
    {
        return functionalAccessService;
    }

    public void setFunctionalAccessService(FunctionalAccessService functionalAccessService)
    {
        this.functionalAccessService = functionalAccessService;
    }

    public ComplaintFactory getComplaintFactory()
    {
        return complaintFactory;
    }

    public void setComplaintFactory(ComplaintFactory complaintFactory)
    {
        this.complaintFactory = complaintFactory;
    }

    public ProjectFactory getProjectFactory()
    {
        return projectFactory;
    }

    public void setProjectFactory(ProjectFactory projectFactory)
    {
        this.projectFactory = projectFactory;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public FrevvoService getFrevvoService()
    {
        return frevvoService;
    }

    public void setFrevvoService(FrevvoService frevvoService)
    {
        this.frevvoService = frevvoService;
    }

    public PlainConfigurationFormFactory getPlainConfigurationFormFactory()
    {
        return plainConfigurationFormFactory;
    }

    public void setPlainConfigurationFormFactory(PlainConfigurationFormFactory plainConfigurationFormFactory)
    {
        this.plainConfigurationFormFactory = plainConfigurationFormFactory;
    }

    public String getPlainFormPropertiesLocation()
    {
        return plainFormPropertiesLocation;
    }

    public void setPlainFormPropertiesLocation(String plainFormPropertiesLocation)
    {
        this.plainFormPropertiesLocation = plainFormPropertiesLocation;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public NotificationEventPublisher getNotificationEventPublisher()
    {
        return notificationEventPublisher;
    }

    public void setNotificationEventPublisher(NotificationEventPublisher notificationEventPublisher)
    {
        this.notificationEventPublisher = notificationEventPublisher;
    }

    public FrevvoFormServiceFactory getFrevvoFormServiceFactory()
    {
        return frevvoFormServiceFactory;
    }

    public void setFrevvoFormServiceFactory(FrevvoFormServiceFactory frevvoFormServiceFactory)
    {
        this.frevvoFormServiceFactory = frevvoFormServiceFactory;
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
