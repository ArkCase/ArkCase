/**
 * 
 */
package com.armedia.acm.form.plain.service;

import com.armedia.acm.form.plain.model.PlainForm;
import com.armedia.acm.form.plain.model.PlainFormCreatedEvent;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author riste.tutureski
 *
 */
public class PlainFormService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private String formName;
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Override
	public Object get(String action) 
	{
		// No implementation is needed so far
		return null;
	}

	@Override
	public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception 
	{
		Long folderId = getFolderAndFilesUtils().convertToLong((String) getRequest().getParameter("folderId"));
		String cmisFolderId = null;
		PlainForm form = (PlainForm) convertFromXMLToObject(cleanXML(xml), PlainForm.class);
		
		if (form == null) 
		{
			LOG.warn("Cannot umarshall " + getFormName() + " form.");
			return false;
		}
		
		try 
		{
			cmisFolderId = findCmisFolderId(folderId, null, form.getObjectType(), form.getId());
		} 
		catch (Exception e) 
		{
			LOG.error("Cannot take CMIS folder id to be able to save attachments.", e);
			return false;
		}

		FrevvoUploadedFiles uploaded = saveAttachments(attachments, cmisFolderId, form.getObjectType(), form.getObjectId());

		PlainFormCreatedEvent event = new PlainFormCreatedEvent(
				form, getFormName(), folderId, cmisFolderId, getAuthentication().getName(), getUserIpAddress(),
				uploaded.getPdfRendition().getFileId(), uploaded.getFormXml().getFileId());
		getApplicationEventPublisher().publishEvent(event);
		
		return true;
	}

	@Override
	public String getFormName() 
	{
		return formName;
	}

	@Override
	public Class<?> getFormClass()
	{
		return PlainForm.class;
	}

	public void setFormName(String formName) 
	{
		this.formName = formName;
	}

	public ApplicationEventPublisher getApplicationEventPublisher()
	{
		return applicationEventPublisher;
	}

	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
	{
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public Object convertToFrevvoForm(Object obj, Object form) {
		// Implementation no needed so far
		return null;
	}

}
