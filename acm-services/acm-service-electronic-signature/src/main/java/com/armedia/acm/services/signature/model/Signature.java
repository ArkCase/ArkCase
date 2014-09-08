package com.armedia.acm.services.signature.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "acm_signature")
public class Signature {
    @Id
    @Column(name = "cm_signature_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long signatureId;
    
    @Column(name = "cm_object_id", nullable = false, insertable = true, updatable = true)
    private Long objectId;
    
    @Column(name = "cm_object_type", nullable = false, insertable = true, updatable = true)
    private String objectType;
    
    @Column(name = "cm_signature_datetime", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date signedDate;
    
    @Column(name = "cm_signature_user", nullable = false, insertable = true, updatable = true)
    private String signedBy;
    
    //TODO private String digitalSignature;
    
    @PrePersist
    protected void beforeInsert()
    {
        setSignedDate(new Date());
    }
    
	public Long getSignatureId() {
		return signatureId;
	}
	void setSignatureId(Long signatureId) {
		this.signatureId = signatureId;
	}
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
	void setSignedDate(Date signedDate) {
		this.signedDate = signedDate;
	}
	public String getSignedBy() {
		return signedBy;
	}
	public void setSignedBy(String signedBy) {
		this.signedBy = signedBy;
	}
}
