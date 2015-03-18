package com.armedia.acm.plugins.casefile.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileHistoryListener implements ApplicationListener<CaseEvent> {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private static final String OBJECT_TYPE = "CASE_FILE";
	
	private AcmObjectHistoryService acmObjectHistoryService;
	
	@Override
	public void onApplicationEvent(CaseEvent event) 
	{
		LOG.debug("Case File event raised. Start adding it to the object history ...");
		
		if (event != null)
		{
            boolean execute = checkExecution(event.getEventType());
            if( execute ) {

                CaseFile caseFile = (CaseFile) event.getSource();

                getAcmObjectHistoryService().save(event.getUserId(), event.getEventType(), caseFile, caseFile.getId(), OBJECT_TYPE, event.getEventDate(), event.getIpAddress());

                LOG.debug("Case File History added to database.");
            }
		}
	}

    private boolean checkExecution(String eventType) {
        if ("com.armedia.acm.casefile.event.created".equals(eventType) || "com.armedia.acm.casefile.event.updated".equals(eventType)) {
            return true;
        }

        return false;
    }
	public AcmObjectHistoryService getAcmObjectHistoryService()
	{
		return acmObjectHistoryService;
	}

	public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService) 
	{
		this.acmObjectHistoryService = acmObjectHistoryService;
	}
}
