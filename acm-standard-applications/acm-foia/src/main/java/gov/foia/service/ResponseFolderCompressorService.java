package gov.foia.service;

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
