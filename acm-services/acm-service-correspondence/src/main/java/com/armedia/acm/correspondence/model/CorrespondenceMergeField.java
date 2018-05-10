package com.armedia.acm.correspondence.model;

/*-
 * #%L
 * ACM Service: Correspondence Library
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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

/**
 * @author sasko.tanaskoski
 *
 */

/**
 * This POJO stores parameters for mergeField
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class CorrespondenceMergeField
{

    private String fieldId;

    private String fieldValue;

    private String fieldDescription;

    private String fieldType;

    private String fieldVersion;

    /**
     * @return the fieldId
     */
    public String getFieldId()
    {
        return fieldId;
    }

    /**
     * @param fieldId
     *            the fieldId to set
     */
    public void setFieldId(String fieldId)
    {
        this.fieldId = fieldId;
    }

    /**
     * @return the fieldValue
     */
    public String getFieldValue()
    {
        return fieldValue;
    }

    /**
     * @param fieldValue
     *            the fieldValue to set
     */
    public void setFieldValue(String fieldValue)
    {
        this.fieldValue = fieldValue;
    }

    /**
     * @return the fieldDescription
     */
    public String getFieldDescription()
    {
        return fieldDescription;
    }

    /**
     * @param fieldDescription
     *            the fieldDescription to set
     */
    public void setFieldDescription(String fieldDescription)
    {
        this.fieldDescription = fieldDescription;
    }

    /**
     * @return the fieldType
     */
    public String getFieldType()
    {
        return fieldType;
    }

    /**
     * @param fieldType
     *            the fieldType to set
     */
    public void setFieldType(String fieldType)
    {
        this.fieldType = fieldType;
    }

    /**
     * @return the fieldVersion
     */
    public String getFieldVersion()
    {
        return fieldVersion;
    }

    /**
     * @param fieldVersion
     *            the fieldVersion to set
     */
    public void setFieldVersion(String fieldVersion)
    {
        this.fieldVersion = fieldVersion;
    }

}
