package com.armedia.acm.objectdiff.model;

/*-
 * #%L
 * Tool Integrations: Object Diff Util
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

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public abstract class AcmChange implements Serializable
{
    /**
     * what action was performed. Constants are used from AcmDiffConstants
     */
    private String action;
    /**
     * path from the root object
     */
    private String path;
    /**
     * path of the parent object
     */
    private String shortPath;

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getShortPath()
    {
        return shortPath;
    }

    public void setShortPath(String shortPath)
    {
        this.shortPath = shortPath;
    }

    @JsonIgnore
    public abstract boolean isLeaf();

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}
