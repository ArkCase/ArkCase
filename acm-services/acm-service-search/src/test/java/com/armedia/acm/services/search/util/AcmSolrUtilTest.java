package com.armedia.acm.services.search.util;

import static org.junit.Assert.*;

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
        assertEquals("_005C_", AcmSolrUtil.getCharEncodingForACL('\\'));
        assertEquals("_002B_", AcmSolrUtil.getCharEncodingForACL('+'));
        assertEquals("_0021_", AcmSolrUtil.getCharEncodingForACL('!'));
        assertEquals("_0028_", AcmSolrUtil.getCharEncodingForACL('('));
        assertEquals("_0029_", AcmSolrUtil.getCharEncodingForACL(')'));
        assertEquals("_003A_", AcmSolrUtil.getCharEncodingForACL(':'));
        assertEquals("_002C_", AcmSolrUtil.getCharEncodingForACL(','));
        assertEquals("_005E_", AcmSolrUtil.getCharEncodingForACL('^'));
        assertEquals("_005B_", AcmSolrUtil.getCharEncodingForACL('['));
        assertEquals("_005D_", AcmSolrUtil.getCharEncodingForACL(']'));
        assertEquals("_0022_", AcmSolrUtil.getCharEncodingForACL('\"'));
        assertEquals("_007B_", AcmSolrUtil.getCharEncodingForACL('{'));
        assertEquals("_007D_", AcmSolrUtil.getCharEncodingForACL('}'));
        assertEquals("_007E_", AcmSolrUtil.getCharEncodingForACL('~'));
        assertEquals("_002A_", AcmSolrUtil.getCharEncodingForACL('*'));
        assertEquals("_002A_", AcmSolrUtil.getCharEncodingForACL('*'));
        assertEquals("_003F_", AcmSolrUtil.getCharEncodingForACL('?'));
        assertEquals("_007C_", AcmSolrUtil.getCharEncodingForACL('|'));
        assertEquals("_0026_", AcmSolrUtil.getCharEncodingForACL('&'));
        assertEquals("_003B_", AcmSolrUtil.getCharEncodingForACL(';'));
        assertEquals("_002F_", AcmSolrUtil.getCharEncodingForACL('/'));
    }

    @Test
    public void encodeSpecialCharactersForACL()
    {
        String termWithSpecialCharacters = "!@!@#asasdasd{}[])(*&^%  asdasd asd asd asd asd";
        String encoded = AcmSolrUtil.encodeSpecialCharactersForACL(termWithSpecialCharacters);
        assertFalse(AcmSolrUtil.hasSpecialCharacters(encoded));
        assertEquals(
                "_0021_@_0021_@#asasdasd_007B__007D__005B__005D__0029__0028__002A__0026__005E_%_0020__0020_asdasd_0020_asd_0020_asd_0020_asd_0020_asd",
                encoded);
    }
}