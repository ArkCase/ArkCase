package gov.foia.pipeline.presave;

import com.armedia.acm.objectonverter.ArkCaseBeanUtils;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.pipeline.presave.EcmFileNewMetadataHandler;
import com.armedia.acm.plugins.ecm.service.impl.EcmTikaFile;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.chemistry.opencmis.client.api.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import gov.foia.model.FOIAFile;

public class FOIAFileNewMetadataHandler extends EcmFileNewMetadataHandler
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ArkCaseBeanUtils arkCaseBeanUtils;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {

        log.debug("metadata pre save handler called");

        // Writes metadata for new document uploads into the database
        if (!pipelineContext.getIsAppend())
        {
            if (entity == null)
            {
                throw new PipelineProcessException("ecmFile is null");
            }

            Document cmisDocument = pipelineContext.getCmisDocument();
            if (cmisDocument == null)
            {
                throw new PipelineProcessException("cmisDocument is null");
            }

            entity.setVersionSeriesId(cmisDocument.getVersionSeriesId());
            entity.setActiveVersionTag(cmisDocument.getVersionLabel());

            // Sets the versioning of the file
            EcmFileVersion version = new EcmFileVersion();
            version.setCmisObjectId(cmisDocument.getId());
            version.setVersionTag(cmisDocument.getVersionLabel());
            version.setVersionMimeType(entity.getFileActiveVersionMimeType());
            version.setVersionFileNameExtension(entity.getFileActiveVersionNameExtension());
            long fileSizeBytes = pipelineContext.getMergedFileByteArray() != null &&
                    pipelineContext.getMergedFileByteArray().length > 0 ? pipelineContext.getMergedFileByteArray().length
                            : pipelineContext.getFileContents() != null ? pipelineContext.getFileContents().length() : 0;
            version.setFileSizeBytes(fileSizeBytes);

            // file metadata
            if (pipelineContext.getDetectedFileMetadata() != null)
            {
                EcmTikaFile etf = pipelineContext.getDetectedFileMetadata();
                etf.stampVersionInfo(version);
            }

            entity.getVersions().add(version);

            // Determines the folder and container in which the file should be saved
            AcmFolder folder = getFolderDao().findByCmisFolderId(pipelineContext.getCmisFolderId());
            entity.setFolder(folder);
            entity.setContainer(pipelineContext.getContainer());

            try
            {
                int pageCount = getPageCountService().getNumberOfPages(entity.getFileActiveVersionMimeType(),
                        pipelineContext.getFileContents());
                if (pageCount > -1)
                {
                    entity.setPageCount(pageCount);
                }
            }
            catch (IOException e)
            {
                throw new PipelineProcessException(e);
            }

            // Saves new file metadata into ArkCase database
            FOIAFile fileMetadata = new FOIAFile();
            try
            {
                getArkCaseBeanUtils().copyProperties(fileMetadata, entity);
                fileMetadata.setPublicFlag(false);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                log.error("Could not copy properties from EcmFile to FoiaFile");
            }
            FOIAFile saved = (FOIAFile) getEcmFileDao().save(fileMetadata);
            pipelineContext.setEcmFile(saved);
        }
        log.debug("metadata pre save handler ended");
    }

    public ArkCaseBeanUtils getArkCaseBeanUtils()
    {
        return arkCaseBeanUtils;
    }

    public void setArkCaseBeanUtils(ArkCaseBeanUtils arkCaseBeanUtils)
    {
        this.arkCaseBeanUtils = arkCaseBeanUtils;
    }
}
