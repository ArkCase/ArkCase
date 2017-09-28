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

        List<InverseValuesLookupEntry> emptyKeyEntries = new ArrayList<>();
        emptyKeyEntries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        emptyKeyEntries.add(new InverseValuesLookupEntry("", "value2", "inverseKey2", "inverseValue2"));

        List<InverseValuesLookupEntry> emptyValueEntries = new ArrayList<>();
        emptyValueEntries.add(new InverseValuesLookupEntry("key1", "", "inverseKey1", "inverseValue1"));
        emptyValueEntries.add(new InverseValuesLookupEntry("key2", "value2", "inverseKey2", "inverseValue2"));

        List<InverseValuesLookupEntry> emptyInverseKeyEntries = new ArrayList<>();
        emptyInverseKeyEntries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        emptyInverseKeyEntries.add(new InverseValuesLookupEntry("key2", "value2", "", "inverseValue2"));

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
                        emptyKeyEntries,
                        false,
                        "Empty key found in '" + testLookupName + "' lookup!" },
                {
                        emptyValueEntries,
                        false,
                        "Empty value found in '" + testLookupName + "' lookup!" },
                {
                        emptyInverseKeyEntries,
                        false,
                        "Empty inverse key found in '" + testLookupName + "' lookup!" },
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