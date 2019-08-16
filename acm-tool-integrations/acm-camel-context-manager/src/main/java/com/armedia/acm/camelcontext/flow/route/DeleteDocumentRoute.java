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

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.camelcontext.basic.auth.HttpInvokerUtil;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cmis.CamelCMISActions;
import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Aug, 2019
 */
public class DeleteDocumentRoute extends RouteBuilder implements ArkCaseRoute
{
    private Logger log = LogManager.getLogger(getClass());
    private Map<String, Object> map = new HashMap<>();
    private String repositoryId;
    private Long timeout;

    @Override
    public void configure() throws Exception
    {
        onException(Exception.class).handled(true)
                .process(x -> {
                    Exception exception = (Exception) x.getProperty(Exchange.EXCEPTION_CAUGHT);
                    String causeMessage = String.valueOf(exception.getCause());
                    log.error("Exception deleting file: {}", causeMessage, exception);
                });

        from("seda:" + repositoryId + "-deleteDocumentQueue?timeout=" + timeout).setExchangePattern(ExchangePattern.InOut)
                .process(exchange -> {
                    map = (Map<String, Object>) exchange.getIn().getBody();
                    exchange.getIn().getHeaders().put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
                    exchange.getIn().getHeaders().put(CamelCMISConstants.CMIS_OBJECT_ID, map.get("cmisDocumentId"));
                    exchange.getIn().getHeaders().put(CamelCMISConstants.CMIS_ACTION, CamelCMISActions.DELETE_DOCUMENT);
                    MDC.put(HttpInvokerUtil.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY,
                            String.valueOf(map.get(HttpInvokerUtil.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY)));
                })
                .recipientList().method(this, "createUrl");
    }

    public String createUrl()
    {
        String api = ArkCaseCMISConstants.ARKCASE_CMIS_COMPONENT + map.get(ArkCaseCMISConstants.CMIS_API_URL).toString();
        UrlBuilder urlBuilder = new UrlBuilder(api);
        urlBuilder.addParameter("username", map.get(SessionParameter.USER).toString());
        urlBuilder.addParameter("password", map.get(SessionParameter.PASSWORD).toString());

        return urlBuilder.toString();
    }

    @Override
    public void setRepositoryId(String repositoryId)
    {
        this.repositoryId = repositoryId;
    }

    @Override
    public void setTimeout(String timeout)
    {
        this.timeout = Long.valueOf(timeout);
    }
}
