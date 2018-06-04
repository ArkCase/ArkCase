package com.armedia.acm.plugins.onlyoffice.model;

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
