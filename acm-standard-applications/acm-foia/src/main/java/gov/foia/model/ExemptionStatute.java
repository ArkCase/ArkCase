package gov.foia.model;

/*-
 * #%L
 * ACM Service: Exemption
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.data.AcmEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "acm_exemption_statute")
public class ExemptionStatute implements Serializable, AcmEntity
{

    private static final long serialVersionUID = 8484945152180957284L;

    @Id
    @TableGenerator(name = "acm_exemption_statute_gen", table = "acm_exemption_statute_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_exemption_statute", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_exemption_statute_gen")
    @Column(name = "cm_exemption_statute_id")
    private Long id;

    @Column(name = "cm_parent_object_id")
    private Long parentObjectId;

    @Column(name = "cm_parent_object_type")
    private String parentObjectType;

    @Column(name = "cm_exemption_statute")
    private String exemptionStatute;

    @Column(name = "cm_exemption_status")
    private String exemptionStatus;

    @Column(name = "cm_exemption_created", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_exemption_creator", nullable = false, updatable = false)
    private String creator;

    @Column(name = "cm_exemption_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_exemption_modifier")
    private String modifier;

    @Column(name = "cm_file_id")
    private Long fileId;

    @Column(name = "cm_file_version")
    private String fileVersion;

    @Transient
    private List<String> exemptionStatutes;

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
    public String getModifier()
    {
        return modifier;
    }

    @Override
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

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
    public Date getModified()
    {
        return modified;
    }

    @Override
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getParentObjectId()
    {
        return parentObjectId;
    }

    public void setParentObjectId(Long parentObjectId)
    {
        this.parentObjectId = parentObjectId;
    }

    public String getParentObjectType()
    {
        return parentObjectType;
    }

    public void setParentObjectType(String parentObjectType)
    {
        this.parentObjectType = parentObjectType;
    }

    public String getExemptionStatute()
    {
        return exemptionStatute;
    }

    public void setExemptionStatute(String exemptionStatute)
    {
        this.exemptionStatute = exemptionStatute;
    }

    public String getExemptionStatus()
    {
        return exemptionStatus;
    }

    public void setExemptionStatus(String exemptionStatus)
    {
        this.exemptionStatus = exemptionStatus;
    }

    public Long getFileId()
    {
        return fileId;
    }

    public void setFileId(Long fileId)
    {
        this.fileId = fileId;
    }

    public String getFileVersion()
    {
        return fileVersion;
    }

    public void setFileVersion(String fileVersion)
    {
        this.fileVersion = fileVersion;
    }

    public List<String> getExemptionStatutes()
    {
        return exemptionStatutes;
    }

    public void setExemptionStatutes(List<String> exemptionStatutes)
    {
        this.exemptionStatutes = exemptionStatutes;
    }
}