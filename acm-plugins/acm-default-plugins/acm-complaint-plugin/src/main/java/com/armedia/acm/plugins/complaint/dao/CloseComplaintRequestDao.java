package com.armedia.acm.plugins.complaint.dao;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;

/**
 * Created by armdev on 10/17/14.
 */
public class CloseComplaintRequestDao extends AcmAbstractDao<CloseComplaintRequest>
{
	private Logger LOG = LoggerFactory.getLogger(getClass());
	
    @Override
    protected Class<CloseComplaintRequest> getPersistenceClass()
    {
        return CloseComplaintRequest.class;
    }
    
    public CloseComplaintRequest findByComplaintId(Long complaintId)
    {
    	
    	CloseComplaintRequest result = null;
    	
    	CriteriaBuilder builder = getEm().getCriteriaBuilder();
    	
    	CriteriaQuery<CloseComplaintRequest> query = builder.createQuery(CloseComplaintRequest.class);
    	Root<CloseComplaintRequest> closeComplaintRequest = query.from(CloseComplaintRequest.class);
    	
    	query.select(closeComplaintRequest);
    	
    	query.where(
    			builder.and(
    					builder.equal(closeComplaintRequest.<Long>get("complaintId"), complaintId)
    			)
    	);
    	
    	TypedQuery<CloseComplaintRequest> dbQuery = getEm().createQuery(query);
    	
    	try
    	{
    		result = dbQuery.getSingleResult();
    	}
    	catch(Exception e)
    	{
    		LOG.info("There is no any CloseComplaintRequest connected with complaint id " + complaintId);
    	}
    	
    	return result;
    }
}
