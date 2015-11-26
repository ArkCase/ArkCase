/**
 * 
 */
package com.armedia.acm.form.plain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.form.plain.model.PlainForm;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;

/**
 * @author riste.tutureski
 *
 */
public class PlainFormService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private String formName;
	
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
		
		saveAttachments(attachments, cmisFolderId, form.getObjectType(), form.getObjectId());
		
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

	@Override
	public Object convertToFrevvoForm(Object obj, Object form) {
		// Implementation no needed so far
		return null;
	}

}
