package com.armedia.acm.plugins.category.model;

/*-
 * #%L
 * ACM Default Plugin: Categories
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

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 16, 2017
 *
 */
public class CategoryEvent extends AcmEvent
{

    private static final long serialVersionUID = 3652276020071035651L;

    public CategoryEvent(Category source, CategoryEventType eventType, String description)
    {

        this(source, eventType, description, true);

    }

    public CategoryEvent(Category source, CategoryEventType eventType, String description, boolean userActionSucceeded)
    {

        super(source);

        setSucceeded(userActionSucceeded);
        setObjectId(source.getId());
        setEventDate(source.getModified());
        setUserId(source.getModifier());
        setObjectType(source.getObjectType());
        setEventType(eventType.type());
        setEventDescription(description);

    }

    public enum CategoryEventType
    {

        CREATE("com.armedia.acm.plugins.category.model.action.create"),
        EDIT("com.armedia.acm.plugins.category.model.action.edit"),
        DELETE(
                "com.armedia.acm.plugins.category.model.action.delete"),
        ACTIVATE(
                "com.armedia.acm.plugins.category.model.action.activate"),
        DEACTIVATE(
                "com.armedia.acm.plugins.category.model.action.deactivate");

        private String actionType;

        private CategoryEventType(String actionType)
        {
            this.actionType = actionType;
        }

        public String type()
        {
            return actionType;
        }
    }

}
