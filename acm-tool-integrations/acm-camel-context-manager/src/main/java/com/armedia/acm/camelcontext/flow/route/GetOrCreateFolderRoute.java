package com.armedia.acm.camelcontext.flow.route;

/*-
 * #%L
 * acm-camel-context-manager
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

import com.armedia.acm.camelcontext.basic.auth.HttpInvokerUtil;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.component.cmis.CamelCMISActions;
import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;

import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Oct, 2019
 */
public class GetOrCreateFolderRoute extends ArkCaseAbstractRoute
{
    private Logger log = LogManager.getLogger(getClass());

    @Override
    public void configure()
    {
        onException(Exception.class).handled(false)
                .process(x -> {
                    Exception exception = (Exception) x.getProperty(Exchange.EXCEPTION_CAUGHT);
                    String causeMessage = String.valueOf(exception.getCause());
                    log.error("Exception while creating object by path: {}", causeMessage, exception);
                    throw new ArkCaseFileRepositoryException(exception);
                });

        from("seda:" + getRepositoryId() + "-getOrCreateFolderQueue?timeout=" + getTimeout()).setExchangePattern(ExchangePattern.InOut)
                .process(exchange -> {
                    routeProperties = (Map<String, Object>) exchange.getIn().getBody();
                    exchange.getIn().getHeaders().put(PropertyIds.PATH, routeProperties.get(PropertyIds.PATH));
                    exchange.getIn().getHeaders().put(PropertyIds.NAME, routeProperties.get(PropertyIds.NAME));
                    exchange.getIn().getHeaders().put(CamelCMISConstants.CMIS_ACTION, CamelCMISActions.FIND_OBJECT_BY_PATH);
                    MDC.put(HttpInvokerUtil.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY,
                            String.valueOf(routeProperties.get(HttpInvokerUtil.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY)));
                })
                .recipientList().method(this, "createUrl")
                .choice()
                .when();
    }
}
