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

import java.io.Serializable;
import java.util.List;

public class AcmDiffBeanConfiguration implements Serializable
{
    /**
     * which class should be processed
     */
    private String className;
    /**
     * name in the path
     */
    private String name;
    /**
     * Spring expression
     */
    private String displayExpression;
    /**
     * which fields should be included for comparing
     */
    private List<String> includeFields;
    /**
     * which fields should be skipped for comparing
     */
    private List<String> skipFields;
    /**
     * which fields are constructing the id
     */
    private List<String> id;

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<String> getIncludeFields()
    {
        return includeFields;
    }

    public void setIncludeFields(List<String> includeFields)
    {
        this.includeFields = includeFields;
    }

    public List<String> getSkipFields()
    {
        return skipFields;
    }

    public void setSkipFields(List<String> skipFields)
    {
        this.skipFields = skipFields;
    }

    public List<String> getId()
    {
        return id;
    }

    public void setId(List<String> id)
    {
        this.id = id;
    }

    public String getDisplayExpression()
    {
        return displayExpression;
    }

    public void setDisplayExpression(String displayExpression)
    {
        this.displayExpression = displayExpression;
    }
}
