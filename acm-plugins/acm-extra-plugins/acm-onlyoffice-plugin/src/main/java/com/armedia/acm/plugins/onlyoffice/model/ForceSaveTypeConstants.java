package com.armedia.acm.plugins.onlyoffice.model;

public interface ForceSaveTypeConstants
{

    /**
     * 0 - the force saving request is performed to the command service
     */
    int BY_COMMAND_SERVICE = 0;
    /**
     * 1 - the force saving request is performed each time the saving is done (e.g. the Save button is clicked), which
     * * is only available when the forcesave option is set to true.
     */
    int BY_SAVING_IS_DONE = 1;
    /**
     * 2 - the force saving request is performed by timer with the settings from the server config.
     */
    int BY_TIMER = 2;

}
