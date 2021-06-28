package com.armedia.acm.services.search.model.solr;

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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class SolrContentDocumentTest
{

    /**
     * Verify whether list properties are correctly transformed into Spring REST URL templates.
     */
    @Test
    public void listsToSolrMultivaluedElements()
    {
        SolrContentDocument scd = new SolrContentDocument();
        scd.setAllow_user_ls(Arrays.asList(100L, 101L, 102L));
        scd.setModified_date_tdt(new Date());
        scd.setCreate_date_tdt(new Date());

        String urlTemplate = scd.buildUrlTemplate();

        // we should have "allow_user_ls={allow_user_ls.1},allow_user_ls={allow_user_ls.2},allow_user_ls={allow_user_ls.3}"
        assertEquals(scd.getAllow_user_ls().size(), occurrencesOfStringInString(urlTemplate, "literal.allow_user_ls="));

        assertTrue(urlTemplate.contains("literal.allow_user_ls={literal.allow_user_ls.0}"));
        assertTrue(urlTemplate.contains("literal.allow_user_ls={literal.allow_user_ls.1}"));
        assertTrue(urlTemplate.contains("literal.allow_user_ls={literal.allow_user_ls.2}"));

        Map<String, Object> urlValues = scd.buildUrlValues();

        assertEquals(urlValues.get("literal.allow_user_ls.0"), scd.getAllow_user_ls().get(0));
        assertEquals(urlValues.get("literal.allow_user_ls.1"), scd.getAllow_user_ls().get(1));
        assertEquals(urlValues.get("literal.allow_user_ls.2"), scd.getAllow_user_ls().get(2));

    }

    public int occurrencesOfStringInString(String target, String searchTerm)
    {
        String replacedWithEmpty = target.replace(searchTerm, "");
        return (target.length() - replacedWithEmpty.length()) / searchTerm.length();
    }

}
