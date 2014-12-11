package com.armedia.acm.plugins.person.model;

import com.armedia.acm.data.AcmEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by marjan.stefanoski on 09.12.2014.
 */
@Entity
@Table(name = "acm_person_identification")
public class PersonIdentification  implements Serializable, AcmEntity {

    private static final long serialVersionUID = 3413715007864370940L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_person_identification_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personIdentificationID;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name="cm_person", nullable = false)
    private Person person;

    @Column(name = "cm_id_type")
    private String identificationType;

    @Column(name = "cm_id_number")
    private String identificationNumber;

    @Column(name = "cm_id_issuer")
    private String identificationIssuer;

    @Column(name = "cm_year_issued")
    @Temporal(TemporalType.TIMESTAMP)
    private Date identificationYearIssued;

    @Column(name = "cm_person_id_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_person_id_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_person_id_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_person_id_modifier")
    private String modifier;

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public Date getModified() {
        return modified;
    }

    @Override
    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public String getModifier() {
        return modifier;
    }

    @Override
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Long getPersonIdentificationID() {
        return personIdentificationID;
    }

    public void setPersonIdentificationID(Long personIdentificationID) {
        this.personIdentificationID = personIdentificationID;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getIdentificationIssuer() {
        return identificationIssuer;
    }

    public void setIdentificationIssuer(String identificationIssuer) {
        this.identificationIssuer = identificationIssuer;
    }

    public Date getIdentificationYearIssued() {
        return identificationYearIssued;
    }

    public void setIdentificationYearIssued(Date identificationYearIssued) {
        this.identificationYearIssued = identificationYearIssued;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
