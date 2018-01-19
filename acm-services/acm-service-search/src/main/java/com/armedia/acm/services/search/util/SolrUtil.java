package com.armedia.acm.services.search.util;

/**
 * Created by nebojsha on 16/01/18.
 */
public class SolrUtil
{
    /**
     * special characters in Solr
     */
    private static final String solrSpecialCharacters = "\\+-!():,^[]\"{}~*?|&;/ ";

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
}
