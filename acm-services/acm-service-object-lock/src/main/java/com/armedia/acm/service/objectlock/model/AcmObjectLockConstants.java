package com.armedia.acm.service.objectlock.model;

/**
 * Created by dragan.simonovski on 04/25/2016.
 */
public interface AcmObjectLockConstants
{
    String WORD_EDIT_LOCK = "WORD_EDIT_LOCK";
    String CHECKOUT_LOCK = "CHECKOUT_LOCK";
    String CHECKIN_LOCK = "CHECKIN_LOCK";
    String CANCEL_LOCK = "CANCEL_LOCK";
    String OBJECT_LOCK = "OBJECT_LOCK";
    /**
     * this should be used when objects needs to be locked for long running tasks.
     */
    String LOCK_FOR_PROCESSING = "OBJECT_PROCESSING_LOCK";

    String EXCLUSIVE_TREE_LOCK = "EXCLUSIVE_TREE_LOCK";
}
