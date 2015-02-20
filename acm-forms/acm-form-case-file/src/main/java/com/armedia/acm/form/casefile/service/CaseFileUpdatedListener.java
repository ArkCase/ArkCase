/**
 * 
 */
package com.armedia.acm.form.casefile.service;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.casefile.model.CaseEvent;

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
				boolean isCaseFile = false;
				boolean isCaseFilePS = false;
				
				if (getProperties().containsKey(FrevvoFormName.CASE_FILE + ".id"))
				{
					isCaseFile = true;
				}
				
				if (getProperties().containsKey(FrevvoFormName.CASE_FILE_PS + ".id"))
				{
					isCaseFilePS = true;
				}
				
				// Ark Case File have advantage over PS Case File
				// NOTE: In the acm-forms.properties should be defined only one - case_file or case_file_ps, otherwise Ark Case File logic will be processed
				
				if (isCaseFile)
				{
					getCaseFileService().updateXML(event.getCaseFile(), event.getEventUser());
				} 
				else if (isCaseFilePS)
				{
					getCaseFilePSService().updateXML(event.getCaseFile(), event.getEventUser());
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
