package com.armedia.acm.service.outlook.service.impl;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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
