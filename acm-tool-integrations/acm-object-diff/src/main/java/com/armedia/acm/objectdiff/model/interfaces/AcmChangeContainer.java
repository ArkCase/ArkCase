package com.armedia.acm.objectdiff.model.interfaces;

import com.armedia.acm.objectdiff.model.AcmChange;

import java.util.List;

/**
 * Interface which all containers which is not leaf and have list of changes should implement
 */
public interface AcmChangeContainer
{
    List<AcmChange> getChanges();
}
