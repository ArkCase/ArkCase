package com.armedia.acm.compressfolder;

import static com.armedia.acm.plugins.ecm.model.EcmFileConstants.OBJECT_FILE_TYPE;
import static com.armedia.acm.plugins.ecm.model.EcmFileConstants.OBJECT_FOLDER_TYPE;
import static org.apache.commons.io.IOUtils.copy;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.commons.io.FileUtils;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 13, 2016
 *
 */
public class FolderCompressor
{

    public static final long DEFAULT_MAX_SIZE = 2;

    public static final SizeUnit DEFAULT_SIZE_UNIT = SizeUnit.GIGA;

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmFolderService folderService;

    private EcmFileService fileService;

    private String compressedFileNameFormat;

    private long maxSize = DEFAULT_MAX_SIZE;

    private SizeUnit sizeUnit = DEFAULT_SIZE_UNIT;

    public String compressFolder(Long folderId) throws FolderCompressorException
    {
        return compressFolder(folderId, maxSize, sizeUnit);
    }

    public String compressFolder(Long folderId, long size, SizeUnit sizeUnit) throws FolderCompressorException
    {

        AcmFolder folder = Optional.ofNullable(folderService.findById(folderId)).orElseThrow(() -> new FolderCompressorException(folderId));

        String tmpDir = System.getProperty("java.io.tmpdir");
        String pathSeparator = System.getProperty("file.separator");

        String filename = String.format(compressedFileNameFormat,
                tmpDir.endsWith(pathSeparator) ? tmpDir : entryName(tmpDir, pathSeparator), folderId, folder.getName());
        log.debug("ZIP creation: using [{}] as temporary file name", filename);
        File file = new File(filename);

        try (ZipOutputStream zos = new ZipOutputStream(new MaxTroughputAwareFileOutputStream(file, size, sizeUnit)))
        {
            zos.setLevel(Deflater.BEST_COMPRESSION);
            compressFolder(zos, maxSize, folder, "");
        } catch (AcmUserActionFailedException | AcmObjectNotFoundException | MuleException | IOException e)
        {
            FileUtils.deleteQuietly(file);
            throw new FolderCompressorException(e);
        }

        return filename;
    }

    private void compressFolder(ZipOutputStream zos, long maxSize, AcmFolder folder, String parentPath)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, MuleException, IOException, FolderCompressorException
    {

        List<AcmObject> folderChildren = folderService.getFolderChildren(folder.getId()).stream().filter(obj -> obj.getObjectType() != null)
                .collect(Collectors.toList());

        for (AcmObject obj : folderChildren)
        {
            String objectType = obj.getObjectType().toUpperCase();
            if (OBJECT_FILE_TYPE.equals(objectType))
            {
                zos.putNextEntry(new ZipEntry(entryName(parentPath, EcmFile.class.cast(obj).getFileName())));
                InputStream fileByteStream = fileService.downloadAsInputStream(obj.getId());
                copy(fileByteStream, zos);
            } else if (OBJECT_FOLDER_TYPE.equals(objectType))
            {
                AcmFolder childFolder = AcmFolder.class.cast(obj);
                String entryName = entryName(parentPath, childFolder.getName(), "/");
                zos.putNextEntry(new ZipEntry(entryName));
                compressFolder(zos, maxSize, childFolder, entryName);
            }
            zos.closeEntry();
        }

    }

    private String entryName(String... pathParts)
    {
        return Stream.of(pathParts).collect(Collectors.joining());
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

    public void setCompressedFileNameFormat(String compressedFileNameFormat)
    {
        this.compressedFileNameFormat = compressedFileNameFormat;
    }

    public void setMaxSize(long maxSize)
    {
        this.maxSize = maxSize;
    }

    public void setSizeUnit(String sizeUnit)
    {
        this.sizeUnit = SizeUnit.valueOf(sizeUnit);
    }

}
