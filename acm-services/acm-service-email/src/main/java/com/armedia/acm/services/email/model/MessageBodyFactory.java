package com.armedia.acm.services.email.model;

import com.armedia.acm.services.email.service.TemplatingEngine;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

/*-
 * #%L
 * ACM Service: Email
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

import java.util.HashMap;
import java.util.Map;

import freemarker.template.TemplateException;

/**
 * Template factory for email notifications
 * 
 * @author dame.gjorgjievski
 *
 */
public class MessageBodyFactory
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private static final String DEFAULT_TEMPLATE = "${model.header} \n\n ${model.body} \n\n\n ${model.footer}";

    private String template;
    private String parentType;
    private String parentNumber;
    private String modelReferenceName;

    private TemplatingEngine templatingEngine;

    public MessageBodyFactory()
    {
    }

    public MessageBodyFactory(String template)
    {
        this.template = template;
    }

    public String getTemplate()
    {
        return template != null ? template : DEFAULT_TEMPLATE;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }

    /**
     * Builds message body string with bound model content from template
     * 
     * @param model
     * @return
     */
    public String buildMessageBodyFromTemplate(String body, String header, String footer)
    {
        Map<String, Object> model = new HashMap<>();
        model.put("header", header);
        model.put("body", body);
        model.put("footer", footer);
        if (getTemplatingEngine() == null || this.template.contains("${body}") || this.template.contains("${model.body}"))
        {
            return buildMessageBodyFromTemplate(model);
        }
        else
        {
            // New FreeMarker template
            try
            {
                return templatingEngine.process(this.template, this.modelReferenceName, this);
            }
            catch (TemplateException | IOException e)
            {
                LOG.error("Failed to process template!", e);
                return buildMessageBodyFromTemplate(model);
            }
        }
    }

    /**
     * Builds message body string with bound model content from template
     * 
     * @param model
     * @return
     */
    private String buildMessageBodyFromTemplate(Map<String, Object> model)
    {

        if (!model.containsKey("header"))
        {
            model.put("header", "");
        }
        if (!model.containsKey("body"))
        {
            model.put("body", "");
        }
        if (!model.containsKey("footer"))
        {
            model.put("footer", "");
        }

        String template = new String(getTemplate());
        for (Map.Entry<String, Object> entry : model.entrySet())
        {
            template = template.replace("${model." + entry.getKey() + "}", entry.getValue() != null ? entry.getValue().toString() : "");
        }
        return template;
    }

    public TemplatingEngine getTemplatingEngine()
    {
        return templatingEngine;
    }

    public void setTemplatingEngine(TemplatingEngine templatingEngine)
    {
        this.templatingEngine = templatingEngine;
    }

    public String getParentNumber()
    {
        return parentNumber;
    }

    public void setParentNumber(String parentNumber)
    {
        this.parentNumber = parentNumber;
    }

    public String getParentType()
    {
        return parentType;
    }

    public void setParentType(String parentType)
    {
        this.parentType = parentType;
    }

    public String getModelReferenceName()
    {
        return modelReferenceName;
    }

    public void setModelReferenceName(String modelReferenceName)
    {
        this.modelReferenceName = modelReferenceName;
    }

}
