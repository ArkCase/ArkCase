/**
 * 
 */
package com.armedia.acm.form.plainconfiguration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.form.plainconfiguration.model.PlainConfigurationForm;
import com.armedia.acm.frevvo.config.FrevvoService;
import com.frevvo.forms.client.FormTypeEntry;
import com.frevvo.forms.client.SchemaEntry;

/**
 * @author riste.tutureski
 *
 */
public class PlainConfigurationFormFactory {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private FrevvoService frevvoService;
	
	public PlainConfigurationForm convertFromFormTypeEntry(FormTypeEntry formEntry, SchemaEntry schemaEntry)
	{
		PlainConfigurationForm form = new PlainConfigurationForm();
		
		try
		{
			if (formEntry != null && schemaEntry != null)
			{
				form.setKey(getFrevvoService().getFormKey(schemaEntry));
				form.setFormId(formEntry.getId());
				form.setName(formEntry.getTitle().getPlainText());
				form.setType(getFrevvoService().getFormType(formEntry));
			}
		}
		catch(Exception e)
		{
			LOG.error("Cannot convert Frevvo Form Entry to Plain Configuration form.", e);
		}
		
		return form;
	}

	public FrevvoService getFrevvoService() {
		return frevvoService;
	}

	public void setFrevvoService(FrevvoService frevvoService) {
		this.frevvoService = frevvoService;
	}
	
}
