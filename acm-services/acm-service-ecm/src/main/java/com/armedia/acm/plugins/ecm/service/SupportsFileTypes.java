package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.json.JsonException;

import java.util.HashSet;
import java.util.Set;

public interface SupportsFileTypes
{
    Logger log = LogManager.getLogger(SupportsFileTypes.class);

    Set<String> getFileTypes();

    default Set<String> getFileTypes(String fileTypesJsonArrayString)
    {
        Set<String> fileTypes = new HashSet<>();
        try
        {
            JSONArray jsonArray = new JSONArray(fileTypesJsonArrayString);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject fileTypeObj = jsonArray.getJSONObject(i);
                String fileType = fileTypeObj.getString("type");
                fileTypes.add(fileType);
            }
        }
        catch (JsonException e)
        {
            log.warn("{} is not valid json string", fileTypesJsonArrayString);
        }
        return fileTypes;
    }
}
