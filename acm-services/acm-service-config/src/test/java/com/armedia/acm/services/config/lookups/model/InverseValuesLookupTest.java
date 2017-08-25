package com.armedia.acm.services.config.lookups.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testValidateValidLookup()
    {
        // given
        List<InverseValuesLookupEntry> entries = new ArrayList<>();
        entries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        entries.add(new InverseValuesLookupEntry("key2", "value2", "inverseKey2", "inverseValue2"));

        inverseValuesLookup.setEntries(entries);

        // when
        LookupValidationResult res = inverseValuesLookup.validate();

        // then
        assertTrue(res.isValid());
    }

    @Test
    public void testValidateLookupWithEmptyKey()
    {
        // given
        List<InverseValuesLookupEntry> entries = new ArrayList<>();
        entries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        entries.add(new InverseValuesLookupEntry("", "value2", "inverseKey2", "inverseValue2"));

        inverseValuesLookup.setEntries(entries);

        // when
        LookupValidationResult res = inverseValuesLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithEmptyValue()
    {
        // given
        List<InverseValuesLookupEntry> entries = new ArrayList<>();
        entries.add(new InverseValuesLookupEntry("key1", "", "inverseKey1", "inverseValue1"));
        entries.add(new InverseValuesLookupEntry("key2", "value2", "inverseKey2", "inverseValue2"));

        inverseValuesLookup.setEntries(entries);

        // when
        LookupValidationResult res = inverseValuesLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithEmptyInverseKey()
    {
        // given
        List<InverseValuesLookupEntry> entries = new ArrayList<>();
        entries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        entries.add(new InverseValuesLookupEntry("key2", "value2", "", "inverseValue2"));

        inverseValuesLookup.setEntries(entries);

        // when
        LookupValidationResult res = inverseValuesLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithEmptyInverseValue()
    {
        // given
        List<InverseValuesLookupEntry> entries = new ArrayList<>();
        entries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", ""));
        entries.add(new InverseValuesLookupEntry("key2", "value2", "inverseKey2", "inverseValue2"));

        inverseValuesLookup.setEntries(entries);

        // when
        LookupValidationResult res = inverseValuesLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithDuplicateKeys()
    {
        // given
        List<InverseValuesLookupEntry> entries = new ArrayList<>();
        entries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        entries.add(new InverseValuesLookupEntry("key1", "value2", "inverseKey2", "inverseValue2"));

        inverseValuesLookup.setEntries(entries);

        // when
        LookupValidationResult res = inverseValuesLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithDuplicateValues()
    {
        // given
        List<InverseValuesLookupEntry> entries = new ArrayList<>();
        entries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        entries.add(new InverseValuesLookupEntry("key2", "value1", "inverseKey2", "inverseValue2"));

        inverseValuesLookup.setEntries(entries);

        // when
        LookupValidationResult res = inverseValuesLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithDuplicateInverseKeys()
    {
        // given
        List<InverseValuesLookupEntry> entries = new ArrayList<>();
        entries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        entries.add(new InverseValuesLookupEntry("key2", "value2", "inverseKey1", "inverseValue2"));

        inverseValuesLookup.setEntries(entries);

        // when
        LookupValidationResult res = inverseValuesLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithDuplicateInversValues()
    {
        // given
        List<InverseValuesLookupEntry> entries = new ArrayList<>();
        entries.add(new InverseValuesLookupEntry("key1", "value1", "inverseKey1", "inverseValue1"));
        entries.add(new InverseValuesLookupEntry("key2", "value2", "inverseKey2", "inverseValue1"));

        inverseValuesLookup.setEntries(entries);

        // when
        LookupValidationResult res = inverseValuesLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }
}