package gov.foia.service;

import com.armedia.acm.convertfolder.ConversionException;
import com.armedia.acm.convertfolder.FolderConverter;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 17, 2018
 *
 */
public class ResponseFolderConverterService
{
    private CaseFileDao caseFileDao;

    private FolderConverter converter;

    private ResponseFolderService responseFolderService;

    public void convertResponseFolder(Long requestId, String username)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException, ConversionException
    {
        CaseFile request = caseFileDao.find(requestId);
        converter.convertFolder(responseFolderService.getResponseFolder(request).getId(), username);
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
}
