package com.armedia.acm.plugins.ecm.model;

/*-
* #%L
* ACM Service: Enterprise Content Management
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

import com.armedia.acm.services.search.model.solr.SolrContentDocument;

import org.springframework.context.ApplicationEvent;

/**
 * Notify interested parties after a content file is indexed in Solr.
 */
public class EcmFileContentIndexedEvent extends ApplicationEvent
{
    private static final long serialVersionUID = 1L;
    private SolrContentDocument solrContentDocument;

    public EcmFileContentIndexedEvent(SolrContentDocument indexed)
    {
        super(indexed);

        setSolrContentDocument(indexed);
    }

    /**
     * @return the solrContentDocument
     */
    public SolrContentDocument getSolrContentDocument()
    {
        return solrContentDocument;
    }

    /**
     * @param solrContentDocument the solrContentDocument to set
     */
    public void setSolrContentDocument(SolrContentDocument solrContentDocument)
    {
        this.solrContentDocument = solrContentDocument;
    }
}
