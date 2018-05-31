package com.armedia.acm.core.exceptions;

/*-
 * #%L
 * ACM Core API
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

import java.util.HashMap;
import java.util.Map;

public class AcmAppErrorJsonMsg extends Exception
{
    private String objectType;
    private String field;
    private Map<String, Object> extra;

    public AcmAppErrorJsonMsg(String message, String objectType, Throwable cause)
    {
        super(message, cause);
        this.objectType = objectType;
        this.extra = new HashMap<>();
    }

    public AcmAppErrorJsonMsg(String message, String objectType, String field, Throwable cause)
    {
        super(message, cause);
        this.objectType = objectType;
        this.field = field;
        this.extra = new HashMap<>();
    }

    public String getObjectType()
    {
        return objectType;
    }

    public String getField()
    {
        return field;
    }

    public Map<String, Object> getExtra()
    {
        return extra;
    }

    public void putExtra(String key, Object value)
    {
        extra.put(key, value);
    }
}
