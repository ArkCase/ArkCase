package com.armedia.acm.plugins.onlyoffice.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.onlyoffice.model.DocumentHistory;
import com.armedia.acm.plugins.onlyoffice.model.callback.History;

import java.io.File;

public interface DocumentHistoryManager
{
    void saveHistoryChanges(History history, String changesUrl, EcmFile ecmFile);

    File getHistoryChangesFile(Long fileId, String version);

    DocumentHistory getDocumentHistory(Long fileId);
}
