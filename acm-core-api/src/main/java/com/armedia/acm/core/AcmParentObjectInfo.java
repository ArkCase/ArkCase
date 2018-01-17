package com.armedia.acm.core;

/**
 * Marker interface to identify business objects. Each POJO that has parent must implement this interface.
 */
public interface AcmParentObjectInfo
{
    Long getParentObjectId();

    String getParentObjectType();
}
