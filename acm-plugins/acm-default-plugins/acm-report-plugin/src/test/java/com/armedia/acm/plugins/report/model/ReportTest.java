package com.armedia.acm.plugins.report.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReportTest
{
    @Test
    public void propertyName() throws Exception
    {
        Report report = new Report();
        report.setName("GratefulDead.prpti");
        assertEquals("GRATEFUL_DEAD", report.getPropertyName());
    }

    @Test
    public void propertyName_noExtension() throws Exception
    {
        Report report = new Report();
        report.setName("GratefulDead");
        assertEquals("GRATEFUL_DEAD", report.getPropertyName());
    }

    @Test
    public void propertyName_nullName() throws Exception
    {
        Report report = new Report();
        report.setName(null);
        assertNull(report.getPropertyName());
    }
}
