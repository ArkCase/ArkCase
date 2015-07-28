/**
 * 
 */
package com.armedia.acm.frevvo.config;

import java.util.List;

import com.frevvo.forms.client.ApplicationEntry;
import com.frevvo.forms.client.FormTypeEntry;
import com.frevvo.forms.client.FormsService;
import com.frevvo.forms.client.SchemaEntry;

/**
 * @author riste.tutureski
 *
 */
public interface FrevvoService {

	public void login();
	public void logout();
	public ApplicationEntry getApplication(String id);
	public FormTypeEntry getForm(String id);
	public List<FormTypeEntry> getForms(ApplicationEntry application);
	public List<FormTypeEntry> getPlainForms(ApplicationEntry application);
	public String getFormKey(SchemaEntry schema);
	public String getFormType(FormTypeEntry form);
	public String getFormApplicationId(FormTypeEntry form);
	public SchemaEntry getSchema(String id);
	public FormsService getFormsService();
	public FrevvoFormUrl getFormUrl();
	
}
