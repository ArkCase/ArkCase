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

import com.armedia.acm.convertfolder.ConversionException;
import com.armedia.acm.convertfolder.FolderConverter;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.admin.service.PDFConversionConfigurationService;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class ResponseFolderConverterService
{
    private CaseFileDao caseFileDao;

    private FolderConverter converter;

    private ResponseFolderService responseFolderService;

    private PDFConversionConfigurationService pdfConversionConfigurationService;

    public void convertResponseFolder(Long requestId, String username)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException, ConversionException
    {
        if (getPdfConversionConfigurationService().isResponseFolderConversionEnabled())
        {
            CaseFile request = caseFileDao.find(requestId);
            converter.convertFolder(responseFolderService.getResponseFolder(request).getId(), username);
        }
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
     * @param converter
     *            the converter to set
     */
    public void setConverter(FolderConverter converter)
    {
        this.converter = converter;
    }

    /**
     * @param responseFolderService
     *            the responseFolderService to set
     */
    public void setResponseFolderService(ResponseFolderService responseFolderService)
    {
        this.responseFolderService = responseFolderService;
    }

    public PDFConversionConfigurationService getPdfConversionConfigurationService()
    {
        return pdfConversionConfigurationService;
    }

    public void setPdfConversionConfigurationService(PDFConversionConfigurationService pdfConversionConfigurationService)
    {
        this.pdfConversionConfigurationService = pdfConversionConfigurationService;
    }
}
