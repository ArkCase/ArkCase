/**
 * 
 */
package com.armedia.acm.plugins.casefile.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.services.users.model.AcmParticipant;

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
}
