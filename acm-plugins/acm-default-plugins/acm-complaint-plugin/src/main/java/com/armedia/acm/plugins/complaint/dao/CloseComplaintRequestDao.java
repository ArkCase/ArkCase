package com.armedia.acm.plugins.complaint.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.services.users.model.AcmParticipant;

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
    
    @Transactional
    public int delete(List<AcmParticipant> participants)
    {
    	CriteriaBuilder builder = getEm().getCriteriaBuilder();
    	 
    	CriteriaDelete<AcmParticipant> delete = builder.createCriteriaDelete(AcmParticipant.class);
    	Root<AcmParticipant> acmParticipant = delete.from(AcmParticipant.class);
    	
    	List<Long> ids = new ArrayList<Long>();
    	if (null != participants && participants.size() > 0)
    	{
    		
    		for (AcmParticipant participant : participants)
    		{
    			ids.add(participant.getId());
    		}
    		
    	}
    	
    	delete.where(
    			acmParticipant.<Long>get("id").in(ids)
    	);
    	
    	Query query = getEm().createQuery(delete);
    	
    	return query.executeUpdate();
    }
}
