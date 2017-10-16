package com.armedia.acm.objectdiff.model.interfaces;

/**
 * Interface which all leafs should implement
 */
public interface AcmChangeDisplayable
{
    String getOldValue();

    String getNewValue();

    void setOldValue(String oldValue);

    void setNewValue(String oldValue);
}
