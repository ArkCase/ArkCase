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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 1/25/2017.
 */
public class CorrespondenceQuery
{
    private QueryType type;

    private String jpaQuery;

    private List<String> fieldNames = new ArrayList<>();

    /**
     * @return the type
     */
    public QueryType getType()
    {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(QueryType type)
    {
        this.type = type;
    }

    public String getJpaQuery()
    {
        return jpaQuery;
    }

    public void setJpaQuery(String jpaQuery)
    {
        this.jpaQuery = jpaQuery;
    }

    public List<String> getFieldNames()
    {
        return new ArrayList<>(fieldNames);
    }

    public void setFieldNames(List<String> fieldNames)
    {
        if (fieldNames != null && !fieldNames.isEmpty())
        {
            this.fieldNames.clear();
            this.fieldNames.addAll(fieldNames);
        }
    }
}
