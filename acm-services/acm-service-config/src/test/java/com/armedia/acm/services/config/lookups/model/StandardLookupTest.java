package com.armedia.acm.services.config.lookups.model;

/*-
 * #%L
 * ACM Service: Config
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

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public class StandardLookupTest extends EasyMockSupport
{
    StandardLookup standardLookup;

    @Before
    public void setUp()
    {
        standardLookup = new StandardLookup();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValidate()
    {
        // given
        String testLookupName = "testLookup";

        List<StandardLookupEntry> validEntries = new ArrayList<>();
        validEntries.add(new StandardLookupEntry("key1", "value1"));
        validEntries.add(new StandardLookupEntry("key2", "value2"));

        List<StandardLookupEntry> duplicateKeyEntries = new ArrayList<>();
        duplicateKeyEntries.add(new StandardLookupEntry("key1", "value1"));
        duplicateKeyEntries.add(new StandardLookupEntry("key1", "value2"));

        List<StandardLookupEntry> emptyValueEntries = new ArrayList<>();
        emptyValueEntries.add(new StandardLookupEntry("key1", ""));
        emptyValueEntries.add(new StandardLookupEntry("key2", "value2"));

        List<StandardLookupEntry> duplicateValuesEntries = new ArrayList<>();
        duplicateValuesEntries.add(new StandardLookupEntry("key1", "value1"));
        duplicateValuesEntries.add(new StandardLookupEntry("key2", "value1"));

        Object[][] testData = {
                {
                        validEntries,
                        true,
                        null },
                {
                        duplicateKeyEntries,
                        false,
                        "Duplicate key found in '" + testLookupName + "' lookup! [key : key1]" },
                {
                        emptyValueEntries,
                        false,
                        "Empty value found in '" + testLookupName + "' lookup!" },
                {
                        duplicateValuesEntries,
                        false,
                        "Duplicate value found in '" + testLookupName + "' lookup! [values : value1]" } };

        for (Object[] testDatum : testData)
        {
            List<StandardLookupEntry> entries = (List<StandardLookupEntry>) testDatum[0];
            standardLookup.setEntries(entries);
            standardLookup.setName(testLookupName);

            // when
            LookupValidationResult res = standardLookup.validate();

            // then
            assertEquals(testDatum[1], res.isValid());
            assertEquals(testDatum[2], res.getErrorMessage());
        }
    }
}
