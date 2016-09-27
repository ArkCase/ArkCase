package com.armedia.broker;

import java.io.Serializable;

/**
 * Handling interface for received objects from message broker
 * 
 * @author dame.gjorgjievski
 *
 */
public interface AcmObjectBrokerHandler<E extends Serializable>
{
    /**
     * Handle for received objects
     * 
     * @param entity
     * @return handling result, true if handled successfully, false otherwise
     */
    public boolean handleObject(E entity);
}
