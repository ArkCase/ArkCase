package com.armedia.acm.data;

/*-
 * #%L
 * Tool Integrations: Spring Data Source
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

import java.util.Date;

/**
 * All ACM JPA entity classes must implement this interface. An EclipseLink session
 * listener uses it to automatically set the created, creator, modified, and modifier fields.
 */
public interface AcmEntity
{
    String CREATOR_PROPERTY_NAME = "creator";
    String MODIFIER_PROPERTY_NAME = "modifier";
    String CREATED_PROPERTY_NAME = "created";
    String MODIFIED_PROPERTY_NAME = "modified";

    String getCreator();

    void setCreator(String creator);

    String getModifier();

    void setModifier(String modifier);

    Date getCreated();

    void setCreated(Date created);

    Date getModified();

    void setModified(Date modified);
}
