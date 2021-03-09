package com.armedia.acm.tool.zylab.model;

/*-
 * #%L
 * Tool Integrations: Arkcase ZyLAB Integration
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import java.time.LocalDateTime;

import javax.persistence.Convert;

import org.springframework.format.annotation.DateTimeFormat;

import com.armedia.acm.data.converter.LocalDateTimeConverter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on February, 2021
 */
public class MatterDTO
{
    @JsonProperty("Id")
    private long id;

    @JsonProperty("Name")
    private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Convert(converter = LocalDateTimeConverter.class)
    @JsonProperty("Date")
    private LocalDateTime date;

    @JsonProperty("CanBeDeleted")
    private boolean canBeDeleted;

    @JsonProperty("State")
    private int state;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public LocalDateTime getDate()
    {
        return date;
    }

    public void setDate(LocalDateTime date)
    {
        this.date = date;
    }

    public boolean isCanBeDeleted()
    {
        return canBeDeleted;
    }

    public void setCanBeDeleted(boolean canBeDeleted)
    {
        this.canBeDeleted = canBeDeleted;
    }

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }

    @Override
    public String toString()
    {
        return "MatterDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", canBeDeleted=" + canBeDeleted +
                ", state=" + state +
                '}';
    }
}
