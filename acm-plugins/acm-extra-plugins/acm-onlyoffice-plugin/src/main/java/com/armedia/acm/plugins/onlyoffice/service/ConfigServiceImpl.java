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
import com.armedia.acm.plugins.onlyoffice.model.config.Config;
import com.armedia.acm.plugins.onlyoffice.model.config.Document;
import com.armedia.acm.plugins.onlyoffice.model.config.DocumentInfo;
import com.armedia.acm.plugins.onlyoffice.model.config.DocumentPermissions;
import com.armedia.acm.plugins.onlyoffice.model.config.EditorConfig;
import com.armedia.acm.plugins.onlyoffice.model.config.EditorCustomization;
import com.armedia.acm.plugins.onlyoffice.model.config.User;
import com.armedia.acm.plugins.onlyoffice.util.DocumentTypeResolver;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.text.SimpleDateFormat;

public class ConfigServiceImpl implements ConfigService, AcmConfigurablePlugin
{
    private Logger logger = LoggerFactory.getLogger(getClass());
    private EcmFileDao ecmFileDao;
    private UserDao userDao;
    private AuthenticationTokenService authenticationTokenService;
    private ArkPermissionEvaluator arkPermissionEvaluator;
    private DocumentTypeResolver documentTypeResolver;

    private String documentServerUrlApi;
    private String arkcaseBaseUrl;
    private Boolean pluginEnabled;
    private static final String ONLY_OFFICE_PLUGIN = "ONLY_OFFICE";

    @Override
    public Config getConfig(Long fileId, Authentication auth)
    {
        EcmFile ecmFile = ecmFileDao.find(fileId);
        String userId = auth.getName();
        AcmUser user = userDao.findByUserId(userId);
        String authTicket = authenticationTokenService.getTokenForAuthentication(auth);

        Config config = new Config();
        config.setHeight("100%");
        config.setWidth("100%");
        config.setType("desktop");
        if (ecmFile.getFileExtension() == null)
        {
            throw new UnsupportedExtension("Extension is not specified for document id[" + ecmFile.getId() + "].");
        }
        config.setDocumentType(documentTypeResolver.resolveDocumentType(ecmFile.getFileExtension().replace(".", "")));

        setDocumentConfig(config, ecmFile, user, authTicket);
        setEditorConfig(config, user, authTicket);

        return config;
    }

    private void setDocumentConfig(Config config, EcmFile ecmFile, AcmUser acmUser, String authTicket)
    {
        // set document
        if (config.getDocument() == null)
        {
            config.setDocument(
                    new Document(ecmFile.getFileExtension().replace(".", ""),
                            String.format("%s-%s", ecmFile.getId(), ecmFile.getActiveVersionTag()),
                            ecmFile.getFileName(),
                            String.format("%s/api/v1/plugin/ecm/download?ecmFileId=%s&acm_ticket=%s",
                                    arkcaseBaseUrl,
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
            Authentication authentication = authenticationTokenService.getAuthenticationForToken(authTicket);
            boolean reviewPermission = arkPermissionEvaluator.hasPermission(authentication, ecmFile.getFileId(), "FILE",
                    "write|group-write");
            boolean writePermission = arkPermissionEvaluator.hasPermission(authentication, ecmFile.getFileId(), "FILE",
                    "write|group-write");
            boolean downloadPermission = false;
            boolean printPermission = true;
            document.setPermissions(new DocumentPermissions(
                    reviewPermission,
                    downloadPermission,
                    writePermission,
                    printPermission,
                    reviewPermission));
        }

    }

    private void setEditorConfig(Config config, AcmUser acmUser, String authTicket)
    {
        // set editorConfig
        if (config.getEditorConfig() == null)
        {
            config.setEditorConfig(new EditorConfig(String.format("%s/onlyoffice/callback?acm_ticket=%s", arkcaseBaseUrl, authTicket)));
        }
        EditorConfig editorConfig = config.getEditorConfig();
        editorConfig.setMode("edit");// FIXME hardcoded
        editorConfig.setLang("en-US");// FIXME hardcoded

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

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setArkcaseBaseUrl(String arkcaseBaseUrl)
    {
        this.arkcaseBaseUrl = arkcaseBaseUrl;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public void setDocumentServerUrlApi(String documentServerUrlApi)
    {
        this.documentServerUrlApi = documentServerUrlApi;
    }

    @Override
    public String getDocumentServerUrlApi()
    {
        return documentServerUrlApi;
    }

    @Override
    public String getArkcaseBaseUrl()
    {
        return arkcaseBaseUrl;
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }

    public void setDocumentTypeResolver(DocumentTypeResolver documentTypeResolver)
    {
        this.documentTypeResolver = documentTypeResolver;
    }

    public Boolean getPluginEnabled()
    {
        return pluginEnabled;
    }

    public void setPluginEnabled(Boolean pluginEnabled)
    {
        this.pluginEnabled = pluginEnabled;
    }

    @Override
    public boolean isEnabled()
    {
        return pluginEnabled;
    }

    @Override
    public String getName()
    {
        return ONLY_OFFICE_PLUGIN;
    }
}
