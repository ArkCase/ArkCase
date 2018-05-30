package com.armedia.acm.plugins.person.model;

/*-
 * #%L
 * ACM Default Plugin: Person
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


import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * Created by armdev on 8/19/14.
 */
public abstract class PersonAssociationPersistenceEvent extends AcmEvent
{
    private static final String OBJECT_TYPE = "PERSON-ASSOCIATION";

    public PersonAssociationPersistenceEvent(PersonAssociation source)
    {
        super(source);
        setObjectId(source.getId());
        setEventDate(new Date());
        setUserId(source.getModifier());

    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

}
