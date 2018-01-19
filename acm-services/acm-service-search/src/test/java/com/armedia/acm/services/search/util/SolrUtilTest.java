package com.armedia.acm.services.search.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SolrUtilTest
{

    @Test
    public void hasSpecialCharacters()
    {
        assertFalse(SolrUtil.hasSpecialCharacters("noSpecialCharacters"));
        for (char c : SolrUtil.getSolrSpecialCharacters().toCharArray())
        {
            assertTrue(SolrUtil.hasSpecialCharacters("test" + c));
            assertTrue(SolrUtil.hasSpecialCharacters(c + "test"));
            assertTrue(SolrUtil.hasSpecialCharacters("test" + c + "test "));
        }
    }
}