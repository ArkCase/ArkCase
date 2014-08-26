package com.armedia.acm.services.signature.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.signature.model.Signature;

public class SignatureDao extends AcmAbstractDao<Signature>{

    @Override
    protected Class<Signature> getPersistenceClass()
    {
        return Signature.class;
    }
}
