package com.armedia.acm.services.config.lookups.model;

/*-
 * #%L
 * ACM Service: Config
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

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
public class LookupDefinition
{
    private LookupType lookupType;

    private String name;

    private Boolean readonly;

    private String lookupEntriesAsJson;

    public LookupDefinition()
    {
    }

    public LookupDefinition(LookupType lookupType, String name, String lookupEntriesAsJson)
    {
        super();
        this.lookupType = lookupType;
        this.name = name;
        this.lookupEntriesAsJson = lookupEntriesAsJson;
    }

    public LookupType getLookupType()
    {
        return lookupType;
    }

    public void setLookupType(LookupType lookupType)
    {
        this.lookupType = lookupType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLookupEntriesAsJson()
    {
        return lookupEntriesAsJson;
    }

    public void setLookupEntriesAsJson(String lookupEntriesAsJson)
    {
        this.lookupEntriesAsJson = lookupEntriesAsJson;
    }

    public Boolean getReadonly()
    {
        return readonly;
    }

    public void setReadonly(Boolean readonly)
    {
        this.readonly = readonly;
    }
}
