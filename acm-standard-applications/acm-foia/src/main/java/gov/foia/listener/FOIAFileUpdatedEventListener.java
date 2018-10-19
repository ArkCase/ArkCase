package gov.foia.listener;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import javax.jms.JMSException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import gov.foia.broker.FOIARequestFileBrokerClient;
import gov.foia.model.FOIAFile;

public class FOIAFileUpdatedEventListener implements ApplicationListener<EcmFileUpdatedEvent>
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private FOIARequestFileBrokerClient foiaRequestFileBrokerClient;
    private EcmFileService ecmFileService;

    @Override
    public void onApplicationEvent(EcmFileUpdatedEvent ecmFileUpdatedEvent)
    {

        EcmFile updatedEcmFile;
        EcmFile oldEcmFile;

        updatedEcmFile = Objects.nonNull(ecmFileUpdatedEvent.getSource()) ? (EcmFile) ecmFileUpdatedEvent.getSource() : null;
        oldEcmFile = Objects.nonNull(ecmFileUpdatedEvent.getEventProperties())
                ? (EcmFile) ecmFileUpdatedEvent.getEventProperties().getOrDefault("oldEcmFile", null)
                : null;

        if (Objects.nonNull(updatedEcmFile) && Objects.nonNull(oldEcmFile))
        {
            if (updatedEcmFile instanceof FOIAFile && oldEcmFile instanceof FOIAFile)
            {
                FOIAFile updatedFoiaFile = (FOIAFile) updatedEcmFile;
                FOIAFile oldFoiaFile = (FOIAFile) oldEcmFile;
                if (oldFoiaFile.getPublicFlag() == false && updatedFoiaFile.getPublicFlag() == true)
                {
                    publishEcmFile(updatedFoiaFile);
                }

            }
        }
    }

    private void publishEcmFile(EcmFile ecmFile)
    {
        File tempFile = null;
        InputStream ecmFileInputStream = null;
        OutputStream ecmFileOutputStream = null;

        try
        {
            String currDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMdd-HHmmss-SSS"));
            tempFile = File.createTempFile(ecmFile.getFileName() + "-" + currDateTime, ecmFile.getFileActiveVersionNameExtension());

            ecmFileOutputStream = new FileOutputStream(tempFile);
            ecmFileInputStream = getEcmFileService().downloadAsInputStream(ecmFile.getId());

            IOUtils.copy(ecmFileInputStream, ecmFileOutputStream);

            Map<String, Object> fileProperties = new HashMap<>();

            fileProperties.put("fileId", ecmFile.getId());
            fileProperties.put("fileName", ecmFile.getFileName().concat(ecmFile.getFileActiveVersionNameExtension()));
            getFoiaRequestFileBrokerClient().sendFile(tempFile, fileProperties);
        }
        catch (AcmUserActionFailedException | IOException | JMSException e)
        {
            log.error("Could not publish Ecm File", e);
        }
        finally
        {
            IOUtils.closeQuietly(ecmFileInputStream);
            IOUtils.closeQuietly(ecmFileOutputStream);
            FileUtils.deleteQuietly(tempFile);
        }
    }

    public FOIARequestFileBrokerClient getFoiaRequestFileBrokerClient()
    {
        return foiaRequestFileBrokerClient;
    }

    public void setFoiaRequestFileBrokerClient(FOIARequestFileBrokerClient foiaRequestFileBrokerClient)
    {
        this.foiaRequestFileBrokerClient = foiaRequestFileBrokerClient;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
}
