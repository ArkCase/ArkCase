/**
 * 
 */
package com.armedia.acm.frevvo.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.form.config.Item;
import com.armedia.acm.form.config.xml.ApproverItem;
import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

/**
 * @author riste.tutureski
 *
 */
public class FrevvoFormFactory {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private UserDao userDao;
	private AcmParticipantDao acmParticipantDao;

	public AcmUser getUser(String userId)
	{	
		AcmUser user = null;
		
		try
		{
			user = getUserDao().findByUserId(userId);			
		}
		catch(Exception e)
		{
			LOG.error("Could not retrive user.", e);
		}
		
		return user;
	}
	
	public List<AcmParticipant> asAcmParticipants(List<ApproverItem> items)
    {
    	List<AcmParticipant> participants = new ArrayList<>();
        
    	if ( items != null )
    	{
    		LOG.debug("# of incoming approvers: " + items.size());
    		
    		for ( Item item : items )
    		{
                AcmParticipant participant = null;
                
                if (item.getId() != null)
                {
                	participant = getAcmParticipantDao().find(item.getId());
                }
                
                if (participant == null)
                {
                	participant = new AcmParticipant();
                }
                
                participant.setId(item.getId());
                participant.setParticipantLdapId(item.getValue());
                participant.setParticipantType(ParticipantTypes.APPROVER);
                participants.add(participant);
    		}
    	}
    	
    	return participants;
    }
	
	public List<ApproverItem> asFrevvoApprovers(List<AcmParticipant> participants)
	{
		List<ApproverItem> items = new ArrayList<>();
		
		if (participants != null)
		{
			LOG.debug("# of incoming participants: " + participants.size());
			
			for (AcmParticipant participant : participants)
			{
				ApproverItem item = new ApproverItem();
				
				item.setId(participant.getId());
				item.setValue(participant.getParticipantLdapId());
				
				items.add(item);
			}
		}
		
		return items;
	}
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public AcmParticipantDao getAcmParticipantDao() {
		return acmParticipantDao;
	}

	public void setAcmParticipantDao(AcmParticipantDao acmParticipantDao) {
		this.acmParticipantDao = acmParticipantDao;
	}	
}
