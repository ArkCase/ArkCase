/**
 * 
 */
package com.armedia.acm.form.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.form.project.model.ProjectConstants;
import com.armedia.acm.form.project.model.ProjectForm;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormFactory;
import com.armedia.acm.plugins.casefile.model.CaseFile;

/**
 * @author riste.tutureski
 *
 */
public class ProjectFactory extends FrevvoFormFactory {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	public CaseFile asAcmCaseFile(ProjectForm form, CaseFile caseFile)
	{
		LOG.debug("Converting Frevvo form to Case file ...");
		
		if (caseFile == null)
		{
			caseFile = new CaseFile();
		}
		
		caseFile.setTitle(form.getProjectTitle());
		caseFile.setCaseType(ProjectConstants.PROJECT);	
		caseFile.setParticipants(getParticipants(caseFile.getParticipants(),form.getParticipants(), form.getOwningGroup(), caseFile.getObjectType()));
		
		return caseFile;
	}
	
	public ProjectForm asFrevvoProjectForm(CaseFile caseFile, ProjectForm form, FrevvoFormAbstractService formService)
	{
		LOG.debug("Converting Case file to Frevvo form ...");
		
		if (caseFile != null && form != null)
		{
			form.setId(caseFile.getId());
			form.setProjectTitle(caseFile.getTitle());
			form.setOwningGroup(asFrevvoGroupParticipant(caseFile.getParticipants()));
			form.setParticipants(asFrevvoParticipants(caseFile.getParticipants()));
			
			String cmisFolderId = formService.findFolderId(caseFile.getContainer(), caseFile.getObjectType(), caseFile.getId());
			form.setCmisFolderId(cmisFolderId);
		}
		
		return form;
	}
	
}
