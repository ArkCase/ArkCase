/**
 * 
 */
package com.armedia.acm.plugins.casefile.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.services.users.model.AcmParticipant;

/**
 * @author riste.tutureski
 *
 */
@Entity
@Table(name="acm_close_case_request")
public class CloseCaseRequest implements Serializable, AcmObject, AcmEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @Column(name = "cm_close_case_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "cm_case_id")
    private Long caseId;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_disposition_id")
    private Disposition disposition;
	
	@Column(name = "cm_close_case_status")
    private String status = "IN APPROVAL";
	
	@OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_object_id")
    private List<AcmParticipant> participants = new ArrayList<>();
	
	@Column(name = "cm_close_case_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_close_case_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_close_case_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_close_case_modifier")
    private String modifier;
    
    @PrePersist
    public void beforeInsert()
    {
        setupChildPointers();
    }

    private void setupChildPointers()
    {
        for ( AcmParticipant ap : getParticipants() )
        {
            ap.setObjectId(getId());
            ap.setObjectType("CLOSE_CASE_REQUEST");
        }
    }
    
    @PreUpdate
    public void beforeUpdate()
    {
        setupChildPointers();
    }
    
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	public Disposition getDisposition()
    {
        return disposition;
    }

    public void setDisposition(Disposition disposition)
    {
        this.disposition = disposition;
    }
    
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public List<AcmParticipant> getParticipants()
    {
        return participants;
    }

    public void setParticipants(List<AcmParticipant> participants)
    {
        this.participants = participants;
    }
	
    @Override
    public Date getCreated()
    {
        return created;
    }

    @Override
    public void setCreated(Date created)
    {
        this.created = created;
    }

    @Override
    public String getCreator()
    {
        return creator;
    }

    @Override
    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    @Override
    public Date getModified()
    {
        return modified;
    }

    @Override
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    @Override
    public String getModifier()
    {
        return modifier;
    }

    @Override
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

	@Override
	public String getObjectType() 
	{
		return "CloseCaseRequest";
	}

}
