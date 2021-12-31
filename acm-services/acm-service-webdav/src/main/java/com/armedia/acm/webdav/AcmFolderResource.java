package com.armedia.acm.webdav;

/*-
 * #%L
 * ACM Service: WebDAV Integration Library
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.armedia.acm.plugins.ecm.model.AcmFolder;

import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.CollectionResource;
import io.milton.resource.MakeCollectionableResource;
import io.milton.resource.Resource;

public class AcmFolderResource extends AcmFileSystemResource implements MakeCollectionableResource, CollectionResource
{

    private AcmFolder acmFolder;
    private ArrayList<Resource> children;

    public AcmFolderResource(String host, AcmFileSystemResourceFactory resourceFactory, String userId, String containerObjectType,
            String containerObjectId, AcmFolder acmFolder)
    {
        super(host, resourceFactory, userId, containerObjectType, containerObjectId);
        this.acmFolder = acmFolder;
        this.children = new ArrayList<>();
    }

    @Override
    public CollectionResource createCollection(String s) throws NotAuthorizedException, ConflictException, BadRequestException
    {
        return this;
    }

    public void updateChildren(Resource child)
    {
        this.children.add(child);
    }

    @Override
    public Resource child(String childName) throws NotAuthorizedException, BadRequestException
    {
        for (Resource r : children)
            if (r.getName().equals(childName))
            {
                return r;
            }
        return null;
    }

    @Override
    public List<? extends Resource> getChildren()
    {
        return children;
    }

    @Override
    public String getUniqueId()
    {
        return (acmFolder.getId() + "_" + acmFolder.getName() + "_" + acmFolder.getCreator()).hashCode() + "";
    }

    @Override
    public String getName()
    {
        return acmFolder.getName();
    }

    @Override
    public Date getModifiedDate()
    {
        return acmFolder.getModified();
    }

    public AcmFolder getAcmFolder()
    {
        return acmFolder;
    }
}
