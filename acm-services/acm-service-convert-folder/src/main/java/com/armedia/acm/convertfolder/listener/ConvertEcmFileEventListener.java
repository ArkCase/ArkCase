package com.armedia.acm.convertfolder.listener;

/*-
 * #%L
 * ACM Service: Folder Converting Service
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

import com.armedia.acm.convertfolder.ConversionException;
import com.armedia.acm.convertfolder.FileConverter;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.event.EcmFileConvertEvent;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ConvertEcmFileEventListener implements ApplicationListener<EcmFileConvertEvent>
{
    private final Logger log = LogManager.getLogger(getClass().getName());

    private FileConverter fileConverter;

    @Override
    public void onApplicationEvent(EcmFileConvertEvent ecmFileConvertEvent)
    {
        EcmFile ecmFile = (EcmFile) ecmFileConvertEvent.getSource();
        String tmpPdfConvertedFullFileName = (String) ecmFileConvertEvent.getEventProperties().get("tmpPdfConvertedFullFileName");
        String fileVersion = (String) ecmFileConvertEvent.getEventProperties().get("ecmFileVersion");

        convertEcmFile(ecmFile, fileVersion, tmpPdfConvertedFullFileName);
    }

    private void convertEcmFile(EcmFile ecmFile, String version, String tmpPdfConvertedFullFileName)
    {
        File convertedFile = null;
        File tmpPdfConvertedFile = new File(tmpPdfConvertedFullFileName);

        try
        {
            convertedFile = getFileConverter().convertAndReturnConvertedFile(ecmFile, version);
            FileUtils.copyFile(convertedFile, tmpPdfConvertedFile);
        }
        catch (IOException | ConversionException | NullPointerException e)
        {
            log.warn(String.format("Could not convert file [%s] to PDF", ecmFile.getFileName()), e);
            FileUtils.deleteQuietly(tmpPdfConvertedFile);
        }
        finally
        {
            if(Objects.nonNull(convertedFile))
            {
                FileUtils.deleteQuietly(convertedFile);
            }
        }
    }


    public FileConverter getFileConverter()
    {
        return fileConverter;
    }

    public void setFileConverter(FileConverter fileConverter)
    {
        this.fileConverter = fileConverter;
    }
}
