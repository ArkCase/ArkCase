package com.armedia.acm.objectonverter;

import java.util.Collection;

/**
 * @author riste.tutureski
 *
 */
public interface AcmUnmarshaller
{

    public <E> E unmarshall(String source, Class<E> c);

    public <T> T unmarshallCollection(String source, Class<? extends Collection> collectionClass, Class elementClass);
}
