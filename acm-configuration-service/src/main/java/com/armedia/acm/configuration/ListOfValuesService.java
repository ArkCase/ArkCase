package com.armedia.acm.configuration;

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
        if ( lookupTableDescriptor == null || StringUtils.isEmpty(StringUtils.trimAllWhitespace(lookupTableDescriptor.getTableName())) )
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
