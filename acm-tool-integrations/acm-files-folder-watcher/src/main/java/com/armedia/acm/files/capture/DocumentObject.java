package com.armedia.acm.files.capture;

/*-
 * #%L
 * Tool Integrations: Folder Watcher
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

import org.eclipse.persistence.dynamic.DynamicEntity;

import java.io.File;
import java.util.List;

/**
 * Created by riste.tutureski on 9/3/2015.
 */
public class DocumentObject
{
    private String id;
    private File document;
    private List<DocumentObject> attachments;
    private DynamicEntity entity;

    public DocumentObject()
    {
    }

    public DocumentObject(String id, File document, List<DocumentObject> attachments, DynamicEntity entity)
    {
        this.id = id;
        this.document = document;
        this.attachments = attachments;
        this.entity = entity;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public File getDocument()
    {
        return document;
    }

    public void setDocument(File document)
    {
        this.document = document;
    }

    public List<DocumentObject> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(List<DocumentObject> attachments)
    {
        this.attachments = attachments;
    }

    public DynamicEntity getEntity()
    {
        return entity;
    }

    public void setEntity(DynamicEntity entity)
    {
        this.entity = entity;
    }
}
