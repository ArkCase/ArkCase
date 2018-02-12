package com.armedia.acm.services.search.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AcmSolrUtilTest
{

    @Test
    public void hasSpecialCharacters()
    {
        assertFalse(AcmSolrUtil.hasSpecialCharacters("noSpecialCharacters"));
        for (char c : AcmSolrUtil.getSolrSpecialCharacters().toCharArray())
        {
            assertTrue(AcmSolrUtil.hasSpecialCharacters("test" + c));
            assertTrue(AcmSolrUtil.hasSpecialCharacters(c + "test"));
            assertTrue(AcmSolrUtil.hasSpecialCharacters("test" + c + "test "));
        }
    }

    @Test
    public void testEncodingSpecialCharactersForACL()
    {
        assertEquals("_5C_", AcmSolrUtil.getCharEncodingForACL('\\'));
        assertEquals("_2B_", AcmSolrUtil.getCharEncodingForACL('+'));
        assertEquals("_2D_", AcmSolrUtil.getCharEncodingForACL('-'));
        assertEquals("_21_", AcmSolrUtil.getCharEncodingForACL('!'));
        assertEquals("_28_", AcmSolrUtil.getCharEncodingForACL('('));
        assertEquals("_29_", AcmSolrUtil.getCharEncodingForACL(')'));
        assertEquals("_3A_", AcmSolrUtil.getCharEncodingForACL(':'));
        assertEquals("_2C_", AcmSolrUtil.getCharEncodingForACL(','));
        assertEquals("_5E_", AcmSolrUtil.getCharEncodingForACL('^'));
        assertEquals("_5B_", AcmSolrUtil.getCharEncodingForACL('['));
        assertEquals("_5D_", AcmSolrUtil.getCharEncodingForACL(']'));
        assertEquals("_22_", AcmSolrUtil.getCharEncodingForACL('\"'));
        assertEquals("_7B_", AcmSolrUtil.getCharEncodingForACL('{'));
        assertEquals("_7D_", AcmSolrUtil.getCharEncodingForACL('}'));
        assertEquals("_7E_", AcmSolrUtil.getCharEncodingForACL('~'));
        assertEquals("_2A_", AcmSolrUtil.getCharEncodingForACL('*'));
        assertEquals("_2A_", AcmSolrUtil.getCharEncodingForACL('*'));
        assertEquals("_3F_", AcmSolrUtil.getCharEncodingForACL('?'));
        assertEquals("_7C_", AcmSolrUtil.getCharEncodingForACL('|'));
        assertEquals("_26_", AcmSolrUtil.getCharEncodingForACL('&'));
        assertEquals("_3B_", AcmSolrUtil.getCharEncodingForACL(';'));
        assertEquals("_2F_", AcmSolrUtil.getCharEncodingForACL('/'));
    }

    @Test
    public void encodeSpecialCharactersForACL()
    {
        String termWithSpecialCharacters = "!@!@#asasdasd{}[])(*&^%  asdasd asd asd asd asd";
        String encoded = AcmSolrUtil.encodeSpecialCharactersForACL(termWithSpecialCharacters);
        assertFalse(AcmSolrUtil.hasSpecialCharacters(encoded));
        assertEquals("_21_@_21_@#asasdasd_7B__7D__5B__5D__29__28__2A__26__5E_%_20__20_asdasd_20_asd_20_asd_20_asd_20_asd", encoded);
    }
}