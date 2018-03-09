package com.armedia.acm.services.transcribe.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.transcribe.model.Transcribe;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/05/2018
 */
public class TranscribeDao extends AcmAbstractDao<Transcribe>
{
    @Override
    protected Class<Transcribe> getPersistenceClass()
    {
        return Transcribe.class;
    }
}
