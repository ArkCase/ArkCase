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

import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 19, 2016
 */
public class ResponseFolderCompressorService
{

    private CaseFileDao caseFileDao;

    private FolderCompressor compressor;

    private ResponseFolderService responseFolderService;

    public String compressResponseFolder(Long requestId)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException
    {

        CaseFile request = caseFileDao.find(requestId);
        return compressor.compressFolder(getResponseFolderService().getResponseFolder(request).getId());
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

}
