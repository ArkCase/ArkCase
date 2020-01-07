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

import com.armedia.acm.pluginmanager.service.AcmConfigurablePlugin;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.onlyoffice.exceptions.UnsupportedExtension;
import com.armedia.acm.plugins.onlyoffice.model.OnlyOfficeConfig;
import com.armedia.acm.plugins.onlyoffice.model.config.Config;
import com.armedia.acm.plugins.onlyoffice.model.config.Document;
import com.armedia.acm.plugins.onlyoffice.model.config.DocumentInfo;
import com.armedia.acm.plugins.onlyoffice.model.config.DocumentPermissions;
import com.armedia.acm.plugins.onlyoffice.model.config.EditorConfig;
import com.armedia.acm.plugins.onlyoffice.model.config.EditorCustomization;
import com.armedia.acm.plugins.onlyoffice.model.config.User;
import com.armedia.acm.plugins.onlyoffice.util.DocumentTypeResolver;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;

import java.text.SimpleDateFormat;

public class ConfigServiceImpl implements ConfigService, AcmConfigurablePlugin
{
    private Logger logger = LogManager.getLogger(getClass());
    private EcmFileDao ecmFileDao;
    private ArkPermissionEvaluator arkPermissionEvaluator;
    private DocumentTypeResolver documentTypeResolver;
    private OnlyOfficeConfig onlyOfficeConfig;

    @Override
    public Config getConfig(Long fileId, String mode, String lang, Authentication auth, String token, AcmUser user)
    {
        EcmFile ecmFile = ecmFileDao.find(fileId);

        Config config = new Config();
        config.setHeight(onlyOfficeConfig.getConfigViewHeight());
        config.setWidth(onlyOfficeConfig.getConfigViewWidth());
        config.setType(onlyOfficeConfig.getConfigViewType());
        if (ecmFile.getFileExtension() == null)
        {
            throw new UnsupportedExtension("Extension is not specified for document id[" + ecmFile.getId() + "].");
        }
        config.setDocumentType(documentTypeResolver.resolveDocumentType(ecmFile.getFileExtension().replace(".", "")));

        setDocumentConfig(config, ecmFile, auth, token);
        setEditorConfig(config, user, mode, lang, token, fileId);

        return config;
    }

    private void setDocumentConfig(Config config, EcmFile ecmFile, Authentication authentication, String authTicket)
    {
        // set document
        if (config.getDocument() == null)
        {
            config.setDocument(
                    new Document(ecmFile.getFileExtension().replace(".", ""),
                            String.format("%s-%s", ecmFile.getId(), ecmFile.getActiveVersionTag()),
                            ecmFile.getFileName(),
                            String.format("%s/api/v1/plugin/ecm/download?ecmFileId=%s&acm_email_ticket=%s",
                                    onlyOfficeConfig.getArkcaseBaseUrl(),
                                    ecmFile.getFileId(),
                                    authTicket)));
        }
        Document document = config.getDocument();

        // set info of the document
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setAuthor(ecmFile.getCreator());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ss hh:mm");
        documentInfo.setCreated(formatter.format(ecmFile.getCreated()));
        document.setInfo(documentInfo);
        // set permissions
        if (document.getPermissions() == null)
        {
            boolean reviewPermission = arkPermissionEvaluator.hasPermission(authentication, ecmFile.getFileId(), "FILE",
                    "reviewOnlyOfficeDocument");
            boolean commentPermission = arkPermissionEvaluator.hasPermission(authentication, ecmFile.getFileId(), "FILE",
                    "commentOnlyOfficeDocument");
            boolean writePermission = arkPermissionEvaluator.hasPermission(authentication, ecmFile.getFileId(), "FILE",
                    "editOnlyOfficeDocument");
            boolean downloadPermission = arkPermissionEvaluator.hasPermission(authentication, ecmFile.getFileId(), "FILE",
                    "downloadOnlyOfficeDocument");
            boolean printPermission = arkPermissionEvaluator.hasPermission(authentication, ecmFile.getFileId(), "FILE",
                    "printOnlyOfficeDocument");
            document.setPermissions(new DocumentPermissions(
                    commentPermission,
                    downloadPermission,
                    writePermission,
                    printPermission,
                    reviewPermission));
        }

    }

    private void setEditorConfig(Config config, AcmUser acmUser, String mode, String lang, String authTicket, Long fileId)
    {
        // set editorConfig
        if (config.getEditorConfig() == null)
        {
            config.setEditorConfig(
                    new EditorConfig(
                            String.format("%s/api/onlyoffice/callback?acm_email_ticket=%s&ecmFileId=%s",
                                    onlyOfficeConfig.getArkcaseBaseUrl(), authTicket, fileId.toString())));
        }
        EditorConfig editorConfig = config.getEditorConfig();
        editorConfig.setMode(mode);
        editorConfig.setLang(lang);

        User user = new User();
        user.setId(acmUser.getUserId());
        user.setName(acmUser.getFullName());
        editorConfig.setUser(user);

        // set editor customization
        if (editorConfig.getCustomization() == null)
        {
            editorConfig.setCustomization(new EditorCustomization());
        }
        EditorCustomization customization = editorConfig.getCustomization();
        customization.setChat(true);
        customization.setAutosave(true);
        customization.setComments(true);
        customization.setShowReviewChanges(true);
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    @Override
    public String getDocumentServerUrlApi()
    {
        return onlyOfficeConfig.getDocumentServerUrlApi();
    }

    @Override
    public String getArkcaseBaseUrl()
    {
        return onlyOfficeConfig.getArkcaseBaseUrl();
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }

    public void setDocumentTypeResolver(DocumentTypeResolver documentTypeResolver)
    {
        this.documentTypeResolver = documentTypeResolver;
    }

    @Override
    public boolean isEnabled()
    {
        return onlyOfficeConfig.isPluginEnabled();
    }

    @Override
    public String getName()
    {
        return OnlyOfficeConfig.getOnlyOfficePluginName();
    }

    @Override
    public boolean isOutboundSignEnabled()
    {
        return onlyOfficeConfig.isOutboundSignEnabled();
    }

    public void setOnlyOfficeConfig(OnlyOfficeConfig onlyOfficeConfig)
    {
        this.onlyOfficeConfig = onlyOfficeConfig;
    }
}
