/**
 * 
 */
package com.armedia.acm.services.costsheet.model;

import java.util.Date;

import com.armedia.acm.event.AcmEvent;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;

/**
 * @author riste.tutureski
 *
 */
public class AcmCostsheetEvent extends AcmEvent {

	private static final long serialVersionUID = 7323464693900974967L;
	
	private FrevvoUploadedFiles frevvoUploadedFiles;
	private boolean startWorkflow;
	
	public AcmCostsheetEvent(AcmCostsheet source, String userId, String ipAddress, boolean succeeded, String type, FrevvoUploadedFiles frevvoUploadedFiles, boolean startWorkflow) 
	{
		super(source);

		setObjectId(source.getId());
		setObjectType(CostsheetConstants.OBJECT_TYPE);
		setUserId(userId);
		setIpAddress(ipAddress);
		setSucceeded(succeeded);
		setEventDate(new Date());
		setEventType(CostsheetConstants.EVENT_TYPE + "." + type);
		
		setFrevvoUploadedFiles(frevvoUploadedFiles);
		setStartWorkflow(startWorkflow);
	}

	public FrevvoUploadedFiles getFrevvoUploadedFiles() {
		return frevvoUploadedFiles;
	}

	public void setFrevvoUploadedFiles(FrevvoUploadedFiles frevvoUploadedFiles) {
		this.frevvoUploadedFiles = frevvoUploadedFiles;
	}

	public boolean isStartWorkflow() {
		return startWorkflow;
	}

	public void setStartWorkflow(boolean startWorkflow) {
		this.startWorkflow = startWorkflow;
	}
}
