/**
 * 
 */
package com.armedia.acm.form.casefile.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.plugins.casefile.model.CaseEvent;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileUpdatedListener implements ApplicationListener<CaseEvent> {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private CaseFileService caseFileService;
	
	@Override
	public void onApplicationEvent(CaseEvent event) {
		if ("com.armedia.acm.casefile.event.updated".equals(event.getEventType().toLowerCase()))
		{
			LOG.debug("Updating Frevvo XML file ...");
			
			getCaseFileService().updateXML(event.getCaseFile());
		}
	}

	public CaseFileService getCaseFileService() {
		return caseFileService;
	}

	public void setCaseFileService(CaseFileService caseFileService) {
		this.caseFileService = caseFileService;
	}

}
