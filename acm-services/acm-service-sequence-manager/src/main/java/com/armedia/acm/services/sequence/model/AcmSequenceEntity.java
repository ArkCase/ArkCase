package com.armedia.acm.services.sequence.model;

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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Version;

import java.io.Serializable;

/**
 * @author sasko.tanaskoski
 *
 */
@Entity
@IdClass(AcmSequenceEntityId.class)
@Table(name = "acm_sequence")
public class AcmSequenceEntity implements Serializable
{

    private static final long serialVersionUID = 4573350572913494010L;

    @Id
    @Column(name = "cm_sequence_name")
    private String sequenceName;

    @Id
    @Column(name = "cm_sequence_part_name")
    private String sequencePartName;

    @Column(name = "cm_sequence_part_value")
    private Integer sequencePartValue;

    @Version
    @Column(name = "cm_sequence_part_lock")
    private Integer sequencePartLock;

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
    public Integer getSequencePartValue()
    {
        return sequencePartValue;
    }

    /**
     * @param sequencePartValue
     *            the sequencePartValue to set
     */
    public void setSequencePartValue(Integer sequencePartValue)
    {
        this.sequencePartValue = sequencePartValue;
    }

    /**
     * @return the sequencePartLock
     */
    public Integer getSequencePartLock()
    {
        return sequencePartLock;
    }

    /**
     * @param sequencePartLock
     *            the sequencePartLock to set
     */
    public void setSequencePartLock(Integer sequencePartLock)
    {
        this.sequencePartLock = sequencePartLock;
    }

}
