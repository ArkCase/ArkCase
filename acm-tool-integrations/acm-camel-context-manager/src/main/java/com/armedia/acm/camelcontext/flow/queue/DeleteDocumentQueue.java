package com.armedia.acm.camelcontext.flow.queue;

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
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;

import org.apache.camel.ProducerTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Aug, 2019
 */
public class DeleteDocumentQueue implements ArkCaseCMISQueue
{
    private ProducerTemplate producerTemplate;
    private String repositoryID;
    private Long timeout;
    private Logger log = LogManager.getLogger(getClass());

    public DeleteDocumentQueue(ProducerTemplate producerTemplate, String repositoryID, String timeout)
    {
        this.producerTemplate = producerTemplate;
        this.repositoryID = repositoryID;
        this.timeout = Long.valueOf(timeout);
    }

    @Override
    public Object send(Map<String, Object> props) throws ArkCaseFileRepositoryException
    {
        String queueName = "seda:" + repositoryID + "-deleteDocumentQueue?timeout=" + timeout;
        producerTemplate.setDefaultEndpointUri(queueName);
        try
        {
            log.debug("Trying to delete document with ID={}", props.get(ArkCaseCMISConstants.CMIS_DOC_ID));
            return producerTemplate.requestBody(props);
        }
        catch (Exception e)
        {
            throw new ArkCaseFileRepositoryException(e);
        }
    }
}
