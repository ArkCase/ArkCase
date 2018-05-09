package com.armedia.acm.service.outlook.model;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 8, 2017
 *
 */
@Entity
@Table(name = "acm_outlook_object_reference", uniqueConstraints = @UniqueConstraint(columnNames = { "cm_object_type", "cm_object_id" }))
public class AcmOutlookObjectReference
{

    @Id
    @TableGenerator(name = "outlook_object_reference_gen", table = "acm_outlook_object_reference_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_outlook_object_reference", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "outlook_object_reference_gen")
    @Column(name = "cm_outlook_object_reference_id")
    private Long id;

    @Column(name = "cm_object_type")
    private String objectType;

    @Column(name = "cm_object_id")
    private Long objectId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cm_outlook_folder_creator_id")
    private AcmOutlookFolderCreator folderCreator;

    /**
     * @return the id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return the objectType
     */
    public String getObjectType()
    {
        return objectType;
    }

    /**
     * @param objectType
     *            the objectType to set
     */
    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    /**
     * @return the objectId
     */
    public Long getObjectId()
    {
        return objectId;
    }

    /**
     * @param objectId
     *            the objectId to set
     */
    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    /**
     * @return the folderCreator
     */
    public AcmOutlookFolderCreator getFolderCreator()
    {
        return folderCreator;
    }

    /**
     * @param folderCreator
     *            the folderCreator to set
     */
    public void setFolderCreator(AcmOutlookFolderCreator folderCreator)
    {
        this.folderCreator = folderCreator;
    }

}
