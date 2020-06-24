package gov.privacy.listener;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import gov.privacy.broker.SARFileBrokerClient;
import gov.privacy.model.SARFile;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 */
public class SARFileUpdatedEventListener implements ApplicationListener<EcmFileUpdatedEvent>
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private SARFileBrokerClient SARFileBrokerClient;
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
            if (updatedEcmFile instanceof SARFile && oldEcmFile instanceof SARFile)
            {
                SARFile updatedSARFile = (SARFile) updatedEcmFile;
                SARFile oldSARFile = (SARFile) oldEcmFile;
                if (oldSARFile.getPublicFlag() == false && updatedSARFile.getPublicFlag() == true)
                {
                    publishEcmFile(updatedSARFile);
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
            getSARFileBrokerClient().sendFile(tempFile, fileProperties);
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

    public SARFileBrokerClient getSARFileBrokerClient()
    {
        return SARFileBrokerClient;
    }

    public void setSARFileBrokerClient(SARFileBrokerClient SARFileBrokerClient)
    {
        this.SARFileBrokerClient = SARFileBrokerClient;
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
