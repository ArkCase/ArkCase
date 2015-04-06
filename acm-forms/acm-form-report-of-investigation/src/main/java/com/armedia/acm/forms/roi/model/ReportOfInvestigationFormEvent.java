/**
 * 
 */
package com.armedia.acm.forms.roi.model;

import com.armedia.acm.event.AcmEvent;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class ReportOfInvestigationFormEvent extends AcmEvent {


	private static final long serialVersionUID = 1192631656494031812L;
	private ROIForm request;
	private FrevvoUploadedFiles frevvoUploadedFiles;
    private String mode;
	private String forObjectType;
	private String forObjectNumber;
	private Object parentObjectId;
	private String parentObjectType;

	public ReportOfInvestigationFormEvent(String forObjectType, String forObjectNumber, String parentObjectType, Long parentObjectId, ROIForm source,
										  FrevvoUploadedFiles files, String userId, String ipAddress, boolean succeeded)
	{
		super(source);

		setMode(mode);
		setUserId(userId);
		setEventDate(new Date());
		
		String event = "created";
		setEventType("com.armedia.acm.reportOfInvestigation." + event);
		
		setIpAddress(ipAddress);
        setObjectId(files.getPdfRendition().getId());
        setObjectType(EcmFileConstants.OBJECT_FILE_TYPE);
        
        setSucceeded(succeeded);

        setRequest(source);
        setFrevvoUploadedFiles(files);

		setForObjectNumber(forObjectNumber);
		setForObjectType(forObjectType);

		setParentObjectType(parentObjectType);
		setParentObjectId(parentObjectId);

	}

	public ROIForm getRequest() {
		return request;
	}

	public void setRequest(ROIForm request) {
		this.request = request;
	}

	public FrevvoUploadedFiles getFrevvoUploadedFiles() {
		return frevvoUploadedFiles;
	}

	public void setFrevvoUploadedFiles(FrevvoUploadedFiles frevvoUploadedFiles) {
		this.frevvoUploadedFiles = frevvoUploadedFiles;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getForObjectType()
	{
		return forObjectType;
	}

	public void setForObjectType(String forObjectType)
	{
		this.forObjectType = forObjectType;
	}

	public String getForObjectNumber()
	{
		return forObjectNumber;
	}

	public void setForObjectNumber(String forObjectNumber)
	{
		this.forObjectNumber = forObjectNumber;
	}

	public Object getParentObjectId()
	{
		return parentObjectId;
	}

	public void setParentObjectId(Object parentObjectId)
	{
		this.parentObjectId = parentObjectId;
	}

	public String getParentObjectType()
	{
		return parentObjectType;
	}

	public void setParentObjectType(String parentObjectType)
	{
		this.parentObjectType = parentObjectType;
	}
}
