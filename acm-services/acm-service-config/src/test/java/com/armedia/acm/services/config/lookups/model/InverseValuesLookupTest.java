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
public class InverseValuesLookupTest extends EasyMockSupport
{
    InverseValuesLookup inverseValuesLookup;

    @Before
    public void setUp()
    {
        inverseValuesLookup = new InverseValuesLookup();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValidate()
    {
        // given
        String testLookupName = "testLookup";

        List<InverseValuesLookupEntry> validEntries = new ArrayList<>();
        validEntries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        validEntries.add(new InverseValuesLookupEntry("key2", "value2", "inverseKey2", "inverseValue2"));

        List<InverseValuesLookupEntry> emptyValueEntries = new ArrayList<>();
        emptyValueEntries.add(new InverseValuesLookupEntry("key1", "", "inverseKey1", "inverseValue1"));
        emptyValueEntries.add(new InverseValuesLookupEntry("key2", "value2", "inverseKey2", "inverseValue2"));

        List<InverseValuesLookupEntry> emptyInverseValueEntries = new ArrayList<>();
        emptyInverseValueEntries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", ""));
        emptyInverseValueEntries.add(new InverseValuesLookupEntry("key2", "value2", "inverseKey2", "inverseValue2"));

        List<InverseValuesLookupEntry> duplicateKeyEntries = new ArrayList<>();
        duplicateKeyEntries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        duplicateKeyEntries.add(new InverseValuesLookupEntry("key1", "value2", "inverseKey2", "inverseValue2"));

        List<InverseValuesLookupEntry> duplicateValuesEntries = new ArrayList<>();
        duplicateValuesEntries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        duplicateValuesEntries.add(new InverseValuesLookupEntry("key2", "value1", "inverseKey2", "inverseValue2"));

        List<InverseValuesLookupEntry> duplicateInverseKeyEntries = new ArrayList<>();
        duplicateInverseKeyEntries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        duplicateInverseKeyEntries.add(new InverseValuesLookupEntry("key2", "value2", "inverseKey1", "inverseValue2"));

        List<InverseValuesLookupEntry> duplicateInverseValuesEntries = new ArrayList<>();
        duplicateInverseValuesEntries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        duplicateInverseValuesEntries.add(new InverseValuesLookupEntry("key2", "value2", "inverseKey2", "inverseValue1"));

        Object[][] testData = {
                {
                        validEntries,
                        true,
                        null },
                {
                        emptyValueEntries,
                        false,
                        "Empty value found in '" + testLookupName + "' lookup!" },
                {
                        emptyInverseValueEntries,
                        false,
                        "Empty inverse value found in '" + testLookupName + "' lookup!" },
                {
                        duplicateKeyEntries,
                        false,
                        "Duplicate key found in '" + testLookupName + "' lookup! [key : key1]" },
                {
                        duplicateValuesEntries,
                        false,
                        "Duplicate value found in '" + testLookupName + "' lookup! [values : value1]" },
                {
                        duplicateInverseKeyEntries,
                        false,
                        "Duplicate inverse key found in '" + testLookupName + "' lookup! [key : inverseKey1]" },
                {
                        duplicateInverseValuesEntries,
                        false,
                        "Duplicate inverse value found in '" + testLookupName + "' lookup! [values : inverseValue1]" } };

        for (Object[] testDatum : testData)
        {
            List<InverseValuesLookupEntry> entries = (List<InverseValuesLookupEntry>) testDatum[0];
            inverseValuesLookup.setEntries(entries);
            inverseValuesLookup.setName(testLookupName);

            // when
            LookupValidationResult res = inverseValuesLookup.validate();

            // then
            assertEquals(testDatum[1], res.isValid());
            assertEquals(testDatum[2], res.getErrorMessage());
        }
    }

}
