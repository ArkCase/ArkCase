package com.armedia.acm.plugins.task.service;

import java.util.HashMap;
import java.util.Map;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;

/**
 * 
 * @author nikolche
 *
 */
public class TaskChangeStatusListener implements ApplicationListener<AcmApplicationTaskEvent> {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AcmFolderService acmFolderService;
	private AcmContainerDao acmContainerDao;
	private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
	private MuleClient muleClient;
	
	
	public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter() {
		return auditPropertyEntityAdapter;
	}

	public void setAuditPropertyEntityAdapter(
			AuditPropertyEntityAdapter auditPropertyEntityAdapter) {
		this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
	}

	@Override
	public void onApplicationEvent(AcmApplicationTaskEvent event) {
		
		if (event != null){
			
			boolean execute = checkExecution(event.getEventType());
			
			if (execute){
				
				
				
				try {
					// call Mule flow to create the Alfresco folder
					Map<String, Object> messageProps = new HashMap<>();
					messageProps.put("acmUser", new UsernamePasswordAuthenticationToken(event.getUserId(), ""));
					messageProps.put("auditAdapter", getAuditPropertyEntityAdapter());
					MuleMessage msg = getMuleClient().send("jms://copyTaskFilesAndFoldersToParent.in", event.getAcmTask(), messageProps);
					
					MuleException e = msg.getInboundProperty("executionException");

				} catch (MuleException e) {
					throw new RuntimeException("Error while copying Task documents.", e);
				}
				


			}
		}
	}
	
	private boolean checkExecution(String eventType)
	{		
		
		return "com.armedia.acm.app.task.complete".equals(eventType) || "com.armedia.acm.activiti.task.complete".equals(eventType);
	}

	public AcmFolderService getAcmFolderService() {
		return acmFolderService;
	}

	public void setAcmFolderService(AcmFolderService acmFolderService) {
		this.acmFolderService = acmFolderService;
	}

	public AcmContainerDao getAcmContainerDao() {
		return acmContainerDao;
	}

	public void setAcmContainerDao(AcmContainerDao acmContainerDao) {
		this.acmContainerDao = acmContainerDao;
	}

	public MuleClient getMuleClient() {
		return muleClient;
	}

	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}
	

	

}
