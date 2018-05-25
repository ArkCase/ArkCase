package com.armedia.acm.plugins.onlyoffice.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.onlyoffice.model.CallBackData;
import com.armedia.acm.plugins.onlyoffice.model.CallbackResponse;
import com.armedia.acm.plugins.onlyoffice.model.StatusConstants;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.dao.UserDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class CallbackServiceImpl implements CallbackService
{
    private Logger logger = LoggerFactory.getLogger(getClass());
    private EcmFileDao ecmFileDao;
    private EcmFileService ecmFileService;
    private UserDao userDao;
    private String arkcaseBaseUrl;
    private AuthenticationTokenService authenticationTokenService;
    private RestTemplate restTemplate;

    @Override
    public CallbackResponse handleCallback(CallBackData callBackData, Authentication authentication)
    {
        Objects.nonNull(callBackData);
        logger.debug("handle callback data [{}]", callBackData);
        switch (callBackData.getStatus())
        {
        case StatusConstants.NO_DOCUMENT_WITH_ID_FOUND:
            return handleNoDocumentWithIdFound(callBackData);
        case StatusConstants.BEING_EDITED:
            return handleBeingEdited(callBackData);
        case StatusConstants.READY_FOR_SAVING:
            return handleReadyForSaving(callBackData, authentication);
        case StatusConstants.SAVING_ERROR_OCCURED:
            return handleSavingErrorOccured(callBackData);
        case StatusConstants.CLOSED_NO_CHANGES:
            return handleClosedNoChanges(callBackData);
        case StatusConstants.EDITED_BUT_ALREADY_SAVED:
            return handleEditedButAlreadySaved(callBackData);
        case StatusConstants.ERROR_WHILE_SAVING:
            return handleErrorWhileSaving(callBackData);
        default:
            return handleUnknownStatusProvided(callBackData);
        }
    }

    private CallbackResponse handleUnknownStatusProvided(CallBackData callBackData)
    {
        logger.debug("handleUnknownStatusProvided.");
        return new CallbackResponse();
    }

    private CallbackResponse handleErrorWhileSaving(CallBackData callBackData)
    {
        logger.debug("handleErrorWhileSaving.");
        return new CallbackResponse();
    }

    private CallbackResponse handleEditedButAlreadySaved(CallBackData callBackData)
    {
        logger.debug("handleEditedButAlreadySaved.");
        return new CallbackResponse();
    }

    private CallbackResponse handleClosedNoChanges(CallBackData callBackData)
    {
        // TODO release lock
        logger.debug("handleClosedNoChanges.");
        return new CallbackResponse();
    }

    private CallbackResponse handleSavingErrorOccured(CallBackData callBackData)
    {
        logger.debug("handleSavingErrorOccured.");
        return new CallbackResponse();
    }

    private CallbackResponse handleReadyForSaving(CallBackData callBackData, Authentication authentication)
    {
        logger.debug("handleReadyForSaving.");
        try
        {
            URL url = new URL(callBackData.getUrl());
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == 200)
            {
                InputStream stream = connection.getInputStream();
                String key = callBackData.getKey();
                Long fileId = Long.parseLong(key.substring(0, key.indexOf("-")));
                EcmFile ecmFile = ecmFileDao.find(fileId);
                ecmFileService.update(ecmFile, stream, authentication);
                logger.debug("Document with key [{}] successfully saved to Arkcase.", key);
                // TODO release lock
            }
            else
            {
                // TODO release lock?
                logger.error("File not saved. Got response status [{}]", connection.getResponseCode());
            }
        }
        catch (IOException | AcmCreateObjectFailedException e)
        {
            logger.error("Error saving document. Reason: [{}]", e.getMessage());
            return new CallbackResponse(10);
        }
        return new CallbackResponse();
    }

    private CallbackResponse handleBeingEdited(CallBackData callBackData)
    {
        // this method is being called when user opens document for editing
        // can't think of any other handling except for logging.
        logger.debug("handleBeingEdited.");
        return new CallbackResponse();
    }

    private CallbackResponse handleNoDocumentWithIdFound(CallBackData callBackData)
    {
        logger.error("Document with wrong id[{}] provided, editing is not possible.", callBackData.getKey());
        return new CallbackResponse(1);
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
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

    public void setRestTemplate(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }
}
