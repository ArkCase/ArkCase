package com.armedia.acm.core;

/*-
 * #%L
 * ACM Core API
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

/**
 * Marker interface to identify business objects. Each POJO that represents a business object must implement this
 * interface.
 */
public interface AcmObject
{
    /**
     * Specify what type of object this is; determines the data access control, business processes, and
     * business rules to be applied to this object. There is not a one-to-one mapping from the class name to the
     * object type. For example, a document's object type is the form type (a "Close Case Request" document's type is
     * "Close Case Request", not "Document").
     * <p/>
     * The value returned must match the "name" property for an AcmObjectType bean in the war project; otherwise
     * the system will not know which access controls, business rules, or business processes to apply.
     *
     * @return The object type; there should be an AcmObjectType bean whose 'name' property is set to this value.
     */
    String getObjectType();

    Long getId();

}
