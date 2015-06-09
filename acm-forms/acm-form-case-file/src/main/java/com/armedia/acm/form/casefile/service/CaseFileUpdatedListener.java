/**
 * 
 */
package com.armedia.acm.form.casefile.service;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.form.casefile.model.CaseFilePSForm;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileUpdatedListener implements ApplicationListener<CaseEvent> {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private Properties properties;
	private CaseFileService caseFileService;
	private CaseFilePSService caseFilePSService;
	
	@Override
	public void onApplicationEvent(CaseEvent event) {
		if ("com.armedia.acm.casefile.event.updated".equals(event.getEventType().toLowerCase()))
		{
			LOG.debug("Updating Frevvo XML file ...");
			
			if (getProperties() != null)
			{
				
				if (getProperties().containsKey(CaseFileConstants.ACTIVE_CASE_FORM_KEY))
				{
					String activeFormName = (String) getProperties().get(CaseFileConstants.ACTIVE_CASE_FORM_KEY);
					
					if (FrevvoFormName.CASE_FILE.equals(activeFormName))
					{
						getCaseFileService().updateXML(event.getCaseFile(), event.getEventUser(), CaseFileForm.class);
					}
					else if (FrevvoFormName.CASE_FILE_PS.equals(activeFormName))
					{
						getCaseFilePSService().updateXML(event.getCaseFile(), event.getEventUser(), CaseFilePSForm.class);
					}
				}
			}
		}
	}

	
	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public CaseFileService getCaseFileService() {
		return caseFileService;
	}

	public void setCaseFileService(CaseFileService caseFileService) {
		this.caseFileService = caseFileService;
	}

	public CaseFilePSService getCaseFilePSService() {
		return caseFilePSService;
	}

	public void setCaseFilePSService(CaseFilePSService caseFilePSService) {
		this.caseFilePSService = caseFilePSService;
	}

}
