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
public class StandardLookupTest extends EasyMockSupport
{
    StandardLookup standardLookup;

    @Before
    public void setUp()
    {
        standardLookup = new StandardLookup();
    }

    @Test
    public void testValidateValidLookup()
    {
        // given
        List<StandardLookupEntry> entries = new ArrayList<>();
        entries.add(new StandardLookupEntry("key1", "value1"));
        entries.add(new StandardLookupEntry("key2", "value2"));

        standardLookup.setEntries(entries);

        // when
        LookupValidationResult res = standardLookup.validate();

        // then
        assertTrue(res.isValid());
    }

    @Test
    public void testValidateLookupWithEmptyKey()
    {
        // given
        List<StandardLookupEntry> entries = new ArrayList<>();
        entries.add(new StandardLookupEntry("key1", "value1"));
        entries.add(new StandardLookupEntry("", "value2"));

        standardLookup.setEntries(entries);

        // when
        LookupValidationResult res = standardLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithEmptyValue()
    {
        // given
        List<StandardLookupEntry> entries = new ArrayList<>();
        entries.add(new StandardLookupEntry("key1", ""));
        entries.add(new StandardLookupEntry("key2", "value2"));

        standardLookup.setEntries(entries);

        // when
        LookupValidationResult res = standardLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithDuplicateKeys()
    {
        // given
        List<StandardLookupEntry> entries = new ArrayList<>();
        entries.add(new StandardLookupEntry("key1", "value1"));
        entries.add(new StandardLookupEntry("key1", "value2"));

        standardLookup.setEntries(entries);

        // when
        LookupValidationResult res = standardLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithDuplicateValues()
    {
        // given
        List<StandardLookupEntry> entries = new ArrayList<>();
        entries.add(new StandardLookupEntry("key1", "value1"));
        entries.add(new StandardLookupEntry("key2", "value1"));

        standardLookup.setEntries(entries);

        // when
        LookupValidationResult res = standardLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }
}