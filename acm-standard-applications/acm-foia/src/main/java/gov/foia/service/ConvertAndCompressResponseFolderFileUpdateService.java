package gov.foia.service;

import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.convertfolder.ConversionException;
import com.armedia.acm.convertfolder.FileConverter;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 23, 2018
 *
 */
public class ConvertAndCompressResponseFolderFileUpdateService implements ResponseFolderFileUpdateService
{
    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    private FileConverter converter;

    private FolderCompressor compressor;

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    /*
     * (non-Javadoc)
     * @see gov.foia.service.ResponseFolderFileUpdateService#updateFile(com.armedia.acm.plugins.ecm.model.EcmFile,
     * java.lang.Long, org.springframework.security.core.Authentication)
     */
    @Override
    @Async
    @Transactional
    public void updateFile(EcmFile file, Long folderId, String username)
    {
        try
        {
            log.debug("Converting and compresssing [{}] file in folder with [{}] ID for user [{}].", file.getFileName(), folderId,
                    username);

            auditPropertyEntityAdapter.setUserId(username);
            converter.convert(file, username);
            compressor.compressFolder(folderId);
        }
        catch (ConversionException | AcmFolderException e)
        {
            log.warn("Error while converting and compressing file [{}].", file.getFileName(), e);
        }
    }

    /**
     * @param converter
     *            the converter to set
     */
    public void setConverter(FileConverter converter)
    {
        this.converter = converter;
    }

    /**
     * @param compressor
     *            the compressor to set
     */
    public void setCompressor(FolderCompressor compressor)
    {
        this.compressor = compressor;
    }

    /**
     * @param auditPropertyEntityAdapter
     *            the auditPropertyEntityAdapter to set
     */
    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

}
