package com.armedia.acm.plugins.onlyoffice.model;

public interface StatusConstants
{
    /**
     * 0 - no document with the key identifier could be found,
     */
    int NO_DOCUMENT_WITH_ID_FOUND = 0;
    /**
     * 1 - document is being edited,
     */
    int BEING_EDITED = 1;
    /**
     * 2 - document is ready for saving,
     */
    int READY_FOR_SAVING = 2;
    /**
     * 3 - document saving error has occurred
     */
    int SAVING_ERROR_OCCURED = 3;
    /**
     * 4 - document is closed with no changes
     */
    int CLOSED_NO_CHANGES = 4;
    /**
     * 6 - document is being edited, but the current document state is saved
     */
    int EDITED_BUT_ALREADY_SAVED = 6;
    /**
     * 7 - error has occurred while force saving the document
     */
    int ERROR_WHILE_SAVING = 7;

}
