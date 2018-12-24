package com.armedia.acm.plugins.businessprocess.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.data.converter.BooleanToStringConverter;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "acm_business_process")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class BusinessProcess implements AcmEntity, AcmContainerEntity, AcmObject, AcmAssignedObject, AcmStatefulEntity {
    
    @Id
    @TableGenerator(name = "business_process_gen", table = "acm_business_process_id", pkColumnName = "cm_seq_name", 
            valueColumnName = "cm_seq_num", pkColumnValue = "acm_business_process", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "business_process_gen")
    @Column(name = "cm_business_process_id")
    Long id;
    
    @Column(name = "cm_business_process_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_business_process_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_business_process_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_business_process_modifier")
    private String modifier;

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType = BusinessProcessConstants.OBJECT_TYPE;

    @OneToOne
    @JoinColumn(name = "cm_container_id")
    private AcmContainer container;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({ @JoinColumn(name = "cm_object_id"),
            @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type") })
    private List<AcmParticipant> participants = new ArrayList<>();

    @Column(name = "cm_business_process_restricted_flag", nullable = false)
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean restricted = Boolean.FALSE;

    @Column(name = "cm_business_process_status", nullable = false)
    private String status;


    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String getModifier() {
        return modifier;
    }

    @Override
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
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
    public AcmContainer getContainer() {
        return container;
    }

    @Override
    public void setContainer(AcmContainer container) {
        this.container = container;
    }

    @Override
    public String getObjectType() {
        return objectType;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public List<AcmParticipant> getParticipants() {
        return participants;
    }

    @Override
    public void setParticipants(List<AcmParticipant> newParticipants) {
        this.participants = newParticipants;
    }

    @Override
    public Boolean getRestricted() {
        return restricted;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    @Override
    public String getStatus() {
        return "DRAFT";
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }
}
