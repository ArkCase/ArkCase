package com.armedia.acm.services.sequence.model;

import com.armedia.acm.data.converter.BooleanToStringConverter;

/*-
 * #%L
 * ACM Service: Sequence Manager
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import java.io.Serializable;

/**
 * @author sasko.tanaskoski
 *
 */

@Entity
@IdClass(AcmSequenceRegistryId.class)
@Table(name = "acm_sequence_registry")
public class AcmSequenceRegistry implements Serializable
{

    private static final long serialVersionUID = 4982213307060399243L;

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

    @Column(name = "cm_sequence_part_value_used_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean sequencePartValueUsedFlag;

    /**
     * @return the sequenceValue
     */
    public String getSequenceValue()
    {
        return sequenceValue;
    }

    /**
     * @param sequenceValue
     *            the sequenceValue to set
     */
    public void setSequenceValue(String sequenceValue)
    {
        this.sequenceValue = sequenceValue;
    }

    /**
     * @return the sequenceName
     */
    public String getSequenceName()
    {
        return sequenceName;
    }

    /**
     * @param sequenceName
     *            the sequenceName to set
     */
    public void setSequenceName(String sequenceName)
    {
        this.sequenceName = sequenceName;
    }

    /**
     * @return the sequencePartName
     */
    public String getSequencePartName()
    {
        return sequencePartName;
    }

    /**
     * @param sequencePartName
     *            the sequencePartName to set
     */
    public void setSequencePartName(String sequencePartName)
    {
        this.sequencePartName = sequencePartName;
    }

    /**
     * @return the sequencePartValue
     */
    public Long getSequencePartValue()
    {
        return sequencePartValue;
    }

    /**
     * @param sequencePartValue
     *            the sequencePartValue to set
     */
    public void setSequencePartValue(Long sequencePartValue)
    {
        this.sequencePartValue = sequencePartValue;
    }

    /**
     * @return the sequencePartValueUsedFlag
     */
    public Boolean getSequencePartValueUsedFlag()
    {
        return sequencePartValueUsedFlag;
    }

    /**
     * @param sequencePartValueUsedFlag
     *            the sequencePartValueUsedFlag to set
     */
    public void setSequencePartValueUsedFlag(Boolean sequencePartValueUsedFlag)
    {
        this.sequencePartValueUsedFlag = sequencePartValueUsedFlag;
    }

}
