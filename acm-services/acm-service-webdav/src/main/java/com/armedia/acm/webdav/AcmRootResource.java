package com.armedia.acm.webdav;

/*-
 * #%L
 * ACM Service: WebDAV Integration Library
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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.CollectionResource;
import io.milton.resource.MakeCollectionableResource;
import io.milton.resource.Resource;

/**
 * Created by nebojsha on 13.08.2016.
 */
public class AcmRootResource extends AcmAbstractResource implements MakeCollectionableResource, CollectionResource
{
    private Logger log = LogManager.getLogger(getClass());
    private ArrayList<Resource> children;

    public AcmRootResource(AcmFileSystemResourceFactory resourceFactory)
    {
        super(resourceFactory);
        this.children = new ArrayList<>();
    }

    @Override
    public CollectionResource createCollection(String newName) throws NotAuthorizedException, ConflictException, BadRequestException
    {
        return this;
    }

    public void updateChildren(Resource child)
    {
        this.children.add(child);
    }

    public void setChildren(ArrayList<Resource> children)
    {
        this.children = children;
    }

    @Override
    public List<? extends Resource> getChildren()
    {
        return children;
    }

    @Override
    public Resource child(String childName)
    {
        for (Resource r : children)
            if (r.getName().equals(childName))
            {
                return r;
            }
        return null;
    }

    @Override
    public String getName()
    {
        return "ROOT";
    }
}
