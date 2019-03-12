package com.armedia.acm.configuration;

/*-
 * #%L
 * ACM Service: Configuration
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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.services.configuration.ListOfValuesService;
import com.armedia.acm.services.configuration.LookupTableDescriptor;
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
