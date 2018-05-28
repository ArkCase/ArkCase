package com.armedia.acm.plugins.ecm.utils;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by joseph.mcgrady on 9/24/2015.
 */
public class GenericUtils
{

    /**
     * Takes a string containing a comma separated list of string values and
     * generates a list of trimmed String objects for each individual entry
     * 
     * @param commaSeparatedList
     *            - comma separated list of string values (e.x. "1,abc,6c")
     * @return array of Strings parsed from the list with trailing/leading whitespace removed from each entry
     */
    public static List<String> parseStringList(String commaSeparatedList)
    {
        List<String> stringList = new ArrayList<>();
        if (commaSeparatedList != null && commaSeparatedList.length() > 0)
        {
            String[] listItems = commaSeparatedList.split(",");
            stringList = Arrays.asList(listItems).stream().map(String::trim).collect(Collectors.toList());
        }
        return stringList;
    }

    /**
     * Determines if the file type is present in the supplied list of acceptable types
     * 
     * @param fileType
     *            - type of file to search for in the list
     * @param typeListString
     *            - comma separated types (e.x. ArkCase model type) to search
     * @return true if the specified type is in the list, false otherwise
     */
    public static boolean isFileTypeInList(String fileType, String typeListString)
    {
        boolean isInList = false;
        if (fileType != null && typeListString != null)
        {
            List<String> typeList = parseStringList(typeListString);
            for (String supportedType : typeList)
            {
                if (supportedType.equalsIgnoreCase(fileType))
                {
                    isInList = true;
                    break;
                }
            }
        }
        return isInList;
    }
}
