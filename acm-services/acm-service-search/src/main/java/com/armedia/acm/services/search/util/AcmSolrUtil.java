package com.armedia.acm.services.search.util;

/*-
 * #%L
 * ACM Service: Search
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

/**
 * Created by nebojsha on 16/01/18.
 */
public final class AcmSolrUtil
{
    /**
     * special characters in Solr
     */
    private static final String solrSpecialCharacters;
    /**
     * encoded chars used for ACL
     */
    private static Map<Character, String> encodedChars;

    static
    {
        solrSpecialCharacters = "\\+!():,^[]\"{}~*?|&;/ ";
        encodedChars = new HashMap<>(solrSpecialCharacters.length());
        for (char c : solrSpecialCharacters.toCharArray())
        {
            encodedChars.put(c, String.format("_00%s_", Integer.toHexString(c).toUpperCase()));
        }
    }

    /**
     * Checks given String if contains at least one special characters from Solr
     *
     * @param value
     *            given String that might contain special characters
     * @return returns true if find at least one match
     */
    public static boolean hasSpecialCharacters(String value)
    {
        for (char c : value.toCharArray())
        {
            if (solrSpecialCharacters.contains(String.valueOf(c)))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * getter for Solr special characters
     *
     * @return String containing Solr special characters
     */
    public static String getSolrSpecialCharacters()
    {
        return solrSpecialCharacters;
    }

    /**
     * This method provides encoding of special character used for ACL handling.
     * Since it works with just escape the whole term with parenthesis this method should
     * become deprecated soon.
     * 
     * @param c
     * @return encoded character
     */
    public static String getCharEncodingForACL(char c)
    {
        return encodedChars.get(c);
    }

    /**
     * This method provides encoding of special characters used for ACL handling.
     * Since it works with just escape the whole term with parenthesis this method should
     * become deprecated soon.
     *
     * @param s
     *            given String
     * @return encoded string
     */
    public static String encodeSpecialCharactersForACL(String s)
    {
        for (char c : solrSpecialCharacters.toCharArray())
        {
            s = s.replace(Character.toString(c), getCharEncodingForACL(c));
        }
        return s;
    }
}
