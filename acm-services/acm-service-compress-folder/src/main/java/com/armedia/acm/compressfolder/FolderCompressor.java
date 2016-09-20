/**
 * 
 */
package com.armedia.acm.compressfolder;

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
     * @param folderId the ID of the folder to be compressed.
     * @return the path to the file on the file system where the output file was stored on the machine where the
     *         application is running.
     * @throws FolderCompressorException if a folder for the given <code>folderId</code> does not exist, or there was an
     *             IO exception during retrieving, writing or compressing the folder.
     *
     * @see #maxSize
     * @see #sizeUnit
     */
    String compressFolder(Long folderId) throws FolderCompressorException;

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
    String compressFolder(Long folderId, long size, SizeUnit sizeUnit) throws FolderCompressorException;

}