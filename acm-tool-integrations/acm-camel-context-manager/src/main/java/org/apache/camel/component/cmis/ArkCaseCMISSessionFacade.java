package org.apache.camel.component.cmis;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.camelcontext.basic.auth.CamelBasicAuthenticationHttpInvoker;

import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ArkCaseCMISSessionFacade extends CMISSessionFacade
{
    private Logger log = LogManager.getLogger(getClass());
    private Map<String, Object> messageProperties;
    private String arkcaseCmisUrl;

    public ArkCaseCMISSessionFacade(String cmsUrl, Map<String, Object> messageProperties)
    {
        super(cmsUrl);
        arkcaseCmisUrl = cmsUrl;
        setMessageProperties(messageProperties);
    }

    @Override
    void initSession()
    {
        Map<String, String> parameter = new HashMap<>();
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameter.put(SessionParameter.ATOMPUB_URL, getArkcaseCmisUrl());
        parameter.put(SessionParameter.USER, (String) messageProperties.get("username"));
        parameter.put(SessionParameter.PASSWORD, (String) messageProperties.get("password"));
        parameter.put(SessionParameter.HTTP_INVOKER_CLASS, CamelBasicAuthenticationHttpInvoker.class.getName());
        try
        {
            // "session" field is private in the superclass, with no setters, so we have to resort to reflection
            // to set it.
            FieldUtils.writeField(this, "session",
                    SessionFactoryLocator.getSessionFactory().getRepositories(parameter).get(0).createSession(), true);
        }
        catch (IllegalAccessException e)
        {
            log.error("Could not set session: {}", e.getMessage(), e);
        }
    }

    public String getArkcaseCmisUrl()
    {
        return arkcaseCmisUrl;
    }

    public void setArkcaseCmisUrl(String arkcaseCmisUrl)
    {
        this.arkcaseCmisUrl = arkcaseCmisUrl;
    }

    public Map<String, Object> getMessageProperties()
    {
        return messageProperties;
    }

    public void setMessageProperties(Map<String, Object> messageProperties)
    {
        this.messageProperties = messageProperties;
    }
}
