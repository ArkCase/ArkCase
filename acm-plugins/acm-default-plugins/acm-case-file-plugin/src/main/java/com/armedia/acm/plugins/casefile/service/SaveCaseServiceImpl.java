package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.outlook.service.OutlookContainerCalendarService;
import com.armedia.acm.plugins.profile.model.OutlookDTO;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import com.armedia.acm.service.outlook.exception.AcmOutlookCreateItemFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import microsoft.exchange.webservices.data.enumeration.DefaultExtendedPropertySet;
import microsoft.exchange.webservices.data.enumeration.MapiPropertyType;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by armdev on 8/29/14.
 */
public class SaveCaseServiceImpl implements SaveCaseService
{
    private CaseFileDao caseFileDao;
    private SaveCaseFileBusinessRule saveRule;
    private CaseFileEventUtility caseFileEventUtility;
    private MuleContextManager muleContextManager;
    private OutlookService outlookService;
    private OutlookContainerCalendarService outlookContainerCalendarService;
    private UserDao userDao;
    private UserOrgService userOrgService;

    private boolean autoCreateFolderForCaseFile;
    private boolean autoDeleteFolderAfterCaseClosed;

    private AcmFolderService acmFolderService;
    private EcmFileService ecmFileService;
    private String folderStructureAsString;
    private String nextCourtHearingDateCalendarSubject;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    @Transactional
    public CaseFile saveCase(CaseFile in, Authentication auth, String ipAddress) throws MuleException
    {
        boolean newCase = in.getId() == null;
        if ( newCase )
        {
            in.setCreator(auth.getName());
        }

        in.setModified(new Date());
        in.setModifier(auth.getName());

        CaseFile retval = getCaseFileDao().save(in);

        log.info("Saving case: retval is null? " + ( retval == null));

        retval = getSaveRule().applyRules(retval);

        // call Mule flow to create the Alfresco folder
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("acmUser", auth);

        MuleMessage received = getMuleContextManager().send("vm://saveCaseFile.in", retval, messageProps);

        CaseFile saved = received.getPayload(CaseFile.class);
        MuleException e = received.getInboundProperty("saveException");

        if ( e != null )
        {
            throw e;
        }

        //create calendar folder
        if (autoCreateFolderForCaseFile && newCase) {
            createOutlookFolder(saved);
        }

        if (!newCase && !StringUtils.isEmpty(saved.getContainer().getCalendarFolderId())) {
            //update folder participants
            updateOutlookFolderPerticipants(saved);
        }
        
        // add next court date in calendar
        addNextCourtHearingDateToCalendar(saved, newCase, auth);

        if ( newCase )
        {
        	createFolderStructure(retval);
            getCaseFileEventUtility().raiseEvent(retval, retval.getStatus(), new Date(), ipAddress, auth.getName(), auth);
        }
        else
        {
        	getCaseFileEventUtility().raiseEvent(retval, "updated", new Date(), ipAddress, auth.getName(), auth);
        }


        return saved;
    }

    public void createOutlookFolder(CaseFile caseFile) {
        try {
            outlookContainerCalendarService.createFolder(caseFile.getTitle() + "(" + caseFile.getCaseNumber() + ")",
                    caseFile.getContainer(), caseFile.getParticipants());
        } catch (AcmOutlookItemNotFoundException e) {
            log.error("Error creating calendar folder for " + caseFile.getCaseNumber(), e);
        } catch (AcmOutlookCreateItemFailedException e) {
            log.error("Error creating calendar folder for " + caseFile.getCaseNumber(), e);
        }
    }

    private void updateOutlookFolderPerticipants(CaseFile caseFile) {
        try {
            AcmContainer container = caseFile.getContainer();
            outlookContainerCalendarService.updateFolderParticipants(container.getCalendarFolderId(),
                    caseFile.getParticipants());
        } catch (AcmOutlookItemNotFoundException e) {
            log.error("Error updating participants for " + caseFile.getCaseNumber(), e);
        }
    }

    private void createFolderStructure(CaseFile caseFile)
    {
    	if (getFolderStructureAsString() != null && !getFolderStructureAsString().isEmpty())
    	try
    	{
    		log.debug("Folder Structure: " + getFolderStructureAsString());
    		JSONArray folderStructure = new JSONArray(getFolderStructureAsString());
    		AcmContainer container = getContainer(caseFile);
    		getAcmFolderService().addFolderStructure(container, container.getFolder(), folderStructure);
    	}
    	catch (Exception e)
    	{
    		log.error("Cannot create folder structure.", e);
    	}
    }

    private AcmContainer getContainer(CaseFile caseFile) throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
    	return getEcmFileService().getOrCreateContainer(caseFile.getObjectType(), caseFile.getId());
    }
    
    private void addNextCourtHearingDateToCalendar(CaseFile caseFile, boolean newCase, Authentication auth)
    {
    	try
    	{
    		String userId = auth.getName();
    		AcmUser user = getUserDao().findByUserId(userId);
    		OutlookDTO outlookDTO = getUserOrgService().retrieveOutlookPassword(auth);
    		AcmOutlookUser outlookUser = new AcmOutlookUser(auth.getName(), user.getMail(), outlookDTO.getOutlookPassword());
    		
    		OutlookCalendarItem item = createNextCourtHearingDateItem(caseFile);
    		
    		if (!newCase)
    		{
    			getOutlookService().deleteAllItemsFoundByExtendedProperty(item.getFolderId(), outlookUser, item.getExtendedPropertyDefinition(), item.getExtendedPropertyValue());
    		}
    		
    		getOutlookService().createOutlookAppointment(outlookUser, item);
    	}
    	catch (Exception e)
    	{
    		log.error("Could not add Next Court Hearing Date to Calendar.", e);
    	}
    }
    
    private OutlookCalendarItem createNextCourtHearingDateItem(CaseFile caseFile)
    {
    	// Adding extended property definition and value to the appointment. With this extended property, we can search to find it
		// and delete correct appointment while updating case file. Here is created extended property for next court date. After
		// updating next court date in the case file, we should find the current appointment for it, delete it and create new one with correct date.
		ExtendedPropertyDefinition extendedPropertyDefinition = null;
		Object extendedPropertyValue = null;
		try 
		{
			extendedPropertyDefinition = new ExtendedPropertyDefinition(DefaultExtendedPropertySet.PublicStrings, CaseFileConstants.NEXT_COURT_HEARING_DATE_CALENDAR_ID, MapiPropertyType.String);
			extendedPropertyValue = CaseFileConstants.NEXT_COURT_HEARING_DATE_CALENDAR_ID + caseFile.getObjectType() + caseFile.getId();
		}
		catch (Exception e) 
		{
			log.error("Cannot create extended property definition and value.", e);
		}
    	
    	OutlookCalendarItem item = new OutlookCalendarItem();
    	
		item.setSubject(getNextCourtHearingDateCalendarSubject());
		item.setStartDate(caseFile.getNextCourtDate());
		item.setEndDate(caseFile.getNextCourtDate());
		item.setFolderId(caseFile.getContainer().getCalendarFolderId());
		item.setExtendedPropertyDefinition(extendedPropertyDefinition);
		item.setExtendedPropertyValue(extendedPropertyValue);
		
		return item;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public SaveCaseFileBusinessRule getSaveRule()
    {
        return saveRule;
    }

    public void setSaveRule(SaveCaseFileBusinessRule saveRule)
    {
        this.saveRule = saveRule;
    }

    public CaseFileEventUtility getCaseFileEventUtility()
    {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {
        this.caseFileEventUtility = caseFileEventUtility;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public OutlookService getOutlookService() {
		return outlookService;
	}

	public void setOutlookService(OutlookService outlookService) {
		this.outlookService = outlookService;
	}

	public void setOutlookContainerCalendarService(OutlookContainerCalendarService outlookContainerCalendarService) {
        this.outlookContainerCalendarService = outlookContainerCalendarService;
    }
	
    public UserOrgService getUserOrgService() {
		return userOrgService;
	}

	public void setUserOrgService(UserOrgService userOrgService) {
		this.userOrgService = userOrgService;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setAutoCreateFolderForCaseFile(boolean autoCreateFolderForCaseFile) {
        this.autoCreateFolderForCaseFile = autoCreateFolderForCaseFile;
    }

    public void setAutoDeleteFolderAfterCaseClosed(boolean autoDeleteFolderAfterCaseClosed) {
        this.autoDeleteFolderAfterCaseClosed = autoDeleteFolderAfterCaseClosed;
    }

	public AcmFolderService getAcmFolderService() {
		return acmFolderService;
	}

	public void setAcmFolderService(AcmFolderService acmFolderService) {
		this.acmFolderService = acmFolderService;
	}

	public EcmFileService getEcmFileService() {
		return ecmFileService;
	}

	public void setEcmFileService(EcmFileService ecmFileService) {
		this.ecmFileService = ecmFileService;
	}

	public String getFolderStructureAsString() {
		return folderStructureAsString;
	}

	public void setFolderStructureAsString(String folderStructureAsString) {
		this.folderStructureAsString = folderStructureAsString;
	}

	public String getNextCourtHearingDateCalendarSubject() {
		return nextCourtHearingDateCalendarSubject;
	}

	public void setNextCourtHearingDateCalendarSubject(
			String nextCourtHearingDateCalendarSubject) {
		this.nextCourtHearingDateCalendarSubject = nextCourtHearingDateCalendarSubject;
	}
}
