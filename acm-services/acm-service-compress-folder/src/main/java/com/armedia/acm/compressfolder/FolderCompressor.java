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
 * Service for compressing folder to a zip file. The folder is recursively traversed and all its' contents is added to
 * the zip file. The contents is added to the current tmp directory as defined by <code>java.io.tmpdir</code> system
 * property. In case a size limit is set, and the output to the compressed file surpasses the limit, the compressing
 * operation stops and the resulting file is removed from the file system.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 13, 2016
 *
 */
public class FolderCompressor
{

    /**
     * The default max size of the compressed file expressed in default size unit.
     *
     * @see #DEFAULT_SIZE_UNIT
     */
    public static final long DEFAULT_MAX_SIZE = 2;

    /**
     * The default size unit used to calculate the max compressed size in bytes.
     */
    public static final SizeUnit DEFAULT_SIZE_UNIT = SizeUnit.GIGA;

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Used to retrieve folder information from the system.
     */
    private AcmFolderService folderService;

    /**
     * Used to retrieve file information from the system.
     */
    private EcmFileService fileService;

    /**
     * A formatting string that is used to generate the output file name. It takes 3 parameters, <code>tmpDir</code>,
     * <code>folderId</code> and <code>folderName</code>, for example <code>
     *      %1$sacm-%2$d-%3$s.zip
     * </code>
     */
    private String compressedFileNameFormat;

    /**
     * Maximum size of the output file expressed in <code>sizeUnit</code>s.
     *
     * @see #sizeUnit
     */
    private long maxSize = DEFAULT_MAX_SIZE;

    /**
     * Size unit used to calculate the max compressed size in bytes.
     */
    private SizeUnit sizeUnit = DEFAULT_SIZE_UNIT;

    /**
     * Compresses the folder contents by using the <code>maxSize</code> and <code>sizeUnit</code> for setting the output
     * file size limit.
     *
     * @param folderId the ID of the folder to be compressed.
     * @return the path to the file on the file system where the output file was stored on the machine where the
     *         application is running.
     * @throws FolderCompressorException if a folder for the given <code>folderId</code> does not exist, or there was an
     *             IO exception during retrieving, writing or compressing the folder.
     *
     * @see #maxSize
     * @see #sizeUnit
     */
    public String compressFolder(Long folderId) throws FolderCompressorException
    {
        return compressFolder(folderId, maxSize, sizeUnit);
    }

    /**
     * Compresses the folder contents by using the <code>size</code> and <code>sizeUnit</code> arguments for setting the
     * output file size limit.
     *
     * @param folderId the ID of the folder to be compressed.
     * @param size size of the output file expressed in <code>sizeUnit</code>s.
     * @param sizeUnit size unit used to calculate the max compressed size in bytes.
     * @return the path to the file on the file system where the output file was stored on the machine where the
     *         application is running.
     * @throws FolderCompressorException if a folder for the given <code>folderId</code> does not exist, or there was an
     *             IO exception during retrieving, writing or compressing the folder.
     */
    public String compressFolder(Long folderId, long size, SizeUnit sizeUnit) throws FolderCompressorException
    {

        AcmFolder folder = Optional.ofNullable(folderService.findById(folderId)).orElseThrow(() -> new FolderCompressorException(folderId));

        String tmpDir = System.getProperty("java.io.tmpdir");
        String pathSeparator = System.getProperty("file.separator");

        String filename = String.format(compressedFileNameFormat,
                tmpDir.endsWith(pathSeparator) ? tmpDir : concatStrings(tmpDir, pathSeparator), folderId, folder.getName());
        log.debug("ZIP creation: using [{}] as temporary file name", filename);
        File file = new File(filename);

        try (ZipOutputStream zos = new ZipOutputStream(new MaxTroughputAwareFileOutputStream(file, size, sizeUnit)))
        {
            zos.setLevel(Deflater.BEST_COMPRESSION);
            compressFolder(zos, folder, "");
        } catch (AcmUserActionFailedException | AcmObjectNotFoundException | MuleException | IOException e)
        {
            FileUtils.deleteQuietly(file);
            throw new FolderCompressorException(e);
        }

        return filename;
    }

    /**
     * Recursively traverses the folder and adds in contents to the instance of the <code>ZipOutputStream</code>
     * preserving the folder structure.
     *
     * @param zos the instance of <code>ZipOutputStream</code> that is used to compress the folder.
     * @param folder current folder being traversed.
     * @param parentPath path to the parent folder used to construct the zip folder structure in order for it to be
     *            identical as the structure of the folder that is being compressed.
     * @throws AcmUserActionFailedException can be thrown while querying for folder children.
     * @throws AcmObjectNotFoundException can be thrown while querying for folder children.
     * @throws MuleException can be thrown while retrieving the <code>InputStream</code> for a file.
     * @throws IOException can be thrown while writing to the output zip file.
     */
    private void compressFolder(ZipOutputStream zos, AcmFolder folder, String parentPath)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, MuleException, IOException
    {

        List<AcmObject> folderChildren = folderService.getFolderChildren(folder.getId()).stream().filter(obj -> obj.getObjectType() != null)
                .collect(Collectors.toList());

        for (AcmObject obj : folderChildren)
        {
            String objectType = obj.getObjectType().toUpperCase();
            if (OBJECT_FILE_TYPE.equals(objectType))
            {
                zos.putNextEntry(new ZipEntry(concatStrings(parentPath, EcmFile.class.cast(obj).getFileName())));
                InputStream fileByteStream = fileService.downloadAsInputStream(obj.getId());
                copy(fileByteStream, zos);
            } else if (OBJECT_FOLDER_TYPE.equals(objectType))
            {
                AcmFolder childFolder = AcmFolder.class.cast(obj);
                String entryName = concatStrings(parentPath, childFolder.getName(), "/");
                zos.putNextEntry(new ZipEntry(entryName));
                compressFolder(zos, childFolder, entryName);
            }
            zos.closeEntry();
        }

    }

    /**
     * Utility method used for string concatenation.
     *
     * @param pathParts an array of strings to be concatenated.
     * @return the concatenated string.
     */
    private String concatStrings(String... pathParts)
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
