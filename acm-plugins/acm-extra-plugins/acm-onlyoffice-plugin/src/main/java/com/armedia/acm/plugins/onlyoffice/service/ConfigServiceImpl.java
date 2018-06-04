package com.armedia.acm.plugins.onlyoffice.service;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.onlyoffice.exceptions.UnsupportedExtension;
import com.armedia.acm.plugins.onlyoffice.model.DocumentTypeResolver;
import com.armedia.acm.plugins.onlyoffice.model.config.*;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.text.SimpleDateFormat;

public class ConfigServiceImpl implements ConfigService
{
    private Logger logger = LoggerFactory.getLogger(getClass());
    private EcmFileDao ecmFileDao;
    private UserDao userDao;
    private AuthenticationTokenService authenticationTokenService;
    private ArkPermissionEvaluator arkPermissionEvaluator;
    private DocumentTypeResolver documentTypeResolver;

    private String documentServerUrlApi;
    private String arkcaseBaseUrl;

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
            boolean reviewPermission = arkPermissionEvaluator.hasPermission(authentication, ecmFile.getFileId(), "FILE", "review");
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
}
