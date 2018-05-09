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
