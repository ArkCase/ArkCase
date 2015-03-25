/**
 * 
 */
package com.armedia.acm.services.timesheet.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.AcmTimesheetEvent;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;

/**
 * @author riste.tutureski
 *
 */
public class TimesheetWorkflowListener implements ApplicationListener<AcmTimesheetEvent>{

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private FileWorkflowBusinessRule fileWorkflowBusinessRule;
	private RuntimeService activitiRuntimeService;
	
	@Override
	public void onApplicationEvent(AcmTimesheetEvent event) 
	{
		if (event != null && event.isStartWorkflow())	
		{
			startWorkflow(event);
		}
	}
	
	protected void startWorkflow(AcmTimesheetEvent event)
	{
		EcmFile pdfRendition = event.getFrevvoUploadedFiles().getPdfRendition();
		EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();
		
		configuration.setEcmFile(pdfRendition);
		
		LOG.debug("Calling business rules");
		
		configuration = getFileWorkflowBusinessRule().applyRules(configuration);
		
		LOG.debug("Start process? " + configuration.isStartProcess());
		
		if ( configuration.isStartProcess() )
        {
			startWorkflow(event, configuration);
        }
	}
	
	private void startWorkflow(AcmTimesheetEvent event, EcmFileWorkflowConfiguration configuration)
	{
		AcmTimesheet timesheet = (AcmTimesheet) event.getSource();
		String processName = configuration.getProcessName();
		
		String author = event.getUserId();
		List<String> reviewers = findReviewers(event);
		
		String taskName = createName(timesheet);
		
		Map<String, Object> pvars = new HashMap<>();
		
		pvars.put("reviewers", reviewers);
	    pvars.put("taskName", taskName);
	    pvars.put("documentAuthor", author);
	    pvars.put("pdfRenditionId", event.getFrevvoUploadedFiles().getPdfRendition().getFileId());
	    pvars.put("formXmlId", event.getFrevvoUploadedFiles().getFormXml().getFileId());
	    
	    pvars.put("OBJECT_TYPE", TimesheetConstants.OBJECT_TYPE);
	    pvars.put("OBJECT_ID", timesheet.getId());
	    pvars.put("OBJECT_NAME", createName(timesheet));
	     
	    LOG.debug("Starting process: " + processName);
	     
	    ProcessInstance pi = getActivitiRuntimeService().startProcessInstanceByKey(processName, pvars);

	    LOG.debug("process ID: " + pi.getId());
	}
	
	private List<String> findReviewers(AcmTimesheetEvent event)
	{
		List<String> reviewers = new ArrayList<>();
		 
		for ( AcmParticipant participant : ((AcmTimesheet) event.getSource()).getParticipants() )
		{
			if ( "approver".equals(participant.getParticipantType() ) )
			{
				reviewers.add(participant.getParticipantLdapId());
			}
		}
		 
		return reviewers;
	}
	
	public String createName(AcmTimesheet timesheet)
	{
		SimpleDateFormat formatter = new SimpleDateFormat(DateFormats.TIMESHEET_DATE_FORMAT);
		
		String objectType =  StringUtils.capitalize(TimesheetConstants.OBJECT_TYPE.toLowerCase());
		String startDate = formatter.format(timesheet.getStartDate());
		String endDate = formatter.format(timesheet.getEndDate());
		
		return objectType + " " + startDate + "-" + endDate;
	}

	public FileWorkflowBusinessRule getFileWorkflowBusinessRule() {
		return fileWorkflowBusinessRule;
	}

	public void setFileWorkflowBusinessRule(
			FileWorkflowBusinessRule fileWorkflowBusinessRule) {
		this.fileWorkflowBusinessRule = fileWorkflowBusinessRule;
	}

	public RuntimeService getActivitiRuntimeService() {
		return activitiRuntimeService;
	}

	public void setActivitiRuntimeService(RuntimeService activitiRuntimeService) {
		this.activitiRuntimeService = activitiRuntimeService;
	}
}
