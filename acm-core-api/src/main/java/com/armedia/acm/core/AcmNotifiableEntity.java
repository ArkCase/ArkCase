package com.armedia.acm.core;

import java.util.Set;

/**
 * @author ncuculova
 *
 */
public interface AcmNotifiableEntity
{
    Set<AcmNotificationReceiver> getReceivers();

    String getNotifiableEntityTitle();
}
