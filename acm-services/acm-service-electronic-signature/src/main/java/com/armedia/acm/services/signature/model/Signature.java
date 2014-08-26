package com.armedia.acm.services.signature.model;

import java.util.Date;

public class Signature {
    private Long objectId;
    private String objectType;
    private Date signedDate;
    private String signedBy; // TODO need to support a list
    //TODO private String digitalSignature;
    
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public Date getSignedDate() {
		return signedDate;
	}
	public void setSignedDate(Date signedDate) {
		this.signedDate = signedDate;
	}
	public String getSignedBy() {
		return signedBy;
	}
	public void setSignedBy(String signedBy) {
		this.signedBy = signedBy;
	}
}
