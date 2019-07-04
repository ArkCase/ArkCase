package com.armedia.acm.correspondence.web.api;

/*-
 * #%L
 * ACM Service: Correspondence Library
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

import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.service.CorrespondenceService;
import com.armedia.acm.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/service/correspondence", "/api/latest/service/correspondence" })
public class ListCorrespondenceTemplatesAPIController
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private CorrespondenceService correspondenceService;
    private SpringContextHolder contextHolder;

    @RequestMapping(value = "/listTemplates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> listTemplates(
            Authentication auth,
            HttpSession session)
    {
        Collection<CorrespondenceTemplate> templates = getContextHolder().getAllBeansOfType(CorrespondenceTemplate.class).values();

        List<String> retval = new ArrayList<>();

        for (CorrespondenceTemplate template : templates)
        {
            retval.add(template.getTemplateFilename());
        }

        Collections.sort(retval);

        return retval;
    }

    @RequestMapping(value = "/listTemplateModelProviders", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> listTemplateModelProviders()
    {
        return getCorrespondenceService().listTemplateModelProviders();
    }


    @RequestMapping(value = "/listAllProperties", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getTemplateModelProviderDeclaredFields(String classPath)
    {
        return getCorrespondenceService().getTemplateModelProviderDeclaredFields(classPath);
    }

    public SpringContextHolder getContextHolder()
    {
        return contextHolder;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }

    public CorrespondenceService getCorrespondenceService()
    {
        return correspondenceService;
    }

    public void setCorrespondenceService(CorrespondenceService correspondenceService)
    {
        this.correspondenceService = correspondenceService;
    }
}
