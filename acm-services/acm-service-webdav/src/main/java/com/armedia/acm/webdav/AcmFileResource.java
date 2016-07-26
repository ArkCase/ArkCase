package com.armedia.acm.webdav;

import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

import io.milton.common.ContentTypeUtils;
import io.milton.common.RangeUtils;
import io.milton.http.Range;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.DigestResource;
import io.milton.resource.PropFindableResource;
import io.milton.resource.ReplaceableResource;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity
 */
public class AcmFileResource extends AcmFileSystemResource implements PropFindableResource, ReplaceableResource, DigestResource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AcmFileResource.class);

    private EcmFile acmFile;
    private String fileType;
    private String lockType;
    private String acmTicket;

    public AcmFileResource(String host, EcmFile acmFile, String fileType, String lockType, String acmTicket,
            AcmFileSystemResourceFactory resourceFactory)
    {
        super(host, resourceFactory);
        this.acmFile = acmFile;
        this.fileType = fileType;
        this.lockType = lockType;
        this.acmTicket = acmTicket;
    }

    public Long getId()
    {
        return acmFile.getId();
    }

    public Long getParentId()
    {
        return acmFile.getFolder().getId();
    }

    public String getFileType()
    {
        return fileType;
    }

    public String getLockType()
    {
        return lockType;
    }

    public String getAcmTicket()
    {
        return acmTicket;
    }

    // Resource interface methods implementation
    @Override
    public String getUniqueId()
    {
        return (acmFile.getId() + "_" + acmFile.getFileName() + "_" + acmFile.getCreator()).hashCode() + "";
    }

    @Override
    public String getName()
    {
        return acmFile.getFileName();
    }

    @Override
    public Date getModifiedDate()
    {
        return acmFile.getModified();
    }

    // PropFindableResource interface methods implementation

    @Override
    public Date getCreateDate()
    {
        return acmFile.getCreated();
    }

    // GetableResource interface methods implementation

    @Override
    public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType)
            throws IOException, NotAuthorizedException, BadRequestException, NotFoundException
    {

        try
        {
            MuleMessage downloadedFile = getResourceFactory().getMuleContextManager().send("vm://downloadFileFlow.in",
                    getResourceFactory().getCmisFileId(acmFile));
            if (downloadedFile.getPayload() instanceof ContentStream)
            {
                ContentStream filePayload = (ContentStream) downloadedFile.getPayload();
                try (InputStream fileIs = filePayload.getStream())
                {
                    if (range != null)
                    {
                        RangeUtils.writeRange(fileIs, range, out);
                    } else
                    {
                        IOUtils.copy(fileIs, out);
                    }
                    out.flush();
                }
            } else
            {
                throw new NotFoundException("Could not locate content");
            }

        } catch (MuleException e)
        {
            LOGGER.error("Error while downloading file via Mule.", e);
        }
    }

    @Override
    public String getContentType(String accepts)
    {
        String mime = acmFile.getFileMimeType();
        return ContentTypeUtils.findAcceptableContentType(mime, accepts);
    }

    // ReplaceableResource interface methods implementation

    @Override
    public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException
    {
        try
        {
            getResourceFactory().getEcmFileTransaction().updateFileTransactionEventAware(
                    getResourceFactory().getSecurityManager().getAuthenticationForTicket(acmTicket), acmFile, in);
        } catch (MuleException e)
        {
            LOGGER.error("Error while uploading file via Mule.", e);
        }

    }

    // DigestResource interface methods implementation

    @Override
    public Object authenticate(DigestResponse digestRequest)
    {
        return getResourceFactory().getSecurityManager().authenticate(digestRequest);
    }

    @Override
    public boolean isDigestAllowed()
    {
        return getResourceFactory().getSecurityManager().isDigestAllowed();
    }

}