package com.armedia.acm.plugins.casefile.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.data.converter.LocalDateConverter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "acm_disposition")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = Disposition.class)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.plugins.casefile.model.Disposition")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Disposition implements Serializable, AcmObject, AcmEntity
{
    private static final long serialVersionUID = 7786267451369775524L;

    @Id
    @TableGenerator(name = "disposition_gen",
            table = "acm_disposition_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_disposition",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "disposition_gen")
    @Column(name = "cm_disposition_id")
    private Long id;

    @Column(name = "cm_close_date")
    @Convert(converter = LocalDateConverter.class)
    private LocalDate closeDate;

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

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    @Override
    public String getObjectType()
    {
        return CaseFileConstants.OBJECT_TYPE_DISPOSITION;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public LocalDate getCloseDate()
    {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate)
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

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }
}
