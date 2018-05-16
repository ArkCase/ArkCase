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

import java.util.Date;

/**
 * @author sasko.tanaskoski
 *
 */

/**
 * This POJO is used for storing mergeFieldVersion parameters to json file
 */
public class CorrespondenceMergeFieldVersionConfiguration
{

    private String mergingVersion;

    private boolean mergingActiveVersion;

    private String mergingType;

    private String modifier;

    private Date modified;

    /**
     * @return the mergingVersion
     */
    public String getMergingVersion()
    {
        return mergingVersion;
    }

    /**
     * @param mergingVersion
     *            the mergingVersion to set
     */
    public void setMergingVersion(String mergingVersion)
    {
        this.mergingVersion = mergingVersion;
    }

    /**
     * @return the mergingActiveVersion
     */
    public boolean isMergingActiveVersion()
    {
        return mergingActiveVersion;
    }

    /**
     * @param mergingActiveVersion
     *            the mergingActiveVersion to set
     */
    public void setMergingActiveVersion(boolean mergingActiveVersion)
    {
        this.mergingActiveVersion = mergingActiveVersion;
    }

    /**
     * @return the modifier
     */
    public String getModifier()
    {
        return modifier;
    }

    /**
     * @param modifier
     *            the modifier to set
     */
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    /**
     * @return the mergingType
     */
    public String getMergingType()
    {
        return mergingType;
    }

    /**
     * @param mergingType
     *            the mergingType to set
     */
    public void setMergingType(String mergingType)
    {
        this.mergingType = mergingType;
    }

    /**
     * @return the modified
     */
    public Date getModified()
    {
        return modified;
    }

    /**
     * @param modified
     *            the modified to set
     */
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

}
