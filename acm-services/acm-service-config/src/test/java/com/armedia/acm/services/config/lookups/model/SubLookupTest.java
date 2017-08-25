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
public class SubLookupTest extends EasyMockSupport
{
    SubLookup subLookup;

    @Before
    public void setUp()
    {
        subLookup = new SubLookup();
    }

    @Test
    public void testValidateValidLookup()
    {
        // given
        StandardLookup standardLookup1 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries1 = new ArrayList<>();
        standardLookupEntries1.add(new StandardLookupEntry("sub1key1", "sub1value1"));
        standardLookupEntries1.add(new StandardLookupEntry("sub1key2", "sub1value2"));
        standardLookup1.setEntries(standardLookupEntries1);

        StandardLookup standardLookup2 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries2 = new ArrayList<>();
        standardLookupEntries2.add(new StandardLookupEntry("sub2key1", "sub2value1"));
        standardLookupEntries2.add(new StandardLookupEntry("sub2key2", "sub2value2"));
        standardLookup2.setEntries(standardLookupEntries2);

        List<SubLookupEntry> entries = new ArrayList<>();
        entries.add(new SubLookupEntry("key1", "value1", standardLookup1));
        entries.add(new SubLookupEntry("key2", "value2", standardLookup2));

        subLookup.setEntries(entries);

        // when
        LookupValidationResult res = subLookup.validate();

        // then
        assertTrue(res.isValid());
    }

    @Test
    public void testValidateLookupWithEmptyKey()
    {
        // given
        StandardLookup standardLookup1 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries1 = new ArrayList<>();
        standardLookupEntries1.add(new StandardLookupEntry("sub1key1", "sub1value1"));
        standardLookupEntries1.add(new StandardLookupEntry("sub1key2", "sub1value2"));
        standardLookup1.setEntries(standardLookupEntries1);

        StandardLookup standardLookup2 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries2 = new ArrayList<>();
        standardLookupEntries2.add(new StandardLookupEntry("sub2key1", "sub2value1"));
        standardLookupEntries2.add(new StandardLookupEntry("sub2key2", "sub2value2"));
        standardLookup2.setEntries(standardLookupEntries2);

        List<SubLookupEntry> entries = new ArrayList<>();
        entries.add(new SubLookupEntry("", "value1", standardLookup1));
        entries.add(new SubLookupEntry("key2", "value2", standardLookup2));

        subLookup.setEntries(entries);

        // when
        LookupValidationResult res = subLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithEmptyValue()
    {
        // given
        StandardLookup standardLookup1 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries1 = new ArrayList<>();
        standardLookupEntries1.add(new StandardLookupEntry("sub1key1", "sub1value1"));
        standardLookupEntries1.add(new StandardLookupEntry("sub1key2", "sub1value2"));
        standardLookup1.setEntries(standardLookupEntries1);

        StandardLookup standardLookup2 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries2 = new ArrayList<>();
        standardLookupEntries2.add(new StandardLookupEntry("sub2key1", "sub2value1"));
        standardLookupEntries2.add(new StandardLookupEntry("sub2key2", "sub2value2"));
        standardLookup2.setEntries(standardLookupEntries2);

        List<SubLookupEntry> entries = new ArrayList<>();
        entries.add(new SubLookupEntry("key1", "value1", standardLookup1));
        entries.add(new SubLookupEntry("key2", "", standardLookup2));

        subLookup.setEntries(entries);

        // when
        LookupValidationResult res = subLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithSubLookupWithEmptyKey()
    {
        // given
        StandardLookup standardLookup1 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries1 = new ArrayList<>();
        standardLookupEntries1.add(new StandardLookupEntry("sub1key1", "sub1value1"));
        standardLookupEntries1.add(new StandardLookupEntry("", "sub1value2"));
        standardLookup1.setEntries(standardLookupEntries1);

        StandardLookup standardLookup2 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries2 = new ArrayList<>();
        standardLookupEntries2.add(new StandardLookupEntry("sub2key1", "sub2value1"));
        standardLookupEntries2.add(new StandardLookupEntry("sub2key2", "sub2value2"));
        standardLookup2.setEntries(standardLookupEntries2);

        List<SubLookupEntry> entries = new ArrayList<>();
        entries.add(new SubLookupEntry("key1", "value1", standardLookup1));
        entries.add(new SubLookupEntry("key2", "value2", standardLookup2));

        subLookup.setEntries(entries);

        // when
        LookupValidationResult res = subLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithSubLookupWithEmptyValue()
    {
        // given
        StandardLookup standardLookup1 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries1 = new ArrayList<>();
        standardLookupEntries1.add(new StandardLookupEntry("sub1key1", "sub1value1"));
        standardLookupEntries1.add(new StandardLookupEntry("sub1key2", "sub1value2"));
        standardLookup1.setEntries(standardLookupEntries1);

        StandardLookup standardLookup2 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries2 = new ArrayList<>();
        standardLookupEntries2.add(new StandardLookupEntry("sub2key1", "sub2value1"));
        standardLookupEntries2.add(new StandardLookupEntry("sub2key2", ""));
        standardLookup2.setEntries(standardLookupEntries2);

        List<SubLookupEntry> entries = new ArrayList<>();
        entries.add(new SubLookupEntry("key1", "value1", standardLookup1));
        entries.add(new SubLookupEntry("key2", "value2", standardLookup2));

        subLookup.setEntries(entries);

        // when
        LookupValidationResult res = subLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithDuplicateKeys()
    {
        // given
        StandardLookup standardLookup1 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries1 = new ArrayList<>();
        standardLookupEntries1.add(new StandardLookupEntry("sub1key1", "sub1value1"));
        standardLookupEntries1.add(new StandardLookupEntry("sub1key2", "sub1value2"));
        standardLookup1.setEntries(standardLookupEntries1);

        StandardLookup standardLookup2 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries2 = new ArrayList<>();
        standardLookupEntries2.add(new StandardLookupEntry("sub2key1", "sub2value1"));
        standardLookupEntries2.add(new StandardLookupEntry("sub2key2", "sub2value2"));
        standardLookup2.setEntries(standardLookupEntries2);

        List<SubLookupEntry> entries = new ArrayList<>();
        entries.add(new SubLookupEntry("key1", "value1", standardLookup1));
        entries.add(new SubLookupEntry("key1", "value2", standardLookup2));

        subLookup.setEntries(entries);

        // when
        LookupValidationResult res = subLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithDuplicateValues()
    {
        // given
        StandardLookup standardLookup1 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries1 = new ArrayList<>();
        standardLookupEntries1.add(new StandardLookupEntry("sub1key1", "sub1value1"));
        standardLookupEntries1.add(new StandardLookupEntry("sub1key2", "sub1value2"));
        standardLookup1.setEntries(standardLookupEntries1);

        StandardLookup standardLookup2 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries2 = new ArrayList<>();
        standardLookupEntries2.add(new StandardLookupEntry("sub2key1", "sub2value1"));
        standardLookupEntries2.add(new StandardLookupEntry("sub2key2", "sub2value2"));
        standardLookup2.setEntries(standardLookupEntries2);

        List<SubLookupEntry> entries = new ArrayList<>();
        entries.add(new SubLookupEntry("key1", "value1", standardLookup1));
        entries.add(new SubLookupEntry("key2", "value1", standardLookup2));

        subLookup.setEntries(entries);

        // when
        LookupValidationResult res = subLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithSublookupWithDuplicateKeys()
    {
        // given
        StandardLookup standardLookup1 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries1 = new ArrayList<>();
        standardLookupEntries1.add(new StandardLookupEntry("sub1key1", "sub1value1"));
        standardLookupEntries1.add(new StandardLookupEntry("sub1key1", "sub1value2"));
        standardLookup1.setEntries(standardLookupEntries1);

        StandardLookup standardLookup2 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries2 = new ArrayList<>();
        standardLookupEntries2.add(new StandardLookupEntry("sub2key1", "sub2value1"));
        standardLookupEntries2.add(new StandardLookupEntry("sub2key2", "sub2value2"));
        standardLookup2.setEntries(standardLookupEntries2);

        List<SubLookupEntry> entries = new ArrayList<>();
        entries.add(new SubLookupEntry("key1", "value1", standardLookup1));
        entries.add(new SubLookupEntry("key2", "value2", standardLookup2));

        subLookup.setEntries(entries);

        // when
        LookupValidationResult res = subLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }

    @Test
    public void testValidateLookupWithSublookupWithDuplicateValues()
    {
        // given
        StandardLookup standardLookup1 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries1 = new ArrayList<>();
        standardLookupEntries1.add(new StandardLookupEntry("sub1key1", "sub1value1"));
        standardLookupEntries1.add(new StandardLookupEntry("sub1key2", "sub1value2"));
        standardLookup1.setEntries(standardLookupEntries1);

        StandardLookup standardLookup2 = new StandardLookup();
        List<StandardLookupEntry> standardLookupEntries2 = new ArrayList<>();
        standardLookupEntries2.add(new StandardLookupEntry("sub2key1", "sub2value2"));
        standardLookupEntries2.add(new StandardLookupEntry("sub2key2", "sub2value2"));
        standardLookup2.setEntries(standardLookupEntries2);

        List<SubLookupEntry> entries = new ArrayList<>();
        entries.add(new SubLookupEntry("key1", "value1", standardLookup1));
        entries.add(new SubLookupEntry("key2", "value2", standardLookup2));

        subLookup.setEntries(entries);

        // when
        LookupValidationResult res = subLookup.validate();

        // then
        assertFalse(res.isValid());
        assertNotNull(res.getErrorMessage());
    }
}