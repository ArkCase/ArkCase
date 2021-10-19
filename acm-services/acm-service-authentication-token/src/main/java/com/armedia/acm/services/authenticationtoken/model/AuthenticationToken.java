package com.armedia.acm.services.authenticationtoken.model;

/*-
 * #%L
 * ACM Service: Authentication Tokens
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

import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "acm_authentication_token")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AuthenticationToken implements Serializable, AcmEntity

{
    private static final long serialVersionUID = -1154137631399833851L;

    @Id
    @TableGenerator(name = "acm_authentication_token_gen", table = "acm_authentication_token_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_authentication_token", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_authentication_token_gen")
    @Column(name = "cm_authentication_token_id")
    private Long id;

    @Column(name = "cm_authentication_token_creator", nullable = false, insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_authentication_token_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_authentication_token_modified", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_authentication_token_modifier", nullable = false, insertable = true, updatable = true)
    private String modifier;

    @Column(name = "cm_authentication_token_key", nullable = false, insertable = true, updatable = false)
    private String key;

    @Column(name = "cm_authentication_token_email", nullable = false, insertable = true, updatable = false)
    private String email;

    @Column(name = "cm_authentication_token_password", nullable = true, insertable = true, updatable = false)
    private String password;

    @Column(name = "cm_authentication_token_status", nullable = true, insertable = true, updatable = true)
    private String status;

    @Column(name = "cm_authentication_token_relative_path")
    private String relativePath;

    @Column(name = "cm_authentication_token_generic_path")
    private String genericPath;

    @Column(name = "cm_authentication_token_expiry_milliseconds")
    private Long tokenExpiry;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getRelativePath()
    {
        return relativePath;
    }

    public void setRelativePath(String relativePath)
    {
        this.relativePath = relativePath;
    }

    public String getGenericPath()
    {
        return genericPath;
    }

    public void setGenericPath(String genericPath)
    {
        this.genericPath = genericPath;
    }

    public Long getTokenExpiry()
    {
        return tokenExpiry;
    }

    public void setTokenExpiry(Long tokenExpiry)
    {
        this.tokenExpiry = tokenExpiry;
    }

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
    public Date getModified()
    {
        return modified;
    }

    @Override
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    @JsonIgnore
    public boolean isActive()
    {
        return status.equals("ACTIVE");
    }
}
