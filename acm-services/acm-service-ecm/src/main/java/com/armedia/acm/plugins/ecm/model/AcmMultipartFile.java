/**
 * 
 */
package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author riste.tutureski
 *
 */
public class AcmMultipartFile implements MultipartFile
{

    private String name;
    private String originalFilename;
    private String contentType;
    private boolean empty;
    private long size;
    private byte[] bytes;
    private InputStream inputStream;
    private String type;
    private MultipartFile multipartFile;

    public AcmMultipartFile()
    {

    }

    public AcmMultipartFile(MultipartFile multipartFile, boolean uniqueFileName)
    {
        this.multipartFile = multipartFile;

        if (uniqueFileName)
        {
            FolderAndFilesUtils folderAndFilesUtils = new FolderAndFilesUtils();
            this.name = folderAndFilesUtils.createUniqueIdentificator(this.multipartFile.getName());
            this.originalFilename = folderAndFilesUtils.createUniqueIdentificator(this.multipartFile.getOriginalFilename());
        }
        else
        {
            this.name = this.multipartFile.getName();
            this.originalFilename = this.multipartFile.getOriginalFilename();
        }
    }

    public AcmMultipartFile(String name, String originalFileName, String contentType, boolean empty, long size, byte[] bytes,
            InputStream inputStream, boolean uniqueFileName)
    {
        init(name, originalFileName, contentType, empty, size, bytes, inputStream, uniqueFileName);
    }

    public AcmMultipartFile(String name, String originalFileName, String contentType, boolean empty, long size, byte[] bytes,
            InputStream inputStream, boolean uniqueFileName, String type)
    {
        init(name, originalFileName, contentType, empty, size, bytes, inputStream, uniqueFileName);
        this.type = type;
    }

    private void init(String name, String originalFileName, String contentType, boolean empty, long size, byte[] bytes,
            InputStream inputStream, boolean uniqueFileName)
    {
        FolderAndFilesUtils folderAndFilesUtils = new FolderAndFilesUtils();
        if (uniqueFileName)
        {
            this.name = folderAndFilesUtils.createUniqueIdentificator(name);
            this.originalFilename = folderAndFilesUtils.createUniqueIdentificator(originalFileName);
        }
        else
        {
            this.name = name;
            this.originalFilename = originalFileName;
        }

        this.contentType = contentType;
        this.empty = empty;
        this.size = size;
        this.bytes = bytes;
        this.inputStream = inputStream;
    }

    @Override
    public String getName()
    {
        if (this.multipartFile != null)
        {
            return this.multipartFile.getName();
        }

        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getOriginalFilename()
    {
        if (this.multipartFile != null)
        {
            return this.multipartFile.getOriginalFilename();
        }

        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename)
    {
        this.originalFilename = originalFilename;
    }

    @Override
    public String getContentType()
    {
        if (this.multipartFile != null)
        {
            return this.multipartFile.getContentType();
        }

        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    @Override
    public boolean isEmpty()
    {
        if (this.multipartFile != null)
        {
            return this.multipartFile.isEmpty();
        }

        return empty;
    }

    public void setEmpty(boolean empty)
    {
        this.empty = empty;
    }

    @Override
    public long getSize()
    {
        if (this.multipartFile != null)
        {
            return this.multipartFile.getSize();
        }

        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    @Override
    public byte[] getBytes() throws IOException
    {
        if (this.multipartFile != null)
        {
            return this.multipartFile.getBytes();
        }

        return bytes;
    }

    public void setBytes(byte[] bytes)
    {
        this.bytes = bytes;
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        if (this.multipartFile != null)
        {
            return this.multipartFile.getInputStream();
        }

        return inputStream;
    }

    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException
    {
        if (this.multipartFile != null)
        {
            FileCopyUtils.copy(this.multipartFile.getBytes(), dest);
        }
        else
        {
            FileCopyUtils.copy(bytes, dest);
        }
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
