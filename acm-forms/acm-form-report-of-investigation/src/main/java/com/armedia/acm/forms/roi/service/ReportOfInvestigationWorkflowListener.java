/**
 * 
 */
package com.armedia.acm.forms.roi.service;

import com.armedia.acm.form.config.Item;
import com.armedia.acm.forms.roi.model.ReportOfInvestigationFormEvent;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import com.armedia.acm.plugins.task.model.TaskConstants;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
public class ReportOfInvestigationWorkflowListener implements ApplicationListener<ReportOfInvestigationFormEvent> {

	
	private FileWorkflowBusinessRule fileWorkflowBusinessRule;

    private RuntimeService activitiRuntimeService;

    private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Override
	public void onApplicationEvent(ReportOfInvestigationFormEvent event)
	{
		handleReportOfInvestigation(event);

	}
	
	protected void handleReportOfInvestigation(ReportOfInvestigationFormEvent event)
	{
		EcmFile pdfRendition = event.getFrevvoUploadedFiles().getPdfRendition();
		EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();
		
		configuration.setEcmFile(pdfRendition);
		
		LOG.debug("Calling business rules");
		
		configuration = getFileWorkflowBusinessRule().applyRules(configuration);
		
		LOG.debug("Start process? " + configuration.isStartProcess());
		
		if ( configuration.isStartProcess() )
        {
            startBusinessProcess(event, configuration);
        }
	}
	
	 private void startBusinessProcess(ReportOfInvestigationFormEvent event, EcmFileWorkflowConfiguration configuration)
	 {
		 String processName = configuration.getProcessName();
		 
		 String author = event.getUserId();
	     List<String> reviewers = findReviewers(event);
	     
	     String taskName = "ROI for " + event.getForObjectType() + " '" + event.getForObjectNumber() + "'";
	     
	     Map<String, Object> pvars = new HashMap<>();
	     
	     pvars.put(TaskConstants.VARIABLE_NAME_REVIEWERS, reviewers);
	     pvars.put(TaskConstants.VARIABLE_NAME_TASK_NAME, taskName);
	     pvars.put(TaskConstants.VARIABLE_NAME_DOC_AUTHOR, author);
	     pvars.put(TaskConstants.VARIABLE_NAME_PDF_RENDITION_ID, event.getFrevvoUploadedFiles().getPdfRendition().getFileId());
	     pvars.put(TaskConstants.VARIABLE_NAME_XML_RENDITION_ID, event.getFrevvoUploadedFiles().getFormXml().getFileId());
	     
	     pvars.put(TaskConstants.VARIABLE_NAME_OBJECT_TYPE, EcmFileConstants.OBJECT_FILE_TYPE);
	     pvars.put(TaskConstants.VARIABLE_NAME_OBJECT_ID, event.getFrevvoUploadedFiles().getPdfRendition().getId());
	     pvars.put(TaskConstants.VARIABLE_NAME_OBJECT_NAME, event.getFrevvoUploadedFiles().getPdfRendition().getFileName());
	     pvars.put(EcmFileConstants.OBJECT_FILE_TYPE, event.getFrevvoUploadedFiles().getPdfRendition().getId());
	     pvars.put(TaskConstants.VARIABLE_NAME_REQUEST_TYPE, "ROI");
	     pvars.put(TaskConstants.VARIABLE_NAME_REQUEST_ID, event.getObjectId());

		 pvars.put(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_TYPE, event.getParentObjectType());
		 pvars.put(TaskConstants.VARIABLE_NAME_PARENT_OBJECT_ID, event.getParentObjectId());
	     
	     LOG.debug("Starting process: " + processName);

	     ProcessInstance pi = getActivitiRuntimeService().startProcessInstanceByKey(processName, pvars);

	     LOG.debug("Process ID: " + pi.getId());
	 }
	 
	 private List<String> findReviewers(ReportOfInvestigationFormEvent event)
	 {
		 List<String> approvers = new ArrayList<>();
		 
		 for ( Item approver : event.getRequest().getApprovers() )
		 {
			 approvers.add(approver.getValue());
		 }
		 
		 return approvers;
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
