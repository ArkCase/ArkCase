package com.armedia.acm.services.config.lookups.model;

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

        List<StandardLookupEntry> emptyKeyEntries = new ArrayList<>();
        emptyKeyEntries.add(new StandardLookupEntry("key1", "value1"));
        emptyKeyEntries.add(new StandardLookupEntry("", "value2"));

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
                        emptyKeyEntries,
                        false,
                        "Empty key found in '" + testLookupName + "' lookup!" },
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