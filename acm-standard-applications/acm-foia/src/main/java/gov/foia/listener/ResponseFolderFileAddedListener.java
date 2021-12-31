package gov.foia.listener;

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

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFilePostUploadEvent;
import gov.foia.model.FOIARequest;
import gov.foia.service.ResponseFolderFileUpdateService;
import org.springframework.context.ApplicationListener;

import java.util.Optional;

import static com.armedia.acm.plugins.casefile.model.CaseFileConstants.OBJECT_TYPE;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 21, 2018
 *
 */
public class ResponseFolderFileAddedListener implements ApplicationListener<EcmFilePostUploadEvent>
{

    private String responseFolderName;

    private String releaseQueueName;

    private CaseFileDao caseFileDao;

    private ResponseFolderFileUpdateService fileUpdateService;

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(EcmFilePostUploadEvent event)
    {
        EcmFile file = event.getFile();

        if (!OBJECT_TYPE.equals(file.getContainer().getContainerObjectType()))
        {
            return;
        }

        FOIARequest request = (FOIARequest) caseFileDao.find(file.getContainer().getContainerObjectId());
        if (!releaseQueueName.equals(request.getQueue().getName()))
        {
            return;
        }

        if (request.getLegacySystemId() != null && !request.getLegacySystemId().trim().isEmpty())
        {
            // do not convert response folder documents in migrated requests, and do not compress the
            // response folder.
            return;
        }

        Optional<AcmFolder> folder = getResponseFolder(file);

        if (folder.isPresent())
        {
            fileUpdateService.updateFile(file, folder.get().getId(), event.getUsername());
        }

    }

    private Optional<AcmFolder> getResponseFolder(EcmFile file)
    {
        AcmFolder folder = file.getFolder();

        while (folder != null)
        {
            if (responseFolderName.equalsIgnoreCase(folder.getName()))
            {
                break;
            }
            folder = folder.getParentFolder();
        }

        return Optional.ofNullable(folder);
    }

    /**
     * @param responseFolderName
     *            the responseFolderName to set
     */
    public void setResponseFolderName(String responseFolderName)
    {
        this.responseFolderName = responseFolderName;
    }

    /**
     * @param releaseQueueName
     *            the releaseQueueName to set
     */
    public void setReleaseQueueName(String releaseQueueName)
    {
        this.releaseQueueName = releaseQueueName;
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
     * @param fileUpdateService
     *            the fileUpdateService to set
     */
    public void setFileUpdateService(ResponseFolderFileUpdateService fileUpdateService)
    {
        this.fileUpdateService = fileUpdateService;
    }

}
