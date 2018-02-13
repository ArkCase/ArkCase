package com.armedia.acm.services.search.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nebojsha on 16/01/18.
 */
public final class AcmSolrUtil
{
    /**
     * encoded chars used for ACL
     */
    private static Map<Character, String> encodedChars;
    /**
     * special characters in Solr
     */
    private static final String solrSpecialCharacters;
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
