package gov.privacy.service;

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

import gov.privacy.model.SARConfig;
import gov.privacy.model.SubjectAccessRequest;
import gov.privacy.model.event.RequestResponseFolderCompressedEvent;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class ResponseFolderCompressorService implements ApplicationEventPublisherAware
{

    private CaseFileDao caseFileDao;

    private FolderCompressor compressor;

    private ResponseFolderService responseFolderService;

    private ApplicationEventPublisher applicationEventPublisher;

    private AcmFolderService acmFolderService;

    private SARConfig SARConfig;

    public String compressResponseFolder(Long requestId)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException
    {
        SubjectAccessRequest request = (SubjectAccessRequest) caseFileDao.find(requestId);

        Long responseFolderId = getResponseFolderService().getResponseFolder(request).getId();

        String compressFileName = "";

        if (getAcmFolderService().getFolderChildren(responseFolderId).isEmpty())
        {
            return compressFileName;
        }

        compressFileName = compressor.compressFolder(responseFolderId);
        caseFileDao.save(request);
        publishResponseFolderCompressedEvent(request);

        return compressFileName;
    }

    private List<Long> getFilesForLimitedRelease(Long responseFolderId)
    {
        List<Long> compressFileIds = new ArrayList<>();

        int limitedPageCount = getSARConfig().getLimitedDeliveryToSpecificPageCount();
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

    public SARConfig getSARConfig()
    {
        return SARConfig;
    }

    public void setSARConfig(SARConfig SARConfig)
    {
        this.SARConfig = SARConfig;
    }

}
