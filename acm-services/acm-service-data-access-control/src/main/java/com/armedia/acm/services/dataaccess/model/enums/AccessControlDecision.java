package com.armedia.acm.services.dataaccess.model.enums;

import java.util.Arrays;

/**
 * Created by armdev on 7/9/14.
 */
public enum AccessControlDecision
{
    MANDATORY_GRANT,
    MANDATORY_DENY,
    GRANT,
    DENY;

    public static void validateValue(String value)
    {
        try
        {
            valueOf(value);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("AccessControlDecision must be one of " +
                    Arrays.asList(values()) + ", but was '" + value + "'");
        }
    }

}
