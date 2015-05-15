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
import com.armedia.acm.form.config.xml.OwningGroupItem;
import com.armedia.acm.form.config.xml.ParticipantItem;
import com.armedia.acm.frevvo.model.FrevvoFormConstants;
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
				
				AcmUser user = getUser(participant.getParticipantLdapId());
				
				if (user != null)
				{
					item.setApproverName(user.getFullName());
				}
				
				items.add(item);
			}
		}
		
		return items;
	}
	
	public List<AcmParticipant> asAcmParticipants(List<ParticipantItem> items, OwningGroupItem groupItem, String formName)
	{
		List<AcmParticipant> participants = null;
		if (items != null)
		{
			participants = new ArrayList<>();
			for (ParticipantItem item : items)
			{
				AcmParticipant participant = new AcmParticipant();
				
				participant.setId(item.getId());
				participant.setObjectType(formName.toUpperCase());
				participant.setParticipantLdapId(item.getValue());
				participant.setParticipantType(item.getType());
				
				participants.add(participant);
			}
		}
		
		// THIS PART OF CODE WILL BE REMOVED ONCE WE IMPLEMENT GROUP PICKER ON FREVVO SIDE
		if (groupItem != null)
		{
			if (participants == null)
			{
				participants = new ArrayList<>();
			}
			
			AcmParticipant participant = new AcmParticipant();
			
			participant.setId(groupItem.getId());
			participant.setObjectType(formName.toUpperCase());
			participant.setParticipantLdapId(groupItem.getValue());
			participant.setParticipantType(groupItem.getType());
			
			participants.add(participant);
		}
		
		return participants;
	}
	
	public List<ParticipantItem> asFrevvoParticipants(List<AcmParticipant> participants)
	{
		if (participants != null)
		{
			List<ParticipantItem> items = new ArrayList<>();
			
			for (AcmParticipant participant : participants)
			{
				if (!FrevvoFormConstants.DEFAULT_USER.equals(participant.getParticipantType()) &&
					!FrevvoFormConstants.OWNING_GROUP.equals(participant.getParticipantType()))
				{
					ParticipantItem item = new ParticipantItem();
					
					item.setId(participant.getId());
					item.setType(participant.getParticipantType());
					item.setValue(participant.getParticipantLdapId());
					
					AcmUser user = getUser(participant.getParticipantLdapId());
					
					if (user != null)
					{
						item.setName(user.getFullName());
					}
					
					items.add(item);
				}
			}
			
			return items;
		}
		
		return null;
	}
	
	/**
	 * THIS METHOD WILL BE REMOVED ONCE WE IMPLEMENT GROUP PICKER ON FREVVO SIDE
	 * 
	 * @param participant
	 * @return
	 */
	public OwningGroupItem asFrevvoGroupParticipant(List<AcmParticipant> participants)
	{
		if (participants != null)
		{
			for (AcmParticipant participant : participants)
			{
				if (!FrevvoFormConstants.DEFAULT_USER.equals(participant.getParticipantType()) &&
					 FrevvoFormConstants.OWNING_GROUP.equals(participant.getParticipantType()))
				{
					OwningGroupItem item = new OwningGroupItem();
					
					item.setId(participant.getId());
					item.setType(participant.getParticipantType());
					item.setValue(participant.getParticipantLdapId());
					
					return item;
				}
			}
		}
		
		return null;
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
