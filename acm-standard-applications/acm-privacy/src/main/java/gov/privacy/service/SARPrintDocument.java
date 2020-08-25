package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.objectdataprocessing.BinaryDataProvider;
import com.armedia.acm.plugins.ecm.model.AcmContainer;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import gov.privacy.model.SubjectAccessRequest;
import gov.privacy.web.api.DocumentPrintingException;

/**
 * <code>PrintDocument</code> implementation that holds binary data of merged PDF document extracted by processing
 * instances of <code>SubjectAccessRequest</code>s
 *
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SARPrintDocument implements BinaryDataProvider<SubjectAccessRequest>
{

    private List<AcmContainer> requestContainers;
    private String filename;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.printdocuments.PrintDocument#getFileName()
     */
    @Override
    public String getFileName()
    {
        String fileName;
        if (requestContainers != null && !requestContainers.isEmpty())
        {
            if (requestContainers.size() > 1)
            {
                fileName = requestContainers.stream().map(r -> r.getId().toString()).collect(Collectors.joining("_"));
            }
            else
            {
                fileName = requestContainers.get(0).getContainerObjectTitle();
            }
        }
        else
        {
            fileName = "";
        }
        return String.format("%s.pdf", fileName);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.printdocumentsi.PrintDocument#getContent()
     */
    @Override
    public InputStream getContent()
    {
        try
        {
            return new DeleteFileOnCloseInputStream(filename);
        }
        catch (IOException e)
        {
            if (filename != null)
            {
                File f = new File(filename);
                if (f.exists())
                {
                    f.delete();
                }
            }
            throw new DocumentPrintingException(e);
        }
    }

    /**
     * @param pdfMergerUtility
     * @throws IOException
     */
    public void setContent(PDFMergerUtility pdfMergerUtility) throws IOException
    {
        filename = String.format("%s/%s.pdf", System.getProperty("java.io.tmpdir"), UUID.randomUUID());
        pdfMergerUtility.setDestinationFileName(filename);
        pdfMergerUtility.mergeDocuments(MemoryUsageSetting.setupMixed(32 * 1024 * 1024));
    }

    @Override
    public long getContentLength()
    {
        File file = null;
        if (filename != null)
        {
            file = new File(filename);
        }
        if (file != null)
        {
            return file.length();
        }
        else
        {
            throw new DocumentPrintingException(
                    "getContentLength method was invoked before content was available, or after the content was released.");
        }
    }

    @Override
    public void releaseContent()
    {
        if (filename != null)
        {
            File f = new File(filename);
            if (f.exists())
            {
                f.delete();
            }
            filename = null;
        }
    }

    /**
     * @return the requestContainers
     */
    public List<AcmContainer> getRequestContainers()
    {
        return requestContainers;
    }

    /**
     * @param requestContainers
     *            the requestContainers to set
     */
    public void setRequestContainers(List<AcmContainer> requestContainers)
    {
        this.requestContainers = requestContainers;
    }

    /**
     * Attempts to delete the temporary file that this stream was created from when the stream is closed.
     *
     * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 30, 2016
     */
    private class DeleteFileOnCloseInputStream extends FileInputStream
    {

        DeleteFileOnCloseInputStream(String filename) throws FileNotFoundException
        {
            super(filename);
        }

        @Override
        public void close() throws IOException
        {
            super.close();
            releaseContent();
        }

    }

}
