package com.armedia.acm.plugins.person.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by marjan.stefanoski on 09.12.2014.
 */
@Entity
@Table(name = "acm_identification")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Identification implements Serializable, AcmEntity, AcmObject
{

    private static final long serialVersionUID = 3413715007864370940L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @TableGenerator(name = "acm_identification_gen", table = "acm_identification_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_identification", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_identification_gen")
    @Column(name = "cm_identification_id")
    private Long identificationID;

    @Column(name = "cm_id_type")
    private String identificationType;

    @Column(name = "cm_id_number")
    private String identificationNumber;

    @Column(name = "cm_id_issuer")
    private String identificationIssuer;

    @Column(name = "cm_year_issued")
    @Temporal(TemporalType.TIMESTAMP)
    private Date identificationYearIssued;

    @Column(name = "cm_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_modifier")
    private String modifier;

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

    public Long getIdentificationID()
    {
        return identificationID;
    }

    public void setIdentificationID(Long identificationID)
    {
        this.identificationID = identificationID;
    }

    public String getIdentificationType()
    {
        return identificationType;
    }

    public void setIdentificationType(String identificationType)
    {
        this.identificationType = identificationType;
    }

    public String getIdentificationNumber()
    {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber)
    {
        this.identificationNumber = identificationNumber;
    }

    public String getIdentificationIssuer()
    {
        return identificationIssuer;
    }

    public void setIdentificationIssuer(String identificationIssuer)
    {
        this.identificationIssuer = identificationIssuer;
    }

    public Date getIdentificationYearIssued()
    {
        return identificationYearIssued;
    }

    public void setIdentificationYearIssued(Date identificationYearIssued)
    {
        this.identificationYearIssued = identificationYearIssued;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof Identification))
        {
            return false;
        }
        return getIdentificationID() == ((Identification) obj).getIdentificationID();
    }

    @Override
    @JsonIgnore
    public String getObjectType()
    {
        return IdentificationConstants.OBJECT_TYPE;
    }

    @Override
    @JsonIgnore
    public Long getId()
    {
        return getIdentificationID();
    }
}
