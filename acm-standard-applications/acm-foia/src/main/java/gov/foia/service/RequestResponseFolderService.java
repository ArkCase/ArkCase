package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.convertfolder.ConversionException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import gov.foia.broker.FOIARequestFileBrokerClient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class RequestResponseFolderService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private ResponseFolderConverterService responseFolderConverterService;
    private ResponseFolderCompressorService responseFolderCompressorService;
    private FOIARequestFileBrokerClient foiaRequestFileBrokerClient;
    private ResponseFolderNotifyService responseFolderNotifyService;

    public void compressAndSendResponseFolderToPortal(Long requestId, String userName) throws ConversionException, AcmUserActionFailedException, AcmFolderException, AcmObjectNotFoundException
    {
        log.debug("Converting the Response folder for the request [{}]", requestId);
        getResponseFolderConverterService().convertResponseFolder(requestId, userName);

        log.debug("Compressing the Response folder for the request [{}]", requestId);
        getResponseFolderCompressorService().compressResponseFolder(requestId);

        log.debug("Sending the compressed Response folder file to outbound message queue the request [{}]", requestId);
        getFoiaRequestFileBrokerClient().sendReleaseFile(requestId);

        log.debug("Sending Email notification Response folder zip completed for the request [{}]", requestId);
        getResponseFolderNotifyService().sendEmailResponseCompressNotification(requestId);

    }

    public ResponseFolderConverterService getResponseFolderConverterService()
    {
        return responseFolderConverterService;
    }

    public void setResponseFolderConverterService(ResponseFolderConverterService responseFolderConverterService)
    {
        this.responseFolderConverterService = responseFolderConverterService;
    }

    public ResponseFolderCompressorService getResponseFolderCompressorService()
    {
        return responseFolderCompressorService;
    }

    public void setResponseFolderCompressorService(ResponseFolderCompressorService responseFolderCompressorService)
    {
        this.responseFolderCompressorService = responseFolderCompressorService;
    }

    public FOIARequestFileBrokerClient getFoiaRequestFileBrokerClient()
    {
        return foiaRequestFileBrokerClient;
    }

    public void setFoiaRequestFileBrokerClient(FOIARequestFileBrokerClient foiaRequestFileBrokerClient)
    {
        this.foiaRequestFileBrokerClient = foiaRequestFileBrokerClient;
    }

    public ResponseFolderNotifyService getResponseFolderNotifyService()
    {
        return responseFolderNotifyService;
    }

    public void setResponseFolderNotifyService(ResponseFolderNotifyService responseFolderNotifyService)
    {
        this.responseFolderNotifyService = responseFolderNotifyService;
    }
}
