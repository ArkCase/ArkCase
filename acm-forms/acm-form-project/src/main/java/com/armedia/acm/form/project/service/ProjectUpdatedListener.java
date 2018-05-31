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

import com.armedia.acm.form.project.model.ProjectForm;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.Properties;

public class ProjectUpdatedListener implements ApplicationListener<CaseEvent>
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private Properties properties;
    private FrevvoFormService projectService;

    @Override
    public void onApplicationEvent(CaseEvent event)
    {
        if ("com.armedia.acm.casefile.updated".equals(event.getEventType().toLowerCase()))
        {
            LOG.debug("Updating Frevvo XML file ...");

            if (getProperties() != null)
            {
                if (getProperties().containsKey(CaseFileConstants.ACTIVE_CASE_FORM_KEY))
                {
                    String activeFormName = (String) getProperties().get(CaseFileConstants.ACTIVE_CASE_FORM_KEY);

                    if (FrevvoFormName.PROJECT.equals(activeFormName))
                    {
                        getProjectService().updateXML(event.getCaseFile(), event.getEventUser(), ProjectForm.class);
                    }
                }
            }
        }
    }

    public Properties getProperties()
    {
        return properties;
    }

    public void setProperties(Properties properties)
    {
        this.properties = properties;
    }

    public FrevvoFormService getProjectService()
    {
        return projectService;
    }

    public void setProjectService(ProjectService projectService)
    {
        this.projectService = projectService;
    }

}
