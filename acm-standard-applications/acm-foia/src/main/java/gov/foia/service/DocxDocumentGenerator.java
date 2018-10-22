package gov.foia.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.correspondence.utils.PoiWordGenerator;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;

import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIADocumentDescriptor;
import gov.foia.model.FOIAObject;

/**
 * Document generator implementation for Microsoft Word documents.
 * 
 * @author bojan.milenkoski
 */
public class DocxDocumentGenerator implements DocumentGenerator
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private Random random = new Random();

    private PoiWordGenerator wordGenerator;
    private EcmFileDao ecmFileDao;
    private EcmFileService ecmFileService;

    @Override
    public EcmFile generateAndUpload(FOIADocumentDescriptor documentDescriptor, FOIAObject acmObject, String targetCmisFolderId,
            String targetFilename, Map<String, String> substitutions) throws DocumentGeneratorException
    {
        try
        {
            String filename = String.format("%s/acm-%020d.pdf", System.getProperty("java.io.tmpdir"), Math.abs(random.nextLong()));
            log.debug("PDF creation: using [{}] as temporary file name", filename);

            getWordGenerator().generate(new FileSystemResource(documentDescriptor.getTemplate()), new FileOutputStream(filename),
                    substitutions);

            EcmFile existing = ecmFileDao.findForContainerAttachmentFolderAndFileType(acmObject.getContainer().getId(),
                    acmObject.getContainer().getAttachmentFolder().getId(), documentDescriptor.getDoctype());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            try (InputStream fis = new FileInputStream(filename))
            {
                if (existing == null)
                {
                    return ecmFileService.upload(targetFilename, documentDescriptor.getDoctype(), "Document", fis,
                            FOIAConstants.MIME_TYPE_DOCX, targetFilename, authentication, targetCmisFolderId, acmObject.getObjectType(),
                            acmObject.getId());
                }
                else
                {
                    return ecmFileService.update(existing, fis, authentication);
                }

            }
        }
        catch (IOException | AcmCreateObjectFailedException | AcmUserActionFailedException e)
        {
            throw new DocumentGeneratorException("Failed to generate Word document for objectId: [" + acmObject.getId() + "], objectType: ["
                    + acmObject.getObjectType() + "] and template:[" + documentDescriptor.getTemplate() + "]", e);
        }
    }

    public PoiWordGenerator getWordGenerator()
    {
        return wordGenerator;
    }

    public void setWordGenerator(PoiWordGenerator wordGenerator)
    {
        this.wordGenerator = wordGenerator;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

}
