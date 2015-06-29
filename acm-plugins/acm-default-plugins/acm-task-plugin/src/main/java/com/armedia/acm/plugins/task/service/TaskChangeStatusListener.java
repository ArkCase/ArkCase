package com.armedia.acm.plugins.task.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
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
	
	@Override
	public void onApplicationEvent(AcmApplicationTaskEvent event) {
		LOG.debug("Task event raised. Start coppieng folder to the parent folder ...");
		
		if (event != null){
			
			boolean execute = checkExecution(event.getEventType());
			
			if (execute){
				
				AcmTask task = (AcmTask) event.getSource();
				try {

					if(null != task.getParentObjectType() && null != task.getParentObjectId()){

						AcmContainer container = task.getContainer() != null ? task.getContainer() : getAcmContainerDao().findFolderByObjectTypeAndId(task.getObjectType(), task.getId());
						
						AcmFolder folderToBeCoppied = container.getFolder();
						
						AcmContainer targetContainer = getAcmContainerDao().findFolderByObjectTypeAndId(task.getParentObjectType(), task.getParentObjectId());

						AcmFolder targetFolder = getAcmFolderService().addNewFolderByPath(task.getParentObjectType(), 
								task.getParentObjectId(), 
								"/" + String.format("Task %d%n %s", task.getId(), task.getTitle()));
						
						getAcmFolderService().copyFolderStructure(folderToBeCoppied.getId(), targetContainer, targetFolder);

					
					}
					
				} catch (AcmFolderException | AcmCreateObjectFailedException | AcmUserActionFailedException | AcmObjectNotFoundException e) {
					
					LOG.error("Could not coppy folder for task id = " + task.getId(), e);
					
				} 

			}
		}
	}
	
	private boolean checkExecution(String eventType)
	{		
		if ("com.armedia.acm.app.task.complete".equals(eventType))
		{
			return true;
		}
		
		return false;
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

	

}
