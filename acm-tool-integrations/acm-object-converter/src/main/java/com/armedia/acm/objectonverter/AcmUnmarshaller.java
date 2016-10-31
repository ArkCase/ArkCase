package com.armedia.acm.objectonverter;

/**
 * @author riste.tutureski
 *
 */
public interface AcmUnmarshaller
{

    public <E> E unmarshall(String source, Class<E> c);

}
