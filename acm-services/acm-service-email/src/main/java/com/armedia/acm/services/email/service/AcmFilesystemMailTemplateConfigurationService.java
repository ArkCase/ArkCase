package com.armedia.acm.services.email.service;

/*-
 * #%L
 * ACM Service: Email
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


import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

import javax.validation.ValidationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 6, 2017
 *
 */
public class AcmFilesystemMailTemplateConfigurationService implements AcmMailTemplateConfigurationService
{
    private Logger log = LogManager.getLogger(getClass());
    private String templateFolderPath;
    private ReadWriteLock lock = new ReentrantReadWriteLock();


    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#getTemplate(java.lang.String)
     */
    @Override
    public String getTemplate(String templateName) throws AcmEmailConfigurationException
    {
        File templateFolder = getTemplateFolder();
        File templateFile = new File(templateFolder, templateName);
        try{
            String absolutePath = templateFile.getCanonicalPath();
            if (!absolutePath.startsWith(templateFolder.getCanonicalPath())){
                log.error("Template name {} does not validate.", templateName);
                throw new ValidationException("Invalid path constructed!");
            }
        }catch (IOException e)
        {
            log.error("Error while reading contents of {} email template.", templateName, e);
            throw new ValidationException("Invalid path constructed!");
        }

        Lock readLock = lock.readLock();
        try
        {
            readLock.lock();
            if (!templateFile.exists())
            {
                log.warn("Email template {} does not exist.", templateName);
                throw new AcmEmailConfigurationIOException(String.format("Email template %s does not exist.", templateName));
            }
            return FileUtils.readFileToString(templateFile, "UTF-8");
        }
        catch (IOException e)
        {
            log.warn("Error while reading contents of {} email template.", templateName, e);
            throw new AcmEmailConfigurationIOException(String.format("Error while reading contents of %s email template.", templateName),
                    e);
        }
        finally
        {
            readLock.unlock();
        }
    }


    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService#getExceptionMapper(com.armedia.acm.
     * services.email.service.AcmEmailServiceException)
     */
    @Override
    public <ME extends AcmEmailServiceException> AcmEmailServiceExceptionMapper<ME> getExceptionMapper(AcmEmailServiceException e)
    {
        return new FilesystemMailTemplateConfigurationExceptionMapper<>();
    }

    /**
     * @return
     */
    private File getTemplateFolder()
    {
        return new File(templateFolderPath);
    }


    /**
     * @param templateFolderPath
     *            the templateFolderPath to set
     */
    public void setTemplateFolderPath(String templateFolderPath)
    {
        this.templateFolderPath = templateFolderPath;
    }


    /**
     * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 8, 2017
     *
     */
    private class FilesystemMailTemplateConfigurationExceptionMapper<ME extends AcmEmailServiceException>
            implements AcmEmailServiceExceptionMapper<ME>
    {

        /*
         * (non-Javadoc)
         * @see
         * com.armedia.acm.services.email.service.AcmEmailServiceExceptionMapper#mapException(com.armedia.acm.services.
         * email.service.AcmEmailServiceException)
         */
        @Override
        public Object mapException(ME me)
        {
            Map<String, Object> errorDetails = new HashMap<>();
            if (me instanceof AcmEmailConfigurationIOException)
            {
                errorDetails.put("error_cause", "READ_WRITE_ERROR.");
            }
            else if (me instanceof AcmEmailConfigurationJsonException)
            {
                errorDetails.put("error_cause", "JSON_PARSING_ERROR.");
            }
            else if (me instanceof AcmEmailConfigurationException)
            {
                errorDetails.put("error_cause", "INTERENAL_SERVER_ERROR.");
            }
            else
            {
                errorDetails.put("error_cause", "UNKOWN_ERROR.");
            }
            errorDetails.put("error_message", me.getMessage());
            return errorDetails;
        }

        /*
         * (non-Javadoc)
         * @see com.armedia.acm.services.email.service.AcmEmailServiceExceptionMapper#getStatusCode()
         */
        @Override
        public HttpStatus getStatusCode()
        {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

}
