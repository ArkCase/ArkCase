package com.armedia.acm.convertfolder.listener;

import com.armedia.acm.convertfolder.ConversionException;
import com.armedia.acm.convertfolder.FileConverter;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.event.EcmFileConvertEvent;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ConvertEcmFileEventListener implements ApplicationListener<EcmFileConvertEvent>
{
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

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
        catch (IOException | ConversionException e)
        {
            log.warn(String.format("Could not convert file [%s] to PDF", ecmFile.getFileName()), e);
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
