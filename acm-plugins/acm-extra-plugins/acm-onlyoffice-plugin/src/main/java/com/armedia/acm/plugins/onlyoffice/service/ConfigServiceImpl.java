package com.armedia.acm.plugins.onlyoffice.service;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.onlyoffice.model.config.Config;
import com.armedia.acm.plugins.onlyoffice.model.config.Document;
import com.armedia.acm.plugins.onlyoffice.model.config.DocumentInfo;
import com.armedia.acm.plugins.onlyoffice.model.config.DocumentPermissions;
import com.armedia.acm.plugins.onlyoffice.model.config.EditorConfig;
import com.armedia.acm.plugins.onlyoffice.model.config.EditorCustomization;
import com.armedia.acm.plugins.onlyoffice.model.config.GoBack;
import com.armedia.acm.plugins.onlyoffice.model.config.User;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
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
    private String arkcaseBaseUrl;
    private AuthenticationTokenService authenticationTokenService;

    @Override
    public Config getConfig(Long fileId, Authentication auth)
    {
        Config config = new Config();
        config.setHeight("100%");
        config.setWidth("100%");
        config.setDocumentType("text");
        config.setType("desktop");
        EcmFile ecmFile = ecmFileDao.find(fileId);
        String userId = auth.getName();
        AcmUser user = userDao.findByUserId(userId);

        String authTicket = authenticationTokenService.getTokenForAuthentication(auth);
        setDocumentConfig(config, ecmFile, user, authTicket);

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
            document.setPermissions(new DocumentPermissions());
        }
        DocumentPermissions permissions = document.getPermissions();
        // TODO instead of hardcoding them, we need to evaluate them from the actual user
        permissions.setEdit(true);
        permissions.setComment(true);
        permissions.setDownload(true);
        permissions.setPrint(true);
        permissions.setReview(true);

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
        GoBack goBack = new GoBack();
        goBack.setUrl(arkcaseBaseUrl);
        goBack.setBlank(true);
        customization.setGoback(goBack);

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
}
