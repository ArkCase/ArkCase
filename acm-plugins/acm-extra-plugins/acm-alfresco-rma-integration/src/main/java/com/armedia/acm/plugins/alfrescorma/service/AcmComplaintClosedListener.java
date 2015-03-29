/**
 * 
 */
package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintClosedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * @author riste.tutureski
 *
 */
public class AcmComplaintClosedListener implements ApplicationListener<ComplaintClosedEvent> {

	private transient Logger LOG = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;

	@Override
	public void onApplicationEvent(ComplaintClosedEvent event) {
		
		if ( LOG.isTraceEnabled() )
        {
			LOG.trace("Got a complaint closed event; complaint id: '" + event.getObjectId() + "'");
        }

        if ( ! event.isSucceeded() )
        {
            if ( LOG.isTraceEnabled() )
            {
            	LOG.trace("Returning - closing complaint was not successful");
            }

            return;
        }
        
        Complaint complaint = (Complaint) event.getSource();


        
        if (null != complaint)
        {
			UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(event.getUserId(), event.getUserId());
            getAlfrescoRecordsService().declareAllContainerFilesAsRecords(auth, complaint.getContainer(),
                    event.getEventDate(), complaint.getComplaintNumber());
        }
		
	}

    public AlfrescoRecordsService getAlfrescoRecordsService()
    {
        return alfrescoRecordsService;
    }

    public void setAlfrescoRecordsService(AlfrescoRecordsService alfrescoRecordsService)
    {
        this.alfrescoRecordsService = alfrescoRecordsService;
    }
}
