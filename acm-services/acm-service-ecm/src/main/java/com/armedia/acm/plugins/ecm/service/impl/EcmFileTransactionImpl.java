package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by armdev on 4/22/14.
 */
public class EcmFileTransactionImpl implements EcmFileTransaction
{
    private MuleContextManager muleContextManager;
    private EcmFileDao ecmFileDao;
    private AcmFolderDao folderDao;
    private FolderAndFilesUtils folderAndFilesUtils;

    private Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public EcmFile addFileTransaction(
            String originalFileName,
            Authentication authentication,
            String fileType,
            InputStream fileInputStream,
            String mimeType,
            String fileName,
            String cmisFolderId,
            AcmContainer container)
            throws MuleException
    {
        // by default, files are documents
        String category = "Document";
        EcmFile retval = addFileTransaction(originalFileName,authentication, fileType, category, fileInputStream, mimeType, fileName,
                cmisFolderId, container);

        return retval;
    }

    @Override
    public EcmFile addFileTransaction(
            String originalFileName,
            Authentication authentication,
            String fileType,
            String fileCategory,
            InputStream fileInputStream,
            String mimeType,
            String fileName,
            String cmisFolderId,
            AcmContainer container)
            throws MuleException
    {
        EcmFile toAdd = new EcmFile();
        toAdd.setFileMimeType(mimeType);
        toAdd.setFileName(fileName);
        toAdd.setFileType(fileType);
        toAdd.setCategory(fileCategory);

        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("cmisFolderId", cmisFolderId);
        messageProps.put("inputStream", fileInputStream);

        MuleMessage request = new DefaultMuleMessage(toAdd, messageProps, getMuleContextManager().getMuleContext());
        MuleMessage received = getMuleContextManager().getMuleClient().send("vm://addFile.in", request);

        MuleException e = received.getInboundProperty("saveException");
        if ( e != null )
        {
            throw e;
        }

        Document cmisDocument = received.getPayload(Document.class);
        toAdd.setVersionSeriesId(cmisDocument.getVersionSeriesId());
        toAdd.setActiveVersionTag(cmisDocument.getVersionLabel());
        toAdd.setFileName(originalFileName);

        EcmFileVersion version = new EcmFileVersion();
        version.setCmisObjectId(cmisDocument.getId());
        version.setVersionTag(cmisDocument.getVersionLabel());
        toAdd.getVersions().add(version);

        AcmFolder folder = getFolderDao().findByCmisFolderId(cmisFolderId);
        toAdd.setFolder(folder);

        toAdd.setContainer(container);

        EcmFile saved = getEcmFileDao().save(toAdd);
        return saved;
    }
    
    @Override
    public EcmFile updateFileTransaction(
            Authentication authentication,
            EcmFile ecmFile,
            InputStream fileInputStream)
            throws MuleException
    {

        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("ecmFileId", ecmFile.getVersionSeriesId());
        messageProps.put("fileName", ecmFile.getFileName());
        messageProps.put("mimeType", ecmFile.getFileMimeType());
        messageProps.put("inputStream", fileInputStream);

        MuleMessage request = new DefaultMuleMessage(ecmFile, messageProps, getMuleContextManager().getMuleContext());
        MuleMessage received = getMuleContextManager().getMuleClient().send("vm://updateFile.in", request);

        MuleException e = received.getInboundProperty("updateException");
        if ( e != null )
        {
            throw e;
        }

        Document cmisDocument = received.getPayload(Document.class);
        ecmFile.setActiveVersionTag(cmisDocument.getVersionLabel());

        EcmFileVersion version = new EcmFileVersion();
        version.setCmisObjectId(cmisDocument.getId());
        version.setVersionTag(cmisDocument.getVersionLabel());
        ecmFile.getVersions().add(version);

        ecmFile = getEcmFileDao().save(ecmFile);

        return ecmFile;
    }
    
    @Override
    public String downloadFileTransaction(EcmFile ecmFile) throws MuleException {
    	try 
		{
            MuleMessage request = new DefaultMuleMessage(ecmFile.getVersionSeriesId(), getMuleContextManager().getMuleContext());
			MuleMessage message = getMuleContextManager().getMuleClient().send("vm://downloadFileFlow.in", request);
			
			String result = getContent((ContentStream) message.getPayload());
			
			return result;
		} 
		catch (MuleException e) 
		{
			log.error("Cannot download file: " + e.getMessage(), e);
			throw e;
		}
    }
    
    private String getContent(ContentStream contentStream)
	{
		String content = "";
		InputStream inputStream = null;
		
		try
        {
			inputStream = contentStream.getStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer);
			content = writer.toString();
        } 
		catch (IOException e) 
		{
        	log.error("Could not copy input stream to the writer: " + e.getMessage(), e);
		}
		finally
        {
            if ( inputStream != null )
            {
                try
                {
                	inputStream.close();
                }
                catch (IOException e)
                {
                    log.error("Could not close CMIS content stream: " + e.getMessage(), e);
                }
            }
        }
		
		return content;
	}

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils() {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils) {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }
}
