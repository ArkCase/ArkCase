package com.armedia.acm.plugins.onlyoffice.service;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.onlyoffice.exceptions.OnlyOfficeException;
import com.armedia.acm.plugins.onlyoffice.model.DocumentHistory;
import com.armedia.acm.plugins.onlyoffice.model.DocumentHistoryVersion;
import com.armedia.acm.plugins.onlyoffice.model.callback.History;
import com.armedia.acm.plugins.onlyoffice.model.config.User;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DocumentHistoryManagerImpl implements DocumentHistoryManager
{
    private final String JSON_EXTENSION = ".json";
    private final String ZIP_EXTENSION = ".zip";

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String historyFolderPath;
    private EcmFileDao ecmFileDao;
    private UserDao userDao;
    private ObjectMapper mapper;
    private String documentHistoryDateFormat;

    @Override
    public void saveHistoryChanges(History history, String changesUrl, EcmFile ecmFile)
    {
        java.net.HttpURLConnection connection;
        try
        {
            URL url = new URL(changesUrl);
            connection = (java.net.HttpURLConnection) url.openConnection();
        }
        catch (IOException e)
        {
            throw new OnlyOfficeException("Provided url [" + changesUrl + "] is not accessible.", e);
        }

        File ecmFileFolder = Paths.get(System.getProperty("user.home"), ".arkcase", historyFolderPath, ecmFile.getId().toString())
                .toFile();
        try (InputStream stream = connection.getInputStream();
                OutputStream historyOS = new FileOutputStream(
                        Paths.get(ecmFileFolder.getPath(), ecmFile.getActiveVersionTag() + JSON_EXTENSION).toFile()))
        {
            if (connection.getResponseCode() == 200)
            {
                if (!ecmFileFolder.exists())
                {
                    ecmFileFolder.mkdirs();
                }
                // save changes zip file
                Files.copy(stream, Paths.get(ecmFileFolder.getPath(), ecmFile.getActiveVersionTag() + ZIP_EXTENSION),
                        StandardCopyOption.REPLACE_EXISTING);

                // write history as file
                mapper.writeValue(historyOS, history);
                historyOS.flush();
            }
            else
            {
                logger.error("File with history changes not saved. Got response status [{}]", connection.getResponseCode());
            }

        }
        catch (Exception e)
        {
            logger.error("File with history changes not saved. Reason: {}", e.getMessage(), e);
        }
    }

    @Override
    public File getHistoryChangesFile(Long fileId, String version)
    {
        return Paths.get(System.getProperty("user.home"), ".arkcase", historyFolderPath, fileId.toString(), version + ZIP_EXTENSION)
                .toFile();
    }

    @Override
    public DocumentHistory getDocumentHistory(Long fileId)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(documentHistoryDateFormat);
        List<DocumentHistoryVersion> documentHistoryVersions = new ArrayList<>();
        EcmFile file = ecmFileDao.find(fileId);
        for (EcmFileVersion fileVersion : file.getVersions())
        {
            DocumentHistoryVersion docHistoryVersion;
            AcmUser acmUser = userDao.findByUserId(fileVersion.getCreator());
            // read already saved history changes
            File historyFile = Paths
                    .get(System.getProperty("user.home"), ".arkcase", historyFolderPath, fileId.toString(),
                            fileVersion.getVersionTag() + ".json")
                    .toFile();
            if (historyFile.exists())
            {
                try (InputStream fileStream = new FileInputStream(historyFile))
                {
                    docHistoryVersion = mapper.readValue(fileStream, DocumentHistoryVersion.class);
                }
                catch (Exception e)
                {
                    docHistoryVersion = new DocumentHistoryVersion();
                }
            }
            else
            {
                docHistoryVersion = new DocumentHistoryVersion();
            }

            docHistoryVersion.setCreated(sdf.format(fileVersion.getCreated()));
            docHistoryVersion.setKey(fileId + "-" + fileVersion.getVersionTag());
            docHistoryVersion.setUser(new User(fileVersion.getCreator(), acmUser.getFullName()));
            docHistoryVersion.setVersion(fileVersion.getVersionTag());

            documentHistoryVersions.add(docHistoryVersion);
        }
        DocumentHistory documentHistory = new DocumentHistory();
        documentHistory.setHistory(documentHistoryVersions);
        documentHistory.setCurrentVersion(file.getActiveVersionTag());
        return documentHistory;
    }

    public void setHistoryFolderPath(String historyFolderPath)
    {
        this.historyFolderPath = historyFolderPath;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setMapper(ObjectMapper mapper)
    {
        this.mapper = mapper;
    }

    public void setDocumentHistoryDateFormat(String documentHistoryDateFormat)
    {
        this.documentHistoryDateFormat = documentHistoryDateFormat;
    }
}
