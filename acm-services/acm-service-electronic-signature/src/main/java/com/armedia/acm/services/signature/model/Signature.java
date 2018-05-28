package com.armedia.acm.services.signature.model;

/*-
 * #%L
 * ACM Service: Electronic Signature
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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Date;

@Entity
@Table(name = "acm_signature")
public class Signature
{
    @Id
    @TableGenerator(name = "acm_signature_gen", table = "acm_signature_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_signature", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_signature_gen")
    @Column(name = "cm_signature_id")
    private Long signatureId;

    @Column(name = "cm_object_id", nullable = false, insertable = true, updatable = false)
    private Long objectId;

    @Column(name = "cm_object_type", nullable = false, insertable = true, updatable = false)
    private String objectType;

    @Column(name = "cm_signature_datetime", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date signedDate;

    @Column(name = "cm_signature_user", nullable = false, insertable = true, updatable = true)
    private String signedBy;

    // TODO private String digitalSignature;

    @PrePersist
    protected void beforeInsert()
    {
        setSignedDate(new Date());
    }

    public Long getSignatureId()
    {
        return signatureId;
    }

    void setSignatureId(Long signatureId)
    {
        this.signatureId = signatureId;
    }

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public Date getSignedDate()
    {
        return signedDate;
    }

    void setSignedDate(Date signedDate)
    {
        this.signedDate = signedDate;
    }

    public String getSignedBy()
    {
        return signedBy;
    }

    public void setSignedBy(String signedBy)
    {
        this.signedBy = signedBy;
    }
}
