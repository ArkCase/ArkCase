package com.armedia.acm.plugins.complaint.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.plugins.casefile.model.Disposition;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="acm_close_complaint_request")
public class CloseComplaintRequest implements Serializable, AcmObject
{
    private static final long serialVersionUID = -6389711968453289552L;

    @Id
    @Column(name = "cm_close_complaint_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cm_complaint_id")
    private Long complaintId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_disposition_id")
    private Disposition disposition;

    @Column(name = "cm_close_complaint_status")
    private String status = "IN APPROVAL";

    @ElementCollection
    @CollectionTable(
            name = "acm_form_approver",
            joinColumns = @JoinColumn(name = "cm_parent_id", referencedColumnName = "cm_close_complaint_id")
    )
    @Column(name = "cm_approver_user_id")
    private List<String> approvers = new ArrayList<>();

    @Column(name = "cm_close_complaint_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_close_complaint_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_close_complaint_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_close_complaint_modifier")
    private String modifier;

    @PrePersist
    public void beforeInsert()
    {
        Date today = new Date();
        setCreated(today);
        setModified(today);

        if ( getDisposition() != null )
        {
            getDisposition().setCreated(today);
            getDisposition().setModified(today);

            if ( getDisposition().getReferExternalContactMethod() != null )
            {
                getDisposition().getReferExternalContactMethod().setCreated(today);
                getDisposition().getReferExternalContactMethod().setModified(today);
            }
        }
    }

    @PreUpdate
    public void beforeUpdate()
    {
        Date today = new Date();
        setModified(today);

        if ( getDisposition() != null )
        {
            getDisposition().setModified(today);

            if ( getDisposition().getReferExternalContactMethod() != null )
            {
                getDisposition().getReferExternalContactMethod().setModified(today);
            }
        }
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getComplaintId()
    {
        return complaintId;
    }

    public void setComplaintId(Long complaintId)
    {
        this.complaintId = complaintId;
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

    public List<String> getApprovers()
    {
        return approvers;
    }

    public void setApprovers(List<String> approvers)
    {
        this.approvers = approvers;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public String getModifier()
    {
        return modifier;
    }

    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    @Override
    public String getObjectType()
    {
        return "CloseComplaintRequest";
    }
}
