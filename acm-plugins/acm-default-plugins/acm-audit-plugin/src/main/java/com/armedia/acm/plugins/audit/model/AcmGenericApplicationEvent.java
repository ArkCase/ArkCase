package com.armedia.acm.plugins.audit.model;

import com.armedia.acm.core.model.AcmEvent;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Dec, 2019
 */
public class AcmGenericApplicationEvent extends AcmEvent
{
    public AcmGenericApplicationEvent(Object source)
    {
        super(source);
    }
}
