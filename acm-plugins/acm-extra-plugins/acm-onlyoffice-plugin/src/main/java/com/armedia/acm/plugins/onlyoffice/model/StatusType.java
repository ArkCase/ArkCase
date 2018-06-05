package com.armedia.acm.plugins.onlyoffice.model;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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

import java.util.HashMap;
import java.util.Map;

/**
 * 0 - no document with the key identifier could be found
 * 1 - document is being edited
 * 2 - document is ready for saving
 * 3 - document saving error has occurred
 * 4 - document is closed with no changes
 * 6 - document is being edited, but the current document state is saved
 * 7 - error has occurred while force saving the document
 */
public enum StatusType
{
    NO_DOCUMENT_WITH_ID_FOUND(0),
    BEING_EDITED(1),
    READY_FOR_SAVING(2),
    SAVING_ERROR_OCCURED(3),
    CLOSED_NO_CHANGES(4),
    EDITED_BUT_ALREADY_SAVED(6),
    ERROR_WHILE_SAVING(7);

    private final int value;
    private static final Map<Integer, StatusType> map = new HashMap<>();

    StatusType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static StatusType from(int value)
    {
        return map.get(value);
    }

    static
    {
        for (StatusType statusType : StatusType.values())
        {
            map.put(statusType.value, statusType);
        }
    }
}
