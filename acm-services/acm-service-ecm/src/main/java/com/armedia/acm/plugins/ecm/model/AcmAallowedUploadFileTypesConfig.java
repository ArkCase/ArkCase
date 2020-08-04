package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.configuration.annotations.MapValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mario Gjurcheski 5/29/2020
 */
public class AcmAallowedUploadFileTypesConfig
{
    private Map<String, List<String>> allowedUploadFileTypes = new HashMap<>();

    @MapValue(value = "allowedUploadFileTypes")
    public Map<String, List<String>> getAllowedUploadFileTypes()
    {
        return allowedUploadFileTypes;
    }

    public void setAllowedUploadFileTypes(Map<String, List<String>> allowedUploadFileTypes)
    {
        this.allowedUploadFileTypes = allowedUploadFileTypes;
    }
}
