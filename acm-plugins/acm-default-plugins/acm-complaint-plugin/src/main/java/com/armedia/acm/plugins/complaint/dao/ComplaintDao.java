package com.armedia.acm.plugins.complaint.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.complaint.model.Complaint;

/**
 * Created by armdev on 4/4/14.
 */
public class ComplaintDao extends AcmAbstractDao<Complaint>
{

    // need an override for Mule to find the method
    @Override
    public Complaint save(Complaint toSave)
    {
        return super.save(toSave);
    }
}
