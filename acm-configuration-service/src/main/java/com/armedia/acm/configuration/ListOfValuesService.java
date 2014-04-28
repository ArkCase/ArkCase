package com.armedia.acm.configuration;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Created by armdev on 4/28/14.
 */
public class ListOfValuesService
{
    private JdbcTemplate acmJdbcTemplate;

    public List<String> lookupListOfStringValues(ListOfValuesType valuesType)
    {
        List<String> values = getAcmJdbcTemplate().queryForList("SELECT cm_value FROM " + valuesType.getTableName() +
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
