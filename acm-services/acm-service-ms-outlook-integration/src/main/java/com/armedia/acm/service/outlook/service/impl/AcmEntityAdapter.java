package com.armedia.acm.service.outlook.service.impl;

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmTitleEntity;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;

import java.util.List;
import java.util.Objects;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 22, 2017
 *
 */
public final class AcmEntityAdapter
{

    public static String getTitle(AcmEntity entity)
    {
        Objects.requireNonNull(entity, "'entity' cannot be null.");
        return ((AcmTitleEntity) entity).getTitle();
    }

    public static String getNotifiableEntityTitle(AcmEntity entity)
    {
        Objects.requireNonNull(entity, "'entity' cannot be null.");
        return ((AcmNotifiableEntity) entity).getNotifiableEntityTitle();
    }

    public static AcmContainer getContainer(AcmEntity entity)
    {
        Objects.requireNonNull(entity, "'entity' cannot be null.");
        return ((AcmContainerEntity) entity).getContainer();
    }

    public static List<AcmParticipant> getParticipants(AcmEntity entity)
    {
        Objects.requireNonNull(entity, "'entity' cannot be null.");
        return ((AcmAssignedObject) entity).getParticipants();
    }

}
