/**
 * 
 */
package com.armedia.acm.form.closecomplaint.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.armedia.acm.form.closecomplaint.model.CloseComplaintFormEvent;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.form.closecomplaint.model.CloseComplaintForm;
import com.armedia.acm.form.closecomplaint.model.CloseComplaintInformation;
import com.armedia.acm.form.closecomplaint.model.ExistingCase;
import com.armedia.acm.form.closecomplaint.model.ReferExternal;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.users.model.AcmUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author riste.tutureski
 *
 */
public class CloseComplaintService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(CloseComplaintService.class);
	private ComplaintDao complaintDao;
	private CaseFileDao caseFileDao;
    private CloseComplaintRequestDao closeComplaintRequestDao;
	private ApplicationEventPublisher applicationEventPublisher;
	private MuleClient muleClient;
			
	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#init()
	 */
	@Override
	public Object init() {

		Object result = "";
		
		String mode = getRequest().getParameter("mode");
		String xmlId = getRequest().getParameter("xmlId");
		
		if ("edit".equals(mode) && null != xmlId && !"".equals(xmlId))
		{
			try{
				Long id = Long.parseLong(xmlId);
				EcmFile file = getEcmFileDao().find(id);
				
				MuleMessage message = getMuleClient().send("vm://downloadFileFlow.in", file.getEcmFileId(), null);
				
				if (null != message && message.getPayload() instanceof ContentStream)
				{
					result = getContent((ContentStream) message.getPayload());
				}
				
			}
			catch(Exception e)
			{
				LOG.warn("EcmFile with id=" + xmlId + " is not found while edit mode. Empty Frevvo form will be shown.");
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#get(java.lang.String)
	 */
	@Override
	public Object get(String action) {
		Object result = null;
		
		if (action != null) {
			if ("init-form-data".equals(action)) {
				result = initFormData();
			}
			
			if ("search-approvers".equals(action)) {
				String keyword = getRequest().getParameter("keyword");
				result = searchApprovers(keyword);
			}
			
			if ("case".equals(action)) {
				String caseNumber = getRequest().getParameter("caseNumber");
				result = getCase(caseNumber);

			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#save(java.lang.String, org.springframework.util.MultiValueMap)
	 */
	@Override
	public boolean save(String xml,
			MultiValueMap<String, MultipartFile> attachments) throws Exception {

		String mode = getRequest().getParameter("mode");
		
		// Convert XML data to Object
		CloseComplaintForm form = (CloseComplaintForm) convertFromXMLToObject(cleanXML(xml), CloseComplaintForm.class);
		
		if (form == null){
			LOG.warn("Cannot unmarshall Close Complaint Form.");
			return false;
		}
		
		// Get Complaint depends on the complaint ID
		Complaint complaint = getComplaintDao().find(form.getInformation().getComplaintId());
		
		if (complaint == null) {
			LOG.warn("Cannot find complaint by given complaintId=" + form.getInformation().getComplaintId());
			return false;
		}
		
		if (("IN APPROVAL".equals(complaint.getStatus()) || "CLOSED".equals(complaint.getStatus())) && !"edit".equals(mode)){
			LOG.info("The complaint is already in '" + complaint.getStatus() + "' mode. No further action will be taken.");
			return true;
		}

        CloseComplaintRequestFactory factory = new CloseComplaintRequestFactory();
        CloseComplaintRequest closeComplaintRequest = factory.fromFormXml(form, getAuthentication());
        
        if ("edit".equals(mode)){
        	String requestId = getRequest().getParameter("requestId");
        	CloseComplaintRequest closeComplaintRequestFromDatabase = null;
        	try{
	        	Long closeComplaintRequestId = Long.parseLong(requestId);
	        	closeComplaintRequestFromDatabase = getCloseComplaintRequestDao().find(closeComplaintRequestId);
        	}
        	catch(Exception e)
        	{
        		LOG.warn("Close Complaint Request with id=" + requestId + " is not found. The new request will be recorded in the database.");
        	}
        	
        	if (null != closeComplaintRequestFromDatabase){
        		closeComplaintRequest.setId(closeComplaintRequestFromDatabase.getId());
        		getCloseComplaintRequestDao().delete(closeComplaintRequestFromDatabase.getParticipants());
        	}
        }
        
        CloseComplaintRequest savedRequest = getCloseComplaintRequestDao().save(closeComplaintRequest);
		
		// Update Status to "IN APPROVAL"
		if (!complaint.getStatus().equals("IN APPROVAL") && !"edit".equals(mode)){
			getComplaintDao().updateComplaintStatus(complaint.getComplaintId(), "IN APPROVAL", getAuthentication().getName(), form.getInformation().getCloseDate());
		}
		
		// Save attachments (or update XML form and PDF form if the mode is "edit")
		FrevvoUploadedFiles uploadedFiles = saveAttachments(
                attachments,
                complaint.getEcmFolderId(),
                FrevvoFormName.COMPLAINT.toUpperCase(),
                complaint.getComplaintId(),
                complaint.getComplaintNumber());

		CloseComplaintFormEvent event = new CloseComplaintFormEvent(
				complaint.getComplaintNumber(), complaint.getComplaintId(), savedRequest, uploadedFiles, mode,
				getAuthentication().getName(), getUserIpAddress(), true);
				getApplicationEventPublisher().publishEvent(event);
		
		return true;
	}
	
	private Object initFormData(){

		String mode = getRequest().getParameter("mode");
		CloseComplaintForm closeComplaint = new CloseComplaintForm();
		
		CloseComplaintInformation information = new CloseComplaintInformation();
		if (!"edit".equals(mode))
		{
			information.setCloseDate(new Date());
		}
		information.setDispositions(convertToList((String) getProperties().get(FrevvoFormName.CLOSE_COMPLAINT + ".dispositions"), ","));

		// Get Approvers
		List<AcmUser> acmUsers = getUserDao().findByFullNameKeyword("");
		
		List<String> approverOptions = new ArrayList<String>();
		if (acmUsers != null && acmUsers.size() > 0){
			for (AcmUser acmUser : acmUsers) {
				// Add only users that are not the logged user
				if (!acmUser.getUserId().equals(getAuthentication().getName()) || "edit".equals(mode)){
					approverOptions.add(acmUser.getUserId() + "=" + acmUser.getFullName());
				}
			}
		}
		
		ReferExternal referExternal = new ReferExternal();
		if (!"edit".equals(mode))
		{
			referExternal.setDate(new Date());
		}
		ContactMethod contact = new ContactMethod();
		contact.setTypes(convertToList((String) getProperties().get(FrevvoFormName.CLOSE_COMPLAINT + ".deviceTypes"), ","));
		referExternal.setContact(contact);
		
		closeComplaint.setInformation(information);
		closeComplaint.setApproverOptions(approverOptions);
		closeComplaint.setReferExternal(referExternal);
		
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
		String jsonString = gson.toJson(closeComplaint);
		
		JSONObject json = new JSONObject(jsonString);

		return json;
	}
	
	private Object searchApprovers(String keyword){
		
		String mode = getRequest().getParameter("mode");
		CloseComplaintForm closeComplaint = new CloseComplaintForm();
		
		List<String> approverOptions = new ArrayList<String>();
		
		if (keyword != null){
			// Get Approvers
			List<AcmUser> acmUsers = getUserDao().findByFullNameKeyword(keyword);

			if (acmUsers != null && acmUsers.size() > 0){
				for (AcmUser acmUser : acmUsers) {
					// Add only users that are not the logged user
					if (!acmUser.getUserId().equals(getAuthentication().getName())  || "edit".equals(mode)){
						approverOptions.add(acmUser.getUserId() + "=" + acmUser.getFullName());
					}
				}
			}
		}
		
		closeComplaint.setApproverOptions(approverOptions);
		
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
		String jsonString = gson.toJson(closeComplaint);
		
		JSONObject json = new JSONObject(jsonString);
		
		return json;
	}
	
	private Object getCase(String caseNumber){
		CloseComplaintForm closeComplaint = new CloseComplaintForm();
		ExistingCase existingCase = new ExistingCase();

		CaseFile caseFile = null;
		
		try{
			caseFile = getCaseFileDao().findByCaseNumber(caseNumber);
		}catch(Exception e){
			LOG.warn("The case with number '" + caseNumber + "' doesn't exist.");
		}
		
		if (caseFile != null){
			existingCase.setCaseNumber(caseNumber);
			existingCase.setCaseTitle(caseFile.getTitle());
			existingCase.setCaseCreationDate(caseFile.getCreated());
			existingCase.setCasePriority(caseFile.getPriority());
		}
		
		closeComplaint.setExistingCase(existingCase);
		
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
		String jsonString = gson.toJson(closeComplaint);
		
		JSONObject json = new JSONObject(jsonString);
		
		return json;
	}
	
	
	private String getContent(ContentStream contentStream)
	{
		String content = "";
		InputStream inputStream = null;
		
		try
        {
			inputStream = contentStream.getStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer);
			content = writer.toString();
        } 
		catch (IOException e) 
		{
        	LOG.error("Could not copy input stream to the writer: " + e.getMessage(), e);
		}
		finally
        {
            if ( inputStream != null )
            {
                try
                {
                	inputStream.close();
                }
                catch (IOException e)
                {
                    LOG.error("Could not close CMIS content stream: " + e.getMessage(), e);
                }
            }
        }
		
		return content;
	}


    @Override
    public String getFormName()
    {
        return FrevvoFormName.CLOSE_COMPLAINT;
    }

	/**
	 * @return the complaintDao
	 */
	public ComplaintDao getComplaintDao() {
		return complaintDao;
	}

	/**
	 * @param complaintDao the complaintDao to set
	 */
	public void setComplaintDao(ComplaintDao complaintDao) {
		this.complaintDao = complaintDao;
	}

	/**
	 * @return the caseFileDao
	 */
	public CaseFileDao getCaseFileDao() {
		return caseFileDao;
	}

	/**
	 * @param caseFileDao the caseFileDao to set
	 */
	public void setCaseFileDao(CaseFileDao caseFileDao) {
		this.caseFileDao = caseFileDao;
	}

    public CloseComplaintRequestDao getCloseComplaintRequestDao()
    {
        return closeComplaintRequestDao;
    }

    public void setCloseComplaintRequestDao(CloseComplaintRequestDao closeComplaintRequestDao)
    {
        this.closeComplaintRequestDao = closeComplaintRequestDao;
    }

	public ApplicationEventPublisher getApplicationEventPublisher()
	{
		return applicationEventPublisher;
	}

	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
	{
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public MuleClient getMuleClient() {
		return muleClient;
	}

	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}
}
