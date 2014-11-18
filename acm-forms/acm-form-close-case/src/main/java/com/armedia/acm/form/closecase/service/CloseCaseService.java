/**
 * 
 */
package com.armedia.acm.form.closecase.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.armedia.acm.form.closecase.model.CloseCaseForm;
import com.armedia.acm.form.config.CloseInformation;
import com.armedia.acm.frevvo.config.FrevvoFormName;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.dao.CloseCaseRequestDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CloseCaseRequest;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.users.model.AcmUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author riste.tutureski
 *
 */
public class CloseCaseService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private CaseFileDao caseFileDao;
	private CloseCaseRequestDao closeCaseRequestDao;
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
		CloseCaseForm form = (CloseCaseForm) convertFromXMLToObject(cleanXML(xml), CloseCaseForm.class);
		
		if (form == null)
		{
			LOG.warn("Cannot unmarshall Close Case Form.");
			return false;
		}
		
		// Get CaseFile depends on the CaseFile ID
		CaseFile caseFile = getCaseFileDao().find(form.getInformation().getId());
		
		if (caseFile == null) 
		{
			LOG.warn("Cannot find case file by given caseId=" + form.getInformation().getId());
			return false;
		}
		
		// Skip if the case is already closed and if it's not edit mode 
		if ("CLOSED".equals(caseFile.getStatus()) && !"edit".equals(mode))
		{
			LOG.info("The case file is already in '" + caseFile.getStatus() + "' mode. No further action will be taken.");
			return true;
		}
		
		CloseCaseRequestFactory factory = new CloseCaseRequestFactory();
		CloseCaseRequest closeCaseRequest = factory.formFromXml(form, getAuthentication());
		
		if ("edit".equals(mode))
		{
			String requestId = getRequest().getParameter("requestId");
			CloseCaseRequest closeCaseRequestFromDatabase = null;
			
			try{
	        	Long closeCaseRequestId = Long.parseLong(requestId);
	        	closeCaseRequestFromDatabase = getCloseCaseRequestDao().find(closeCaseRequestId);
        	}
        	catch(Exception e)
        	{
        		LOG.warn("Close Case Request with id=" + requestId + " is not found. The new request will be recorded in the database.");
        	}
			
			if (null != closeCaseRequestFromDatabase){
        		closeCaseRequest.setId(closeCaseRequestFromDatabase.getId());
        		getCloseCaseRequestDao().delete(closeCaseRequestFromDatabase.getParticipants());
        	}
		}
		
		CloseCaseRequest savedRequest = getCloseCaseRequestDao().save(closeCaseRequest);
		
		if (!caseFile.getStatus().equals("IN APPROVAL") && !"edit".equals(mode)){
			getCaseFileDao().updateComplaintStatus(caseFile.getId(), "IN APPROVAL", getAuthentication().getName(), form.getInformation().getCloseDate());
		}
		
		// Save attachments (or update XML form and PDF form if the mode is "edit")
		FrevvoUploadedFiles uploadedFiles = saveAttachments(
                attachments,
                caseFile.getEcmFolderId(),
                FrevvoFormName.CASE.toUpperCase(),
                caseFile.getId(),
                caseFile.getCaseNumber());
		
		return false;
	}

    @Override
    public String getFormName()
    {
        return FrevvoFormName.CLOSE_CASE;
    }
    
    private Object initFormData()
    {
    	String mode = getRequest().getParameter("mode");
    	CloseCaseForm closeCase = new CloseCaseForm();
    	
    	CloseInformation information = new CloseInformation();
		if (!"edit".equals(mode))
		{
			information.setCloseDate(new Date());
		}
		information.setDispositions(convertToList((String) getProperties().get(FrevvoFormName.CLOSE_CASE + ".dispositions"), ","));
		
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
		
		closeCase.setInformation(information);
		closeCase.setApproverOptions(approverOptions);
    	
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
		String jsonString = gson.toJson(closeCase);
		
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

	/**
	 * @return the closeCaseRequestDao
	 */
	public CloseCaseRequestDao getCloseCaseRequestDao() {
		return closeCaseRequestDao;
	}

	/**
	 * @param closeCaseRequestDao the closeCaseRequestDao to set
	 */
	public void setCloseCaseRequestDao(CloseCaseRequestDao closeCaseRequestDao) {
		this.closeCaseRequestDao = closeCaseRequestDao;
	}

	/**
	 * @return the muleClient
	 */
	public MuleClient getMuleClient() {
		return muleClient;
	}

	/**
	 * @param muleClient the muleClient to set
	 */
	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}

}
