/**
 *
 */
package com.armedia.acm.form.casefile.service;

/*-
 * #%L
 * ACM Forms: Case File
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
import com.armedia.acm.form.config.FormsTypeCheckService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.plugins.casefile.model.CaseEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.Properties;

/**
 * @author riste.tutureski
 */
public class CaseFileUpdatedListener implements ApplicationListener<CaseEvent>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private CaseFileFormConfig formConfig;
    private Properties properties;
    private FrevvoFormService caseFileService;
    private FormsTypeCheckService formsTypeCheckService;

    @Override
    public void onApplicationEvent(CaseEvent event)
    {

        if (formsTypeCheckService.getTypeOfForm().equals("frevvo"))
        {
            if ("com.armedia.acm.casefile.created".equals(event.getEventType().toLowerCase())
                    || "com.armedia.acm.casefile.updated".equals(event.getEventType().toLowerCase()))
            {
                LOG.debug("Updating Frevvo XML file ...");

                try
                {
                    if (FrevvoFormName.CASE_FILE.equals(formConfig.getActiveForm()))
                    {
                        getCaseFileService().updateXML(event.getCaseFile(), event.getEventUser(), getCaseFileService().getFormClass());
                    }
                }
                catch (Exception e)
                {
                    LOG.error(String.format("Could not update Frevvo form XML: [%s]", e.getMessage()), e);
                }
            }
        }
    }

    public void setFormsTypeCheckService(FormsTypeCheckService formsTypeCheckService)
    {
        this.formsTypeCheckService = formsTypeCheckService;
    }

    public Properties getProperties()
    {
        return properties;
    }

    public void setProperties(Properties properties)
    {
        this.properties = properties;
    }

    public FrevvoFormService getCaseFileService()
    {
        return caseFileService;
    }

    public void setCaseFileService(CaseFileService caseFileService)
    {
        this.caseFileService = caseFileService;
    }

    public CaseFileFormConfig getFormConfig()
    {
        return formConfig;
    }

    public void setFormConfig(CaseFileFormConfig formConfig)
    {
        this.formConfig = formConfig;
    }
}
