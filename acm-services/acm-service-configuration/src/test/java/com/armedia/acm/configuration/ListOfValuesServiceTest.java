package com.armedia.acm.configuration;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by armdev on 4/28/14.
 */
public class ListOfValuesServiceTest extends EasyMockSupport
{
    private ListOfValuesService unit;

    private JdbcTemplate mockJdbcTemplate;

    @Before
    public void setUp() throws Exception
    {
        unit = new ListOfValuesService();

        mockJdbcTemplate = createMock(JdbcTemplate.class);

        unit.setAcmJdbcTemplate(mockJdbcTemplate);
    }

    @Test
    public void lookupListOfStringValues() throws Exception
    {
        LookupTableDescriptor ltd = new LookupTableDescriptor();
        ltd.setTableName("lookupTable");

        List<String> values = Arrays.asList("One", "Two", "Three");

        expect(mockJdbcTemplate.queryForList("SELECT cm_value FROM " + ltd.getTableName() +
                " WHERE cm_status = 'ACTIVE' ORDER BY cm_order", String.class)).andReturn(values);

        replayAll();

        List<String> found = unit.lookupListOfStringValues(ltd);

        verifyAll();

        assertEquals(values, found);
    }

}
