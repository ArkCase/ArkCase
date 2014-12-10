package com.armedia.acm.plugins.person.dao;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.PersonIdentification;

/**
 * Created by marjan.stefanoski on 09.12.2014.
 */
public class PersonIdentificationDao extends AcmAbstractDao<PersonIdentification> {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
    @Override
    protected Class<PersonIdentification> getPersistenceClass() {
        return PersonIdentification.class;
    }
    
    public PersonIdentification findByPersonIdAndType(Long personId, String type)
    {
    	PersonIdentification result = null;
    	
    	CriteriaBuilder builder = getEm().getCriteriaBuilder();
    	CriteriaQuery<PersonIdentification> query = builder.createQuery(PersonIdentification.class);
    	Root<PersonIdentification> personIdentification = query.from(PersonIdentification.class);
    	
    	query.select(personIdentification);
    	
    	query.where(
    			builder.and(
    					builder.equal(personIdentification.get("person").<Long>get("id"), personId)
    			),
    			builder.and(
    					builder.equal(personIdentification.<String>get("identificationType"), type)
    			)
    	);
    	
    	TypedQuery<PersonIdentification> dbQuery = getEm().createQuery(query);
    	
    	try
    	{
    		result = dbQuery.getSingleResult();
    	}
    	catch(Exception e)
    	{
    		LOG.warn("There is no any results.");
    	}
    	
    	return result;
    }
}
