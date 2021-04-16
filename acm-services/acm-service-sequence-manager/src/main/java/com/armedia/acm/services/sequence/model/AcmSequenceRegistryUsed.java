package com.armedia.acm.services.sequence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@IdClass(AcmSequenceRegistryId.class)
@Table(name = "acm_sequence_registry_used")
public class AcmSequenceRegistryUsed implements Serializable
{
    private static final long serialVersionUID = -2497132561629649750L;

    @Id
    @Column(name = "cm_sequence_value")
    private String sequenceValue;

    @Id
    @Column(name = "cm_sequence_name")
    private String sequenceName;

    @Id
    @Column(name = "cm_sequence_part_name")
    private String sequencePartName;

    @Column(name = "cm_sequence_part_value")
    private Long sequencePartValue;

    public String getSequenceValue() {
        return sequenceValue;
    }

    public void setSequenceValue(String sequenceValue) {
        this.sequenceValue = sequenceValue;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public String getSequencePartName() {
        return sequencePartName;
    }

    public void setSequencePartName(String sequencePartName) {
        this.sequencePartName = sequencePartName;
    }

    public Long getSequencePartValue() {
        return sequencePartValue;
    }

    public void setSequencePartValue(Long sequencePartValue) {
        this.sequencePartValue = sequencePartValue;
    }
}
