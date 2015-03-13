package com.armedia.acm.plugins.casefile.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.casefile.model.Disposition;

/**
 * Created by marjan.stefanoski on 09.03.2015.
 */
public class DispositionDao extends AcmAbstractDao<Disposition> {

    @Override
    protected Class<Disposition> getPersistenceClass() {
        return Disposition.class;
    }
}
