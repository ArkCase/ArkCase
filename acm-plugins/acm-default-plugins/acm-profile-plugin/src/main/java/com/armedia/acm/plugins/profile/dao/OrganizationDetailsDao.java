package com.armedia.acm.plugins.profile.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.profile.model.OrganizationDetails;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class OrganizationDetailsDao extends AcmAbstractDao<OrganizationDetails> {


    @Transactional
    public OrganizationDetails updateOrganizationDetails(OrganizationDetails details){
        details = getEm().merge(details);
        return details;
    }

    @Override
    protected Class<OrganizationDetails> getPersistenceClass() {
        return OrganizationDetails.class;
    }
}
