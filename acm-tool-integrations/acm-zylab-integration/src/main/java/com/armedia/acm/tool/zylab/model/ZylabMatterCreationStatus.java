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

import com.armedia.acm.data.converter.LocalDateTimeConverter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "acm_zylab_matter_status")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = ZylabMatterCreationStatus.class)
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class ZylabMatterCreationStatus
{
    @Id
    @TableGenerator(name = "acm_zylab_matter_status_gen", table = "acm_zylab_matter_status_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_zylab_matter_status", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_zylab_matter_status_gen")
    @Column(name = "cm_zylab_matter_status_id")
    private Long id;

    @Column(name = "cm_matter_name", unique = true, nullable = false)
    private String matterName;

    @Column(name = "cm_zylab_id", unique = true)
    private Long zylabId;

    @Column(name = "cm_last_updated", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime lastUpdated;

    @Column(name = "cm_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ZylabMatterStatus status;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getMatterName()
    {
        return matterName;
    }

    public void setMatterName(String matterName)
    {
        this.matterName = matterName;
    }

    public Long getZylabId() {
        return zylabId;
    }

    public void setZylabId(Long zylabId) {
        this.zylabId = zylabId;
    }

    public LocalDateTime getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime modified)
    {
        this.lastUpdated = modified;
    }

    public ZylabMatterStatus getStatus()
    {
        return status;
    }

    public void setStatus(ZylabMatterStatus status)
    {
        this.status = status;
    }

}
