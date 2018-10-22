package gov.foia.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 23, 2018
 *
 */
public interface ResponseFolderFileUpdateService
{

    /**
     * @param file
     * @param folderId
     * @param username
     */
    void updateFile(EcmFile file, Long folderId, String username);

}
