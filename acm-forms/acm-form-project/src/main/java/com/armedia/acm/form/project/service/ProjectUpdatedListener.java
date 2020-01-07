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

import com.armedia.acm.form.casefile.model.CaseFileFormConfig;
import com.armedia.acm.form.project.model.ProjectForm;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.plugins.casefile.model.CaseEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

public class ProjectUpdatedListener implements ApplicationListener<CaseEvent>
{

    private final Logger LOG = LogManager.getLogger(getClass());

    private CaseFileFormConfig caseFileFormConfig;
    private FrevvoFormService projectService;

    @Override
    public void onApplicationEvent(CaseEvent event)
    {
        if ("com.armedia.acm.casefile.updated".equals(event.getEventType().toLowerCase()))
        {
            LOG.debug("Updating Frevvo XML file ...");

            String activeFormName = caseFileFormConfig.getActiveForm();
            if (FrevvoFormName.PROJECT.equals(activeFormName))
            {
                getProjectService().updateXML(event.getCaseFile(), event.getEventUser(), ProjectForm.class);
            }
        }
    }

    public FrevvoFormService getProjectService()
    {
        return projectService;
    }

    public void setProjectService(ProjectService projectService)
    {
        this.projectService = projectService;
    }

    public CaseFileFormConfig getCaseFileFormConfig()
    {
        return caseFileFormConfig;
    }

    public void setCaseFileFormConfig(CaseFileFormConfig caseFileFormConfig)
    {
        this.caseFileFormConfig = caseFileFormConfig;
    }
}
