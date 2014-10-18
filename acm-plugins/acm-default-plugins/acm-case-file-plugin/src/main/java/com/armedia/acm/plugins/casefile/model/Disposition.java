package com.armedia.acm.plugins.casefile.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.plugins.addressable.model.ContactMethod;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import java.util.Date;

@Entity
@Table(name="acm_disposition")
public class Disposition implements Serializable, AcmObject
{
    private static final long serialVersionUID = 7786267451369775524L;

    @Id
    @Column(name = "cm_disposition_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cm_close_date")
    @Temporal(TemporalType.DATE)
    private Date closeDate;

    @Column(name = "cm_disposition_type")
    private String dispositionType;

    @Column(name = "cm_refer_ext_org_name")
    private String referExternalOrganizationName;

    @Column(name = "cm_refer_ext_person_name")
    private String referExternalContactPersonName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_refer_ext_contact_method_id")
    private ContactMethod referExternalContactMethod;

    @Column(name = "cm_existing_case_number")
    private String existingCaseNumber;

    @Column(name = "cm_disposition_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_disposition_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_disposition_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_disposition_modifier")
    private String modifier;

    @Override
    public String getObjectType()
    {
        return "Disposition";
    }

    @PrePersist
    public void beforeInsert()
    {
        Date today = new Date();
        setCreated(today);

        setModified(today);

        if ( getReferExternalContactMethod() != null )
        {
            getReferExternalContactMethod().setCreated(today);
            getReferExternalContactMethod().setModified(today);
        }
    }

    @PreUpdate
    public void beforeUpdate()
    {
        Date today = new Date();
        setModified(today);

        if ( getReferExternalContactMethod() != null )
        {
            getReferExternalContactMethod().setModified(today);
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

    public Date getCloseDate()
    {
        return closeDate;
    }

    public void setCloseDate(Date closeDate)
    {
        this.closeDate = closeDate;
    }

    public String getDispositionType()
    {
        return dispositionType;
    }

    public void setDispositionType(String dispositionType)
    {
        this.dispositionType = dispositionType;
    }

    public String getReferExternalOrganizationName()
    {
        return referExternalOrganizationName;
    }

    public void setReferExternalOrganizationName(String referExternalOrganizationName)
    {
        this.referExternalOrganizationName = referExternalOrganizationName;
    }

    public String getReferExternalContactPersonName()
    {
        return referExternalContactPersonName;
    }

    public void setReferExternalContactPersonName(String referExternalContactPersonName)
    {
        this.referExternalContactPersonName = referExternalContactPersonName;
    }

    public ContactMethod getReferExternalContactMethod()
    {
        return referExternalContactMethod;
    }

    public void setReferExternalContactMethod(ContactMethod referExternalContactMethod)
    {
        this.referExternalContactMethod = referExternalContactMethod;
    }

    public String getExistingCaseNumber()
    {
        return existingCaseNumber;
    }

    public void setExistingCaseNumber(String existingCaseNumber)
    {
        this.existingCaseNumber = existingCaseNumber;
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
}
