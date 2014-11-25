/**
 * 
 */
package com.armedia.acm.plugins.casefile.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.activiti.AcmBusinessProcessEvent;

/**
 * @author riste.tutureski
 *
 */
public class ChangeCaseStatusProcessEndListener implements ApplicationListener<AcmBusinessProcessEvent> {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	private ChangeCaseFileStateService changeCaseFileStateService;
	
	@Override
	public void onApplicationEvent(AcmBusinessProcessEvent event) 
	{
		boolean isChangeCaseStatusWorkflow = checkChangeCaseStatusWorkflow(event);		
		
		if ( isChangeCaseStatusWorkflow )
        {
            try
            {
            	Long caseId = (Long) event.getProcessVariables().get("OBJECT_ID");
                Long requestId = (Long) event.getProcessVariables().get("REQUEST_ID");
                String user = event.getUserId();
                
                LOG.debug("Request id: " + requestId);
                LOG.debug("Case File id: " + caseId);
                LOG.debug("User: " + user);
                
                getChangeCaseFileStateService().handleChangeCaseStatusApproved(caseId, requestId, user, event.getEventDate(), event.getIpAddress());
            }
            catch ( Exception e)
            {
                LOG.error("Exception handling completed change case status: " + e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
	}
	
	private boolean checkChangeCaseStatusWorkflow(AcmBusinessProcessEvent event)
	{
		if ( ! "com.armedia.acm.activiti.businessProcess.end".equals(event.getEventType()))
        {
            LOG.debug("Event is not the end of a business process: " + event.getEventType());
            return false;
        }
		
		Map<String, Object> pvars = event.getProcessVariables();

        if ( !pvars.containsKey("REQUEST_TYPE"))
        {
            LOG.debug("Event does not contain a request type");
            return false;
        }
        
        if ( !"CHANGE_CASE_STATUS".equals(pvars.get("REQUEST_TYPE")))
        {
            LOG.debug("Request type is not CHANGE_CASE_STATUS: " + pvars.get("REQUEST_TYPE"));
            return false;
        }
        
        if ( !pvars.containsKey("reviewOutcome"))
        {
            LOG.debug("Event does not contain a review outcome");
            return false;
        }
        
        if ( !"APPROVE".equals(pvars.get("reviewOutcome")))
        {
            LOG.debug("Request type is not APPROVE: " + pvars.get("reviewOutcome"));
            return false;
        }
        
        LOG.debug("This event marks the end of an approved change case status.");
		
		return true;
	}

	public ChangeCaseFileStateService getChangeCaseFileStateService() {
		return changeCaseFileStateService;
	}

	public void setChangeCaseFileStateService(
			ChangeCaseFileStateService changeCaseFileStateService) {
		this.changeCaseFileStateService = changeCaseFileStateService;
	}

}
