/**
 *
 */
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

import com.armedia.acm.compressfolder.model.CompressNode;
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 20, 2016
 *
 */
public interface FolderCompressor
{

    /**
     * Compresses the folder contents by using the <code>maxSize</code> and <code>sizeUnit</code> for setting the output
     * file size limit.
     *
     * @param folderId
     *            the ID of the folder to be compressed.
     * @return the path to the file on the file system where the output file was stored on the machine where the
     *         application is running.
     * @throws FolderCompressorException
     *             if a folder for the given <code>folderId</code> does not exist, or there was an
     *             IO exception during retrieving, writing or compressing the folder.
     *
     * @see #maxSize
     * @see #sizeUnit
     */
    String compressFolder(Long folderId) throws AcmFolderException;

    /**
     * Compresses the folder contents by using the <code>maxSize</code> and <code>sizeUnit</code> for setting the output
     * file size limit.
     *
     * @param compressNode
     *            object passed from frontend containing the selected nodes and root node
     * @return the path to the file on the file system where the output file was stored on the machine where the
     *         application is running.
     * @throws AcmFolderException
     *             if a folder for the given <code>folderId</code> does not exist, or there was an
     *             IO exception during retrieving, writing or compressing the folder.
     *
     * @see #maxSize
     * @see #sizeUnit
     */
    String compressFolder(CompressNode compressNode) throws AcmFolderException;

    String compressFolder(CompressNode compressNode, Authentication authentication) throws AcmFolderException;

    /**
     * Compresses the folder contents by using the <code>size</code> and <code>sizeUnit</code> arguments for setting the
     * output file size limit.
     *
     * @param folderId
     *            the ID of the folder to be compressed.
     * @param size
     *            size of the output file expressed in <code>sizeUnit</code>s.
     * @param sizeUnit
     *            size unit used to calculate the max compressed size in bytes.
     * @return the path to the file on the file system where the output file was stored on the machine where the
     *         application is running.
     * @throws AcmFolderException
     *             if a folder for the given <code>folderId</code> does not exist, or there was an
     *             IO exception during retrieving, writing or compressing the folder.
     */
    String compressFolder(Long folderId, long size, SizeUnit sizeUnit) throws AcmFolderException;

    String compressFolder(Long folderId, CompressNode compressNode, long size, SizeUnit sizeUnit)
            throws AcmFolderException;

    void compressFiles(List<Long> fileIds, Authentication authentication) throws Exception;

    String compressFiles(List<Long> fileIds);

    boolean canBeCompressed(EcmFile file, List<AcmObject> files, AcmFolder folder, CompressNode compressNode);

    List<EcmFile> filterConvertedFiles(List<EcmFile> files);

    /**
     * Returns path for the compressed folder file
     *
     * @param folder
     * @return the path of the compressed folder file
     */
    String getCompressedFolderFilePath(AcmFolder folder);

}
