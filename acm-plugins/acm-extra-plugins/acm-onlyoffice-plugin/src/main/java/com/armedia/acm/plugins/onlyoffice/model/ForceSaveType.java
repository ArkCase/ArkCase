package com.armedia.acm.plugins.onlyoffice.model;

/**
 * 0 - the force saving request is performed to the command service
 * 1 - the force saving request is performed each time the saving is done (e.g. the Save button is clicked), which
 * is only available when the forcesave option is set to true.
 * 2 - the force saving request is performed by timer with the settings from the server config.
 */
public enum ForceSaveType
{
    BY_COMMAND_SERVICE(0),
    BY_SAVING_IS_DONE(1),
    BY_TIMER(2);
    private final int value;

    ForceSaveType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}