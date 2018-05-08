package com.armedia.acm.objectdiff.model.interfaces;

/**
 * Interface which all leafs should implement
 */
public interface AcmChangeDisplayable
{
    String getOldValue();

    void setOldValue(String oldValue);

    String getNewValue();

    void setNewValue(String oldValue);
}
