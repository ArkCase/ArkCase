package com.armedia.acm.services.signature.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "acm_signature")
public class Signature {
    @Id
    @TableGenerator(name = "acm_signature_gen",
            table = "acm_signature_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_signature",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_signature_gen")
    @Column(name = "cm_signature_id")
	private Long signatureId;

    @Column(name = "cm_object_id", nullable = false, insertable = true, updatable = false)
    private Long objectId;
    
    @Column(name = "cm_object_type", nullable = false, insertable = true, updatable = false)
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
