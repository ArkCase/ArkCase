package com.armedia.acm.services.dataupdate.model;

/*-
 * #%L
 * ACM Service: Data Update Service
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

import com.armedia.acm.data.converter.LocalDateConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "acm_data_update_executor")
public class AcmDataUpdateExecutorLog
{
    @Id
    @Column(name = "cm_executor_id")
    private String executorId;

    @Column(name = "cm_executed_on")
    @Convert(converter = LocalDateConverter.class)
    private LocalDate executedOn;

    public String getExecutorId()
    {
        return executorId;
    }

    public void setExecutorId(String executorId)
    {
        this.executorId = executorId;
    }

    public LocalDate getExecutedOn()
    {
        return executedOn;
    }

    public void setExecutedOn(LocalDate executedOn)
    {
        this.executedOn = executedOn;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AcmDataUpdateExecutorLog that = (AcmDataUpdateExecutorLog) o;
        return Objects.equals(executorId, that.executorId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(executorId);
    }
}
