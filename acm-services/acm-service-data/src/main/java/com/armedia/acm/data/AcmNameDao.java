package com.armedia.acm.data;

import com.armedia.acm.core.AcmObject;

public interface AcmNameDao
{
    AcmObject findByName(String name);
}