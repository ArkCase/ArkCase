/**
 * 
 */
package com.armedia.acm.form.project.service;

/*-
 * #%L
 * ACM Forms: Project
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.form.project.model.ProjectConstants;
import com.armedia.acm.form.project.model.ProjectForm;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormFactory;
import com.armedia.acm.plugins.casefile.model.CaseFile;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * @author riste.tutureski
 *
 */
public class ProjectFactory extends FrevvoFormFactory
{

    private Logger LOG = LogManager.getLogger(getClass());

    public CaseFile asAcmCaseFile(ProjectForm form, CaseFile caseFile)
    {
        LOG.debug("Converting Frevvo form to Case file ...");

        if (caseFile == null)
        {
            caseFile = new CaseFile();
        }

        caseFile.setTitle(form.getProjectTitle());
        caseFile.setCaseType(ProjectConstants.PROJECT);
        caseFile.setParticipants(
                getParticipants(caseFile.getParticipants(), form.getParticipants(), form.getOwningGroup(), caseFile.getObjectType()));

        return caseFile;
    }

    public ProjectForm asFrevvoProjectForm(CaseFile caseFile, ProjectForm form, FrevvoFormAbstractService formService)
    {
        LOG.debug("Converting Case file to Frevvo form ...");

        try
        {
            if (caseFile != null && form != null)
            {
                form.setId(caseFile.getId());
                form.setProjectTitle(caseFile.getTitle());
                form.setOwningGroup(asFrevvoGroupParticipant(caseFile.getParticipants()));
                form.setParticipants(asFrevvoParticipants(caseFile.getParticipants()));

                String cmisFolderId = formService.findFolderIdForAttachments(caseFile.getContainer(), caseFile.getObjectType(),
                        caseFile.getId());
                form.setCmisFolderId(cmisFolderId);
            }
        }
        catch (Exception e)
        {
            LOG.error("Cannot convert Object to Frevvo form.", e);
        }

        return form;
    }

}
