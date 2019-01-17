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

import com.armedia.acm.data.converter.BooleanToStringConverter;
import com.armedia.acm.data.converter.LocalDateTimeConverter;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import java.time.LocalDateTime;

/**
 * @author sasko.tanaskoski
 *
 */

@Entity
@IdClass(AcmSequenceResetId.class)
@Table(name = "acm_sequence_reset")
public class AcmSequenceReset
{

    @Id
    @Column(name = "cm_sequence_name")
    private String sequenceName;

    @Id
    @Column(name = "cm_sequence_part_name")
    private String sequencePartName;

    @Id
    @Column(name = "cm_reset_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime resetDate;

    @Column(name = "cm_reset_executed_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime resetExecutedDate;

    @Column(name = "cm_reset_executed_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean resetExecutedFlag;

    @Column(name = "cm_reset_repeatable_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean resetRepeatableFlag;

    @Column(name = "cm_reset_repeatable_period")
    private Integer resetRepeatablePeriod;

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
     * @return the resetDate
     */
    public LocalDateTime getResetDate()
    {
        return resetDate;
    }

    /**
     * @param resetDate
     *            the resetDate to set
     */
    public void setResetDate(LocalDateTime resetDate)
    {
        this.resetDate = resetDate;
    }

    /**
     * @return the resetExecutedDate
     */
    public LocalDateTime getResetExecutedDate()
    {
        return resetExecutedDate;
    }

    /**
     * @param resetExecutedDate
     *            the resetExecutedDate to set
     */
    public void setResetExecutedDate(LocalDateTime resetExecutedDate)
    {
        this.resetExecutedDate = resetExecutedDate;
    }

    /**
     * @return the resetExecutedFlag
     */
    public Boolean getResetExecutedFlag()
    {
        return resetExecutedFlag;
    }

    /**
     * @param resetExecutedFlag
     *            the resetExecutedFlag to set
     */
    public void setResetExecutedFlag(Boolean resetExecutedFlag)
    {
        this.resetExecutedFlag = resetExecutedFlag;
    }

    /**
     * @return the resetRepeatableFlag
     */
    public Boolean getResetRepeatableFlag()
    {
        return resetRepeatableFlag;
    }

    /**
     * @param resetRepeatableFlag
     *            the resetRepeatableFlag to set
     */
    public void setResetRepeatableFlag(Boolean resetRepeatableFlag)
    {
        this.resetRepeatableFlag = resetRepeatableFlag;
    }

    /**
     * @return the resetRepeatablePeriod
     */
    public Integer getResetRepeatablePeriod()
    {
        return resetRepeatablePeriod;
    }

    /**
     * @param resetRepeatablePeriod
     *            the resetRepeatablePeriod to set
     */
    public void setResetRepeatablePeriod(Integer resetRepeatablePeriod)
    {
        this.resetRepeatablePeriod = resetRepeatablePeriod;
    }

}
