package com.armedia.acm.services.transcribe.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.transcribe.model.TranscribeItem;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/05/2018
 */
public class TranscribeItemDao extends AcmAbstractDao<TranscribeItem>
{
    @Override
    protected Class<TranscribeItem> getPersistenceClass()
    {
        return TranscribeItem.class;
    }
}
