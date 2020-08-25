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

import static com.armedia.acm.plugins.ecm.model.EcmFileConstants.OBJECT_FOLDER_TYPE;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;

import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class ResponseFolderService
{

    private AcmFolderService folderService;

    private String responseFolderName;

    private CaseFileDao caseFileDao;

    public AcmFolder getResponseFolder(Long requestId)
            throws AcmFolderException, AcmUserActionFailedException, AcmObjectNotFoundException
    {

        CaseFile request = caseFileDao.find(requestId);
        return getResponseFolder(request);
    }

    public AcmFolder getResponseFolder(CaseFile request)
            throws AcmFolderException, AcmUserActionFailedException, AcmObjectNotFoundException
    {

        AcmFolder rootFolder = request.getContainer().getFolder();

        List<AcmObject> rootFolderChildren = folderService.getFolderChildren(rootFolder.getId());

        AcmFolder responseFolder = rootFolderChildren.stream()
                .filter(child -> child.getObjectType() != null && OBJECT_FOLDER_TYPE.equals(child.getObjectType())
                        && responseFolderName.equals(AcmFolder.class.cast(child).getName()))
                .map(child -> AcmFolder.class.cast(child)).findFirst().orElseThrow(() -> new AcmFolderException(
                        String.format("No response folder in folder with id %d was found!", rootFolder.getId())));

        return responseFolder;
    }

    /**
     * @return the folderService
     */
    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    /**
     * @param folderService
     *            the folderService to set
     */
    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    /**
     * @return the responseFolderName
     */
    public String getResponseFolderName()
    {
        return responseFolderName;
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
     * @return the caseFileDao
     */
    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    /**
     * @param caseFileDao
     *            the caseFileDao to set
     */
    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

}
