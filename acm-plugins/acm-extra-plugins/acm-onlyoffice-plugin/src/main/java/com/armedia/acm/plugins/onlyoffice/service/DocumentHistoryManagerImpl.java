package com.armedia.acm.plugins.onlyoffice.service;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.onlyoffice.exceptions.OnlyOfficeException;
import com.armedia.acm.plugins.onlyoffice.model.DocumentHistory;
import com.armedia.acm.plugins.onlyoffice.model.DocumentHistoryVersion;
import com.armedia.acm.plugins.onlyoffice.model.OnlyOfficeConfig;
import com.armedia.acm.plugins.onlyoffice.model.callback.History;
import com.armedia.acm.plugins.onlyoffice.model.config.User;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;

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
    private Logger logger = LogManager.getLogger(getClass());
    private EcmFileDao ecmFileDao;
    private UserDao userDao;
    private ObjectMapper mapper;
    private OnlyOfficeConfig config;

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

        File ecmFileFolder = Paths.get(System.getProperty("user.home"), ".arkcase", config.getHistoryFolderPath(), ecmFile.getId().toString())
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
        return Paths.get(System.getProperty("user.home"), ".arkcase", config.getHistoryFolderPath(), fileId.toString(), version + ZIP_EXTENSION)
                .toFile();
    }

    @Override
    public DocumentHistory getDocumentHistory(Long fileId)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(config.getDocumentHistoryDateFormat());
        List<DocumentHistoryVersion> documentHistoryVersions = new ArrayList<>();
        EcmFile file = ecmFileDao.find(fileId);
        for (EcmFileVersion fileVersion : file.getVersions())
        {
            DocumentHistoryVersion docHistoryVersion;
            AcmUser acmUser = userDao.findByUserId(fileVersion.getCreator());
            // read already saved history changes
            File historyFile = Paths
                    .get(System.getProperty("user.home"), ".arkcase", config.getHistoryFolderPath(), fileId.toString(),
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

    public void setConfig(OnlyOfficeConfig config)
    {
        this.config = config;
    }
}
