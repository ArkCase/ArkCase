package com.armedia.acm.services.transcribe.model;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public enum TranscribeActionType
{
    CREATED,
    UPDATED,
    QUEUED,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED,
    COMPILED,
    ROLLBACK,
    PROVIDER_FAILED
}
