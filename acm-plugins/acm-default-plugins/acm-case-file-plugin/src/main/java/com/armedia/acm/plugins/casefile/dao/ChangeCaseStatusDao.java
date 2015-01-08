/**
 * 
 */
package com.armedia.acm.plugins.casefile.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.services.participants.model.AcmParticipant;

/**
 * @author riste.tutureski
 *
 */
public class ChangeCaseStatusDao extends AcmAbstractDao<ChangeCaseStatus>
{
	private Logger LOG = LoggerFactory.getLogger(getClass());

	@Override
	protected Class<ChangeCaseStatus> getPersistenceClass() 
	{
		return ChangeCaseStatus.class;
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
	
	public ChangeCaseStatus findByCaseId(Long caseId)
	{
		ChangeCaseStatus result = null;
		
		CriteriaBuilder builder = getEm().getCriteriaBuilder();
		
		CriteriaQuery<ChangeCaseStatus> query = builder.createQuery(ChangeCaseStatus.class);
		Root<ChangeCaseStatus> changeCaseStatus = query.from(ChangeCaseStatus.class);
		
		query.select(changeCaseStatus);
		
		query.where(
				builder.and(
    					builder.equal(changeCaseStatus.<Long>get("caseId"), caseId)
    			),
    			builder.and(
    					builder.notEqual(changeCaseStatus.<String>get("status"), "APPROVED")
    			)
		);
		
		TypedQuery<ChangeCaseStatus> dbQuery = getEm().createQuery(query);
		
		try
    	{
    		result = dbQuery.getSingleResult();
    	}
    	catch(Exception e)
    	{
    		LOG.info("There is no any results.");
    	}
    	
    	return result;
	}
}
