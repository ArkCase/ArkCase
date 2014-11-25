package com.armedia.acm.plugins.alfrescorma.service;

import java.util.Collection;
import java.util.Date;

import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.plugins.alfrescorma.model.AcmRecord;
import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;

public class AcmCaseFileStatusChangedListener implements ApplicationListener<CaseEvent> {

	private transient Logger LOG = LoggerFactory.getLogger(getClass());
	private EcmFileDao ecmFileDao;
	
	@Override
	public void onApplicationEvent(CaseEvent event) {
		if ("com.armedia.acm.casefile.event.statuschanged".equals(event.getEventType()))
		{
			// TODO: Record Management Strategy for Case File
			
			/*CaseFile caseFile = event.getCaseFile();
			
			 if (null != caseFile)
		     {
				 Collection<ObjectAssociation> associations =  caseFile.getChildObjects();
				 
				 if (null != associations && associations.size() > 0)
	        	{
	        		for (ObjectAssociation association : associations)
	        		{
	        			if ("FILE".equals(association.getTargetType()))
	        			{
	        				try
	        				{
		        				EcmFile file = ecmFileDao.find(association.getTargetId());
		        				
		        				AcmRecord record = new AcmRecord();
		        				
		        				record.setEcmFileId(file.getEcmFileId());
		        		        record.setCategoryFolder("Case Files");
		        		        record.setOriginatorOrg("Armedia LLC");
		        		        record.setOriginator(file.getModifier());
		        		        record.setPublishedDate(new Date());
		        		        record.setReceivedDate(event.getEventDate());
		        		        record.setRecordFolder(association.getParentName());
		        		        
		        		        try
		        		        {
		        		            if ( LOG.isTraceEnabled() )
		        		            {
		        		                LOG.trace("Sending JMS message.");
		        		            }
		        		            
		        		            getMuleClient().dispatch("jms://rmaRecord.in", record, null);
		        		            
		        		            if ( LOG.isTraceEnabled() )
		        		            {
		        		                LOG.trace("Done");
		        		            }

		        		        }
		        		        catch (MuleException e)
		        		        {
		        		            LOG.error("Could not create RMA folder: " + e.getMessage(), e);
		        		        }
		        		        
	        				}
	        				catch(Exception e)
	        				{
	        					LOG.error("Cannot finish Record Management Strategy for file with id=" + association.getTargetId(), e);
	        				}
	        			}
	        		}
	        	}
		     }*/
		}
	}
	
	public MuleClient getMuleClient()
    {
        return null;  // this method should be overridden by Spring method injection
    }

	public EcmFileDao getEcmFileDao() {
		return ecmFileDao;
	}

	public void setEcmFileDao(EcmFileDao ecmFileDao) {
		this.ecmFileDao = ecmFileDao;
	}

}
