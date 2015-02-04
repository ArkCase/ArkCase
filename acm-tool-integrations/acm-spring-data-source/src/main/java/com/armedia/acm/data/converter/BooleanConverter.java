package com.armedia.acm.data.converter;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 * We have to use an EclipseLink interface instead of the JPA @Converter annotation since Spring JPA
 * classpath scanning does not search for @Converters... so the persistence unit Spring builds will not know
 * about the converter.
 */
public class BooleanConverter implements Converter
{
    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session)
    {
        if ( objectValue == null )
        {
            return "false";
        }

        if ( objectValue instanceof Boolean )
        {
            return objectValue.toString();
        }

        return "false";
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session)
    {
        if ( dataValue == null )
        {
            return Boolean.FALSE;
        }

        if ( dataValue instanceof String )
        {
            return Boolean.valueOf((String) dataValue);
        }

        return Boolean.FALSE;
    }

    @Override
    public boolean isMutable()
    {
        return false;
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session)
    {

    }
}
