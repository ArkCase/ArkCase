package gov.foia.service;

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

import gov.foia.model.FOIARequest;
import gov.foia.web.api.DocumentPrintingException;

/**
 * <code>PrintDocument</code> implementation that holds binary data of merged PDF document extracted by processing
 * instances of <code>FOIARequest</code>s
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 16, 2016
 */
public class FoiaPrintDocument implements BinaryDataProvider<FOIARequest>
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
