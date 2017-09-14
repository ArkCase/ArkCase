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
public class NestedLookupTest extends EasyMockSupport
{
    NestedLookup nestedLookup;

    @Before
    public void setUp()
    {
        nestedLookup = new NestedLookup();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValidate()
    {
        // given
        String testLookupName = "testLookup";

        List<StandardLookupEntry> validStandardLookupEntries1 = new ArrayList<>();
        validStandardLookupEntries1.add(new StandardLookupEntry("sub1key1", "sub1value1"));
        validStandardLookupEntries1.add(new StandardLookupEntry("sub1key2", "sub1value2"));

        List<StandardLookupEntry> validStandardLookupEntries2 = new ArrayList<>();
        validStandardLookupEntries2.add(new StandardLookupEntry("sub2key1", "sub2value1"));
        validStandardLookupEntries2.add(new StandardLookupEntry("sub2key2", "sub2value2"));

        List<NestedLookupEntry> validEntries = new ArrayList<>();
        validEntries.add(new NestedLookupEntry("key1", "value1", validStandardLookupEntries1));
        validEntries.add(new NestedLookupEntry("key2", "value2", validStandardLookupEntries2));

        List<NestedLookupEntry> emptyKeyEntries = new ArrayList<>();
        emptyKeyEntries.add(new NestedLookupEntry("", "value1", validStandardLookupEntries1));
        emptyKeyEntries.add(new NestedLookupEntry("key2", "value2", validStandardLookupEntries2));

        List<NestedLookupEntry> emptyValueEntries = new ArrayList<>();
        emptyValueEntries.add(new NestedLookupEntry("key1", "value1", validStandardLookupEntries1));
        emptyValueEntries.add(new NestedLookupEntry("key2", "", validStandardLookupEntries2));

        List<StandardLookupEntry> emptyKeyInSubLookupEntries = new ArrayList<>();
        emptyKeyInSubLookupEntries.add(new StandardLookupEntry("sub1key1", "sub1value1"));
        emptyKeyInSubLookupEntries.add(new StandardLookupEntry("", "sub1value2"));

        List<NestedLookupEntry> emptyKeyInSubLookupEntriesNestedLookupEntries = new ArrayList<>();
        emptyKeyInSubLookupEntriesNestedLookupEntries.add(new NestedLookupEntry("key1", "value1", validStandardLookupEntries1));
        emptyKeyInSubLookupEntriesNestedLookupEntries.add(new NestedLookupEntry("key2", "value2", emptyKeyInSubLookupEntries));

        List<StandardLookupEntry> emptyValueInSubLookupEntries = new ArrayList<>();
        emptyValueInSubLookupEntries.add(new StandardLookupEntry("sub2key1", "sub2value1"));
        emptyValueInSubLookupEntries.add(new StandardLookupEntry("sub2key2", ""));

        List<NestedLookupEntry> emptyValueInSubLookupEntriesLookupNestedLookupEntries = new ArrayList<>();
        emptyValueInSubLookupEntriesLookupNestedLookupEntries.add(new NestedLookupEntry("key1", "value1", validStandardLookupEntries2));
        emptyValueInSubLookupEntriesLookupNestedLookupEntries.add(new NestedLookupEntry("key2", "value2", emptyValueInSubLookupEntries));

        List<NestedLookupEntry> duplicateKeysEntries = new ArrayList<>();
        duplicateKeysEntries.add(new NestedLookupEntry("key1", "value1", validStandardLookupEntries1));
        duplicateKeysEntries.add(new NestedLookupEntry("key1", "value2", validStandardLookupEntries2));

        List<NestedLookupEntry> duplicateValuesEntries = new ArrayList<>();
        duplicateValuesEntries.add(new NestedLookupEntry("key1", "value1", validStandardLookupEntries1));
        duplicateValuesEntries.add(new NestedLookupEntry("key2", "value1", validStandardLookupEntries2));

        List<StandardLookupEntry> duplicateKeysInSubLookupEntries = new ArrayList<>();
        duplicateKeysInSubLookupEntries.add(new StandardLookupEntry("sub1key1", "sub1value1"));
        duplicateKeysInSubLookupEntries.add(new StandardLookupEntry("sub1key1", "sub1value2"));

        List<NestedLookupEntry> duplicateKeysInSubLookupEntriesNestedLookupEntries = new ArrayList<>();
        duplicateKeysInSubLookupEntriesNestedLookupEntries.add(new NestedLookupEntry("key1", "value1", validStandardLookupEntries1));
        duplicateKeysInSubLookupEntriesNestedLookupEntries.add(new NestedLookupEntry("key2", "value2", duplicateKeysInSubLookupEntries));

        List<StandardLookupEntry> duplicateValuesInSubLookupEntries = new ArrayList<>();
        duplicateValuesInSubLookupEntries.add(new StandardLookupEntry("sub1key1", "sub1value1"));
        duplicateValuesInSubLookupEntries.add(new StandardLookupEntry("sub1key2", "sub1value1"));

        List<NestedLookupEntry> duplicateValuesInSubLookupEntriesNestedLookupEntries = new ArrayList<>();
        duplicateValuesInSubLookupEntriesNestedLookupEntries.add(new NestedLookupEntry("key1", "value1", validStandardLookupEntries1));
        duplicateValuesInSubLookupEntriesNestedLookupEntries
                .add(new NestedLookupEntry("key2", "value2", duplicateValuesInSubLookupEntries));

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
                        emptyValueEntries,
                        false,
                        "Empty value found in '" + testLookupName + "' lookup!" },
                {
                        emptyKeyInSubLookupEntriesNestedLookupEntries,
                        false,
                        "Empty key found in '" + testLookupName + "' lookup!" },
                {
                        emptyValueInSubLookupEntriesLookupNestedLookupEntries,
                        false,
                        "Empty value found in '" + testLookupName + "' lookup!" },
                {
                        duplicateKeysEntries,
                        false,
                        "Duplicate key found in '" + testLookupName + "' lookup! [key : key1]" },
                {
                        duplicateValuesEntries,
                        false,
                        "Duplicate value found in '" + testLookupName + "' lookup! [values : value1]" },
                {
                        duplicateKeysInSubLookupEntriesNestedLookupEntries,
                        false,
                        "Duplicate key found in '" + testLookupName + "' lookup! [key : sub1key1]" },
                {
                        duplicateValuesInSubLookupEntriesNestedLookupEntries,
                        false,
                        "Duplicate value found in '" + testLookupName + "' lookup! [values : sub1value1]" } };

        for (Object[] testDatum : testData)
        {
            List<NestedLookupEntry> entries = (List<NestedLookupEntry>) testDatum[0];
            nestedLookup.setEntries(entries);
            nestedLookup.setName(testLookupName);

            // when
            LookupValidationResult res = nestedLookup.validate();

            // then
            assertEquals(testDatum[1], res.isValid());
            assertEquals(testDatum[2], res.getErrorMessage());
        }
    }
}