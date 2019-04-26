package com.armedia.acm.compressfolder.model;

/*-
 * #%L
 * ACM Service: Folder Compressing Service
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import org.springframework.beans.factory.annotation.Value;

public class CompressorServiceConfig
{
    /**
     * A formatting string that is used to generate the output file name. It takes 3 parameters, <code>tmpDir</code>,
     * <code>folderId</code> and <code>folderName</code>, for example <code>
     *      %1$sacm-%2$d-%3$s.zip
     * </code>
     */
    @Value("${compressor.fileNameFormat}")
    private String fileNameFormat;

    public String getFileNameFormat()
    {
        return fileNameFormat;
    }

    public void setFileNameFormat(String fileNameFormat)
    {
        this.fileNameFormat = fileNameFormat;
    }
}
