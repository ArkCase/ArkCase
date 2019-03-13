package com.armedia.acm.services.configuration;

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

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by armdev on 4/28/14.
 */
public class ListOfValuesService
{
    private JdbcTemplate acmJdbcTemplate;

    public List<String> lookupListOfStringValues(LookupTableDescriptor lookupTableDescriptor)
    {
        if (lookupTableDescriptor == null || StringUtils.isEmpty(StringUtils.trimAllWhitespace(lookupTableDescriptor.getTableName())))
        {
            throw new IllegalArgumentException("Lookup table descriptor must specify a table name.");
        }

        String lookupTableName = StringUtils.trimAllWhitespace(lookupTableDescriptor.getTableName());

        List<String> values = getAcmJdbcTemplate().queryForList("SELECT cm_value FROM " + lookupTableName +
                " WHERE cm_status = 'ACTIVE' ORDER BY cm_order", String.class);

        return values;
    }

    public JdbcTemplate getAcmJdbcTemplate()
    {
        return acmJdbcTemplate;
    }

    public void setAcmJdbcTemplate(JdbcTemplate acmJdbcTemplate)
    {
        this.acmJdbcTemplate = acmJdbcTemplate;
    }
}
