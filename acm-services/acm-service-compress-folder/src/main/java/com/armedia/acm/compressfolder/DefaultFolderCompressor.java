package com.armedia.acm.compressfolder;

import static com.armedia.acm.plugins.ecm.model.EcmFileConstants.OBJECT_FILE_TYPE;
import static com.armedia.acm.plugins.ecm.model.EcmFileConstants.OBJECT_FOLDER_TYPE;
import static org.apache.commons.io.IOUtils.copy;

import com.armedia.acm.compressfolder.model.CompressNode;
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AcmEntity;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
public class DefaultFolderCompressor implements FolderCompressor
{

    /**
     * The default max size of the compressed file expressed in default size unit.
     *
     * @see #DEFAULT_SIZE_UNIT
     */
    private static final long DEFAULT_MAX_SIZE = 2;

    /**
     * The default size unit used to calculate the max compressed size in bytes.
     */
    private static final SizeUnit DEFAULT_SIZE_UNIT = SizeUnit.GIGA;

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

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.compressfolder.FolderCompressor#compressFolder(java.lang.Long)
     */
    @Override
    public String compressFolder(Long folderId) throws FolderCompressorException
    {
        return compressFolder(folderId, maxSize, sizeUnit);
    }

    @Override
    public String compressFolder(CompressNode compressNode) throws FolderCompressorException
    {
        return compressFolder(compressNode.getRootFolderId(), compressNode, maxSize, sizeUnit);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.compressfolder.FolderCompressor#compressFolder(java.lang.Long, long,
     * com.armedia.acm.compressfolder.SizeUnit)
     */
    @Override
    public String compressFolder(Long folderId, long size, SizeUnit sizeUnit) throws FolderCompressorException
    {
        return compressFolder(folderId, null, size, sizeUnit);
    }

    @Override
    public String compressFolder(Long folderId, CompressNode compressNode, long size, SizeUnit sizeUnit)
            throws FolderCompressorException
    {
        AcmFolder folder = Optional.ofNullable(folderService.findById(folderId)).orElseThrow(() -> new FolderCompressorException(folderId));

        String filename = getCompressedFolderFilePath(folder);
        log.debug("ZIP creation: using [{}] as temporary file name", filename);
        File file = new File(filename);

        try (ZipOutputStream zos = new ZipOutputStream(new MaxThroughputAwareFileOutputStream(file, size, sizeUnit)))
        {
            zos.setLevel(Deflater.BEST_COMPRESSION);
            compressFolder(zos, folder, "", compressNode);
        }
        catch (AcmUserActionFailedException | AcmObjectNotFoundException | MuleException | IOException e)
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
     * @param zos
     *            the instance of <code>ZipOutputStream</code> that is used to compress the folder.
     * @param folder
     *            current folder being traversed.
     * @param parentPath
     *            path to the parent folder used to construct the zip folder structure in order for it to be
     *            identical as the structure of the folder that is being compressed.
     * @throws AcmUserActionFailedException
     *             can be thrown while querying for folder children.
     * @throws AcmObjectNotFoundException
     *             can be thrown while querying for folder children.
     * @throws MuleException
     *             can be thrown while retrieving the <code>InputStream</code> for a file.
     * @throws IOException
     *             can be thrown while writing to the output zip file.
     */
    private void compressFolder(ZipOutputStream zos, AcmFolder folder, String parentPath, CompressNode compressNode)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, MuleException, IOException
    {

        List<AcmObject> folderChildren = folderService.getFolderChildren(folder.getId()).stream().filter(obj -> obj.getObjectType() != null)
                .collect(Collectors.toList());
        List<String> fileFolderList = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("yyyy_M_d_k_m_s", Locale.ENGLISH);
        for (AcmObject obj : folderChildren)
        {
            String objectType = obj.getObjectType().toUpperCase();
            if (canBeCompressed(obj, folder, compressNode))
            {
                String fileName = EcmFile.class.cast(obj).getFileName();
                /*
                 * If filename is duplicate, we will have to rename it.
                 * Otherwise, the zip file errors out.
                 * Here we just append an underscore "_" and date the file was created
                 */
                if (fileFolderList.contains(fileName))
                {
                    Date forFilenameUniquenessDt = (obj instanceof AcmEntity) ? AcmEntity.class.cast(obj).getCreated() : new Date();
                    String forFilenameUniqueness = format.format(forFilenameUniquenessDt);
                    fileName = fileName + "_" + forFilenameUniqueness;
                }
                fileFolderList.add(fileName);

                zos.putNextEntry(
                        new ZipEntry(concatStrings(parentPath, fileName + EcmFile.class.cast(obj).getFileActiveVersionNameExtension())));
                InputStream fileByteStream = fileService.downloadAsInputStream(obj.getId());
                copy(fileByteStream, zos);
            }
            else if (OBJECT_FOLDER_TYPE.equals(objectType))
            {
                AcmFolder childFolder = AcmFolder.class.cast(obj);

                String folderName = childFolder.getName();
                /*
                 * If foldername is duplicate, we will have to rename it.
                 * Otherwise, the zip file errors out.
                 * Here we just append an underscore "_" and date the file was created
                 */
                if (fileFolderList.contains(folderName))
                {
                    Date forFoldernameUniquenessDt = (obj instanceof AcmEntity) ? AcmEntity.class.cast(obj).getCreated() : new Date();
                    String forFoldernameUniqueness = format.format(forFoldernameUniquenessDt);
                    folderName = folderName + "_" + forFoldernameUniqueness;
                }
                fileFolderList.add(folderName);

                String entryName = concatStrings(parentPath, folderName, "/");
                zos.putNextEntry(new ZipEntry(entryName));
                compressFolder(zos, childFolder, entryName, compressNode);
            }
            zos.closeEntry();
        }

    }

    private boolean isFileSelected(Long fileId, CompressNode compressNode)
    {
        return compressNode.getSelectedNodes()
                .stream()
                .anyMatch(fileFolderNode -> fileFolderNode.getObjectId().equals(fileId) && fileFolderNode.isFolder() == false);
    }

    private boolean isFileParentFolderSelected(Long parentFolderId, CompressNode compressNode)
    {
        return compressNode.getSelectedNodes()
                .stream()
                .anyMatch(fileFolderNode -> fileFolderNode.getObjectId().equals(parentFolderId) && fileFolderNode.isFolder() == true);
    }

    private boolean isRootFolderSelected(CompressNode compressNode)
    {
        return compressNode.getSelectedNodes()
                .stream()
                .anyMatch(fileFolderNode -> fileFolderNode.getObjectId().equals(compressNode.getRootFolderId())
                        && fileFolderNode.isFolder() == true);
    }

    private boolean canBeCompressed(AcmObject acmObject, AcmFolder folder, CompressNode compressNode)
    {
        if (compressNode == null && OBJECT_FILE_TYPE.equals(acmObject.getObjectType().toUpperCase()))
        {
            return true;
        }
        else if (compressNode != null && OBJECT_FILE_TYPE.equals(acmObject.getObjectType().toUpperCase())
                && (isFileSelected(acmObject.getId(), compressNode) || isFileParentFolderSelected(folder.getId(), compressNode)
                        || isRootFolderSelected(compressNode)))
        {
            return true;
        }
        return false;
    }

    /**
     * Returns path of compressed folder file
     */
    @Override
    public String getCompressedFolderFilePath(AcmFolder folder)
    {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String pathSeparator = System.getProperty("file.separator");

        String filename = String.format(compressedFileNameFormat,
                tmpDir.endsWith(pathSeparator) ? tmpDir : concatStrings(tmpDir, pathSeparator), folder.getId(), folder.getName());
        return filename;
    }

    /**
     * Utility method used for string concatenation.
     *
     * @param pathParts
     *            an array of strings to be concatenated.
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
