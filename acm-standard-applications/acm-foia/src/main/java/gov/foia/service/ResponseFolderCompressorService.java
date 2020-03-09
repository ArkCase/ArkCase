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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import gov.foia.model.FOIARequest;
import gov.foia.model.FoiaConfig;
import gov.foia.model.event.RequestResponseFolderCompressedEvent;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 19, 2016
 */
public class ResponseFolderCompressorService implements ApplicationEventPublisherAware
{

    private CaseFileDao caseFileDao;

    private FolderCompressor compressor;

    private ResponseFolderService responseFolderService;

    private ApplicationEventPublisher applicationEventPublisher;

    private AcmFolderService acmFolderService;

    private FoiaConfig foiaConfig;

    public String compressResponseFolder(Long requestId)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException
    {
        FOIARequest request = (FOIARequest) caseFileDao.find(requestId);

        Long responseFolderId = getResponseFolderService().getResponseFolder(request).getId();

        String compressFileName = "";

        if (getAcmFolderService().getFolderChildren(responseFolderId).isEmpty())
        {
            return compressFileName;
        }

        if (isResponseLimited(request))
        {
            List<Long> compressFileIds = getFilesForLimitedRelease(responseFolderId);
            compressFileName = compressor.compressFiles(compressFileIds);
            publishLimitedResponseFolderCompressedEvent(request, getFoiaConfig().getLimitedDeliveryToSpecificPageCount());
        }
        else
        {
            compressFileName = compressor.compressFolder(responseFolderId);
            publishResponseFolderCompressedEvent(request);
        }
        return compressFileName;
    }

    private List<Long> getFilesForLimitedRelease(Long responseFolderId)
    {
        List<Long> compressFileIds = new ArrayList<>();

        int limitedPageCount = getFoiaConfig().getLimitedDeliveryToSpecificPageCount();
        int currentPageCount = 0;

        List<EcmFile> allFiles = getAcmFolderService().getFilesInFolderAndSubfolders(responseFolderId);
        allFiles = getCompressor().filterConvertedFiles(allFiles);
        allFiles.sort(Comparator.comparing(EcmFile::getCreated));

        for (EcmFile file : allFiles)
        {
            compressFileIds.add(file.getId());
            currentPageCount += file.getPageCount();

            if (currentPageCount > limitedPageCount)
            {
                break;
            }
        }
        return compressFileIds;
    }

    private boolean isResponseLimited(FOIARequest request)
    {
        return getFoiaConfig().getLimitedDeliveryToSpecificPageCountEnabled() && request.getLimitedDeliveryFlag();
    }

    private void publishResponseFolderCompressedEvent(CaseFile source)
    {
        RequestResponseFolderCompressedEvent event = new RequestResponseFolderCompressedEvent(source,
                AuthenticationUtils.getUserIpAddress());
        applicationEventPublisher.publishEvent(event);
    }

    private void publishLimitedResponseFolderCompressedEvent(CaseFile source, int pageCount)
    {
        RequestResponseFolderCompressedEvent event = new RequestResponseFolderCompressedEvent(source, pageCount,
                AuthenticationUtils.getUserIpAddress());
        applicationEventPublisher.publishEvent(event);
    }

    /**
     * @param caseFileDao
     *            the caseFileDao to set
     */
    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    /**
     * Return folder compressor for service
     */
    public FolderCompressor getCompressor()
    {
        return compressor;
    }

    /**
     * @param compressor
     *            the compressor to set
     */
    public void setCompressor(FolderCompressor compressor)
    {
        this.compressor = compressor;
    }

    /**
     * @return the responseFolderService
     */
    public ResponseFolderService getResponseFolderService()
    {
        return responseFolderService;
    }

    /**
     * @param responseFolderService
     *            the responseFolderService to set
     */
    public void setResponseFolderService(ResponseFolderService responseFolderService)
    {
        this.responseFolderService = responseFolderService;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public FoiaConfig getFoiaConfig()
    {
        return foiaConfig;
    }

    public void setFoiaConfig(FoiaConfig foiaConfig)
    {
        this.foiaConfig = foiaConfig;
    }

}
