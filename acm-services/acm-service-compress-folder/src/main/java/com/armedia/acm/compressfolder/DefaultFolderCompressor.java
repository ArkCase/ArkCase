package com.armedia.acm.compressfolder;

/*-
 * #%L
 * ACM Service: Folder Compressing Service
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import static com.armedia.acm.plugins.ecm.model.EcmFileConstants.OBJECT_FILE_TYPE;
import static com.armedia.acm.plugins.ecm.model.EcmFileConstants.OBJECT_FOLDER_TYPE;
import static org.apache.commons.io.IOUtils.copy;

import com.armedia.acm.compressfolder.model.CompressNode;
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
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
import java.util.*;
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
    public String compressFolder(Long folderId) throws AcmFolderException
    {
        return compressFolder(folderId, maxSize, sizeUnit);
    }

    @Override
    public String compressFolder(CompressNode compressNode) throws AcmFolderException
    {
        return compressFolder(compressNode.getRootFolderId(), compressNode, maxSize, sizeUnit);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.compressfolder.FolderCompressor#compressFolder(java.lang.Long, long,
     * com.armedia.acm.compressfolder.SizeUnit)
     */
    @Override
    public String compressFolder(Long folderId, long size, SizeUnit sizeUnit) throws AcmFolderException
    {
        return compressFolder(folderId, null, size, sizeUnit);
    }

    @Override
    public String compressFolder(Long folderId, CompressNode compressNode, long size, SizeUnit sizeUnit)
            throws AcmFolderException
    {
        AcmFolder folder = Optional.ofNullable(folderService.findById(folderId)).orElseThrow(() -> new AcmFolderException(folderId));

        String filename = getCompressedFolderFilePath(folder);
        log.debug("ZIP creation: using [{}] as temporary file name", filename);
        File file = new File(filename);

        try (ZipOutputStream zos = new ZipOutputStream(new MaxThroughputAwareFileOutputStream(file, size, sizeUnit)))
        {
            zos.setLevel(Deflater.BEST_COMPRESSION);
            compressFolder(zos, folder, "", compressNode);
        }
        catch (AcmUserActionFailedException | AcmObjectNotFoundException | IOException e)
        {
            FileUtils.deleteQuietly(file);
            throw new AcmFolderException(e);
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
            throws AcmUserActionFailedException, AcmObjectNotFoundException, IOException
    {

        List<AcmObject> folderChildren = folderService.getFolderChildren(folder.getId()).stream().filter(obj -> obj.getObjectType() != null)
                .collect(Collectors.toList());
        List<String> fileFolderList = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("yyyy_M_d_k_m_s", Locale.ENGLISH);

        // all child objects of OBJECT_FILE_TYPE
        List<AcmObject> files = folderChildren.stream().filter(c -> OBJECT_FILE_TYPE.equals(c.getObjectType().toUpperCase()))
                .collect(Collectors.toList());
        files.forEach(c -> {
            try
            {
                EcmFile file = EcmFile.class.cast(c);
                if (canBeCompressed(file, files, folder, compressNode))
                {

                    String objectName = getUniqueObjectName(fileFolderList, format, c, file.getFileName());
                    fileFolderList.add(objectName);

                    String entryName = concatStrings(parentPath, objectName + file.getFileActiveVersionNameExtension());
                    zos.putNextEntry(new ZipEntry(entryName));
                    InputStream fileByteStream = fileService.downloadAsInputStream(c.getId());
                    copy(fileByteStream, zos);
                }
                zos.closeEntry();
            }
            catch (IOException e)
            {
                log.warn("ZIP creation: Error while creating zip entry for object with [{}] id of [{}] type.", c.getId(), c.getObjectType(),
                        e);
            }
            catch (AcmUserActionFailedException e)
            {
                log.warn("Error while downloading stream for object with [{}] id of [{}] type.", c.getId(), c.getObjectType(), e);
            }
        });

        // all child objects of OBJECT_FOLDER_TYPE
        List<AcmObject> folders = folderChildren.stream().filter(c -> OBJECT_FOLDER_TYPE.equals(c.getObjectType().toUpperCase()))
                .collect(Collectors.toList());
        folders.forEach(c -> {
            try
            {
                AcmFolder childFolder = AcmFolder.class.cast(c);

                String objectName = getUniqueObjectName(fileFolderList, format, c, childFolder.getName());
                String entryName = concatStrings(parentPath, objectName, "/");
                if(isFolderRequestedToBeCompressed(compressNode, childFolder))
                {
                   fileFolderList.add(objectName);
                   zos.putNextEntry(new ZipEntry(entryName));
                   zos.closeEntry();
                }
                compressFolder(zos, childFolder, entryName, compressNode);
            }
            catch (IOException e)
            {
                log.warn("ZIP creation: Error while creating zip entry for object with [{}] id of [{}] type.", c.getId(), c.getObjectType(),
                        e);
            }
            catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
            {
                log.warn("Error while downloading stream for object with [{}] id of [{}] type.", c.getId(), c.getObjectType(), e);
            }
        });

    }

    public boolean isFolderRequestedToBeCompressed(CompressNode compressNode, AcmFolder childFolder)
    {
       if(Objects.nonNull(compressNode) && Objects.nonNull(childFolder))
       {
           return compressNode
                   .getSelectedNodes()
                   .stream()
                   .anyMatch(fileFolderNode -> fileFolderNode.getObjectId().equals(childFolder.getId()) && fileFolderNode.isFolder())
                   ||
                   isFolderParentSelected(compressNode, childFolder)
                   ||
                   isRootFolderSelected(compressNode);
       }
       else
       {
           return true;
       }
    }

    private boolean isFolderParentSelected(CompressNode compressNode, AcmFolder childFolder)
    {
        if(childFolder.getParentFolder() != null)
        {
            if(compressNode.getSelectedNodes().stream().anyMatch(fileFolderNode -> fileFolderNode.getObjectId().equals(childFolder.getParentFolder().getId())))
            {
                return true;
            }
           return isFolderParentSelected(compressNode, childFolder.getParentFolder());
        }
        return false;
    }

    /*
     * If filename is duplicate, we will have to rename it.
     * Otherwise, the zip file errors out.
     * Here we just append an underscore "_" and date the file was created
     */
    private String getUniqueObjectName(List<String> fileFolderList, DateFormat format, AcmObject obj, String objectName)
    {
        if (fileFolderList.contains(objectName))
        {
            Date objectnameUniqueness = (obj instanceof AcmEntity) ? AcmEntity.class.cast(obj).getCreated() : new Date();
            objectName = objectName + "_" + format.format(objectnameUniqueness);
        }
        return objectName;
    }

    private boolean isFileSelected(Long fileId, CompressNode compressNode)
    {
        return compressNode.getSelectedNodes()
                .stream()
                .anyMatch(fileFolderNode -> fileFolderNode.getObjectId().equals(fileId) && !fileFolderNode.isFolder());
    }

    private boolean isFileParentFolderSelected(Long parentFolderId, CompressNode compressNode)
    {
        return compressNode.getSelectedNodes()
                .stream()
                .anyMatch(fileFolderNode -> fileFolderNode.getObjectId().equals(parentFolderId) && fileFolderNode.isFolder());
    }

    private boolean isRootFolderSelected(CompressNode compressNode)
    {
        return compressNode.getSelectedNodes()
                .stream()
                .anyMatch(fileFolderNode -> fileFolderNode.getObjectId().equals(compressNode.getRootFolderId())
                        && fileFolderNode.isFolder());
    }

    private boolean canBeCompressed(EcmFile file, List<AcmObject> files, AcmFolder folder, CompressNode compressNode)
    {
        if (isConverted(file, files))
        {
            return false;
        }
        if (compressNode == null)
        {
            return true;
        }
        else if (compressNode != null
                && (isFileSelected(file.getId(), compressNode) || isFileParentFolderSelected(folder.getId(), compressNode)
                        || isRootFolderSelected(compressNode)))
        {
            return true;
        }
        return false;
    }

    /**
     * @param
     * @param files
     * @return
     */
    private boolean isConverted(EcmFile file, List<AcmObject> files)
    {
        // TODO: Currently, base file name is used to link the original file with the PDF rendition. We should devise a
        // way to associate the rendition with the original file trough means other than base file name.
        if (".pdf".equalsIgnoreCase(file.getFileActiveVersionNameExtension()))
        {
            return false;
        }
        else if (files.stream().map(f -> EcmFile.class.cast(f)).filter(f -> ".pdf".equalsIgnoreCase(f.getFileActiveVersionNameExtension()))
                .anyMatch(f -> f.getFileName().equals(file.getFileName())))
        {
            return true;
        }
        else
        {
            return false;
        }
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
