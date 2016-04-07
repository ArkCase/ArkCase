package com.armedia.acm.webdav;

import com.armedia.acm.plugins.ecm.model.AcmFolder;
import io.milton.http.LockInfo;
import io.milton.http.LockTimeout;
import io.milton.http.LockToken;
import io.milton.http.Range;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.resource.GetableResource;
import io.milton.resource.LockingCollectionResource;
import io.milton.resource.PropFindableResource;
import io.milton.resource.Resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity
 */
public class AcmFolderResource extends AcmFileSystemResource implements PropFindableResource, LockingCollectionResource, GetableResource
{

    private AcmFolder acmFolder;

    public AcmFolderResource(String host, AcmFolder acmFolder, AcmFileSystemResourceFactory resourceFactory)
    {
        super(host, resourceFactory);
        this.acmFolder = acmFolder;
    }

    public Long getId()
    {
        return acmFolder.getId();
    }

    // Resource interface methods implementation
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

    @Override
    public Date getCreateDate()
    {
        return acmFolder.getCreated();
    }

    // LockingCollectionResource interface methods implementation
    @Override
    public Resource child(String childName) throws NotAuthorizedException, BadRequestException
    {
        // implementation not needed
        return null;
    }

    @Override
    public List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException
    {

        throw new UnsupportedOperationException("Folder browsing is not supported.");

    }

    @Override
    public LockToken createAndLock(String name, LockTimeout timeout, LockInfo lockInfo) throws NotAuthorizedException
    {
        // implementation not needed
        return null;
    }

    // GetableResource interface methods implementation
    @Override
    public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException
    {
        throw new UnsupportedOperationException("Folder browsing is not supported!");
    }

    @Override
    public String getContentType(String accepts)
    {
        return "text/html";
    }

}