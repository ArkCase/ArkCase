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
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.portalgateway.model.PortalConfig;
import com.armedia.acm.services.holiday.service.HolidayConfigurationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

import gov.foia.broker.FOIARequestFileBrokerClient;
import gov.foia.dao.ResponseInstallmentDao;
import gov.foia.model.FOIARequest;
import gov.foia.model.ResponseInstallment;

public class RequestResponseFolderService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private ResponseFolderConverterService responseFolderConverterService;
    private ResponseFolderCompressorService responseFolderCompressorService;
    private FOIARequestFileBrokerClient foiaRequestFileBrokerClient;
    private ResponseFolderNotifyService responseFolderNotifyService;
    private CaseFileDao caseFileDao;
    private PortalConfig portalConfig;
    private HolidayConfigurationService holidayConfigurationService;
    private ResponseInstallmentDao responseInstallmentDao;

    public void compressAndSendResponseFolderToPortal(Long requestId, String userName)
            throws ConversionException, AcmUserActionFailedException, AcmFolderException, AcmObjectNotFoundException
    {
        log.debug("Converting the Response folder for the request [{}]", requestId);
        getResponseFolderConverterService().convertResponseFolder(requestId, userName);

        log.debug("Compressing the Response folder for the request [{}]", requestId);
        String filePath = getResponseFolderCompressorService().compressResponseFolder(requestId);

        log.debug("Sending the compressed Response folder file to outbound message queue the request [{}]", requestId);
        getFoiaRequestFileBrokerClient().sendReleaseFile(requestId, filePath);

        log.debug("Sending Email notification Response folder zip completed for the request [{}]", requestId);
        getResponseFolderNotifyService().sendEmailResponseCompressNotification(requestId);

        log.debug("Saving Response Installment details for the request [{}]", requestId);
        saveResponseInstallmentDetails(requestId);
    }

    public void saveResponseInstallmentDetails(Long requestId)
    {
        FOIARequest request = (FOIARequest) caseFileDao.find(requestId);

        Date today = new Date();
        Date dueDate = getHolidayConfigurationService().addWorkingDaysToDate(today, getPortalConfig().getNumOfAvailableDays());

        ResponseInstallment responseInstallment = new ResponseInstallment();

        responseInstallment.setDueDate(dueDate);
        responseInstallment.setMaxDownloadAttempts(getPortalConfig().getMaxDownloadAttempts());
        responseInstallment.setParentNumber(request.getCaseNumber());

        getResponseInstallmentDao().save(responseInstallment);
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

    public ResponseInstallmentDao getResponseInstallmentDao()
    {
        return responseInstallmentDao;
    }

    public void setResponseInstallmentDao(ResponseInstallmentDao responseInstallmentDao)
    {
        this.responseInstallmentDao = responseInstallmentDao;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public PortalConfig getPortalConfig()
    {
        return portalConfig;
    }

    public void setPortalConfig(PortalConfig portalConfig)
    {
        this.portalConfig = portalConfig;
    }

    public HolidayConfigurationService getHolidayConfigurationService()
    {
        return holidayConfigurationService;
    }

    public void setHolidayConfigurationService(HolidayConfigurationService holidayConfigurationService)
    {
        this.holidayConfigurationService = holidayConfigurationService;
    }
}
