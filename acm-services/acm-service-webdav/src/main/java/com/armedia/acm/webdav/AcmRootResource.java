package com.armedia.acm.webdav;

import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.CollectionResource;
import io.milton.resource.MakeCollectionableResource;
import io.milton.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nebojsha on 13.08.2016.
 */
public class AcmRootResource extends AcmAbstractResource implements MakeCollectionableResource, CollectionResource
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private ArrayList<Resource> children;

    public AcmRootResource(AcmFileSystemResourceFactory resourceFactory)
    {
        super(resourceFactory);
    }

    @Override
    public CollectionResource createCollection(String newName) throws NotAuthorizedException, ConflictException, BadRequestException
    {
        return this;
    }

    @Override
    public List<? extends Resource> getChildren()
    {
        if (children == null)
            children = new ArrayList<Resource>();

        return children;
    }


    @Override
    public Resource child(String childName)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return null;
    }
}