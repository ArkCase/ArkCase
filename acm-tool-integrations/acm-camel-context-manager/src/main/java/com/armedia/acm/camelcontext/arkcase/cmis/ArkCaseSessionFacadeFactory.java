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

import org.apache.camel.component.cmis.ArkCaseCMISSessionFacade;
import org.apache.camel.component.cmis.CMISEndpoint;
import org.apache.camel.component.cmis.CMISSessionFacade;
import org.apache.camel.component.cmis.DefaultCMISSessionFacadeFactory;
import org.apache.camel.support.PropertyBindingSupport;

import java.util.HashMap;
import java.util.Map;

public class ArkCaseSessionFacadeFactory extends DefaultCMISSessionFacadeFactory
{
    @Override
    public CMISSessionFacade create(CMISEndpoint endpoint) throws Exception
    {
        CMISSessionFacade facade = new ArkCaseCMISSessionFacade(endpoint.getCmsUrl(), endpoint.getProperties());

        // must use a copy of the properties
        Map<String, Object> copy = new HashMap<>(endpoint.getProperties());
        // which we then set on the newly created facade
        PropertyBindingSupport.bindProperties(endpoint.getCamelContext(), facade, copy);

        return facade;
    }
}
