package com.armedia.acm.camelcontext.arkcase.cmis;

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

import org.apache.camel.Endpoint;
import org.apache.camel.component.cmis.CMISComponent;
import org.apache.camel.component.cmis.CMISEndpoint;
import org.apache.camel.component.cmis.CMISSessionFacade;

import java.util.HashMap;
import java.util.Map;

public class ArkCaseCMISComponent extends CMISComponent
{
    public ArkCaseCMISComponent()
    {
        setSessionFacadeFactory(new ArkCaseSessionFacadeFactory());
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception
    {
        CMISEndpoint endpoint = new CMISEndpoint(uri, this, remaining);

        // create a copy of parameters which we need to store on the endpoint which are in use from the session factory
        Map<String, Object> copy = new HashMap<>(parameters);
        endpoint.setProperties(copy);
        if (getSessionFacadeFactory() != null)
        {
            endpoint.setSessionFacadeFactory(getSessionFacadeFactory());
        }

        // create a dummy CMISSessionFacade which we set the properties on
        // so we can validate if they are all known options and fail fast if there are unknown options
        CMISSessionFacade dummy = new CMISSessionFacade(remaining);
        setProperties(dummy, parameters);

        // and the remainder options are for the endpoint
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
