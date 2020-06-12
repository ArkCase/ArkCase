package com.armedia.acm.services.email.service;

/*-
 * #%L
 * ACM Service: Email
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

import com.armedia.acm.core.ObjectLabelConfig;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.MessageBodyFactory;

import org.springframework.security.core.Authentication;

/**
 * Created by manoj.dhungana on 7/14/2017.
 */
public class AcmEmailContentGeneratorService
{
    private AuthenticationTokenService authenticationTokenService;
    private AuthenticationTokenDao authenticationTokenDao;
    private TemplatingEngine templatingEngine;
    private EcmFileDao ecmFileDao;
    private ObjectLabelConfig objectLabelConfig;

    public String generateEmailBody(EmailWithEmbeddedLinksDTO emailDTO, String emailAddress, Authentication authentication)
    {
        MessageBodyFactory messageBodyFactory = new MessageBodyFactory(emailDTO.getTemplate());
        messageBodyFactory.setTemplatingEngine(getTemplatingEngine());
        messageBodyFactory.setModelReferenceName(emailDTO.getModelReferenceName());
        messageBodyFactory.setParentNumber(emailDTO.getObjectNumber());
        String parentTypeLabel = objectLabelConfig.getLabelForObjectType(emailDTO.getObjectType());
        messageBodyFactory.setParentType(parentTypeLabel);
        messageBodyFactory.addPropertyToModel("fileIds", emailDTO.getFileIds());
        messageBodyFactory.addPropertyToModel("fileNames", emailDTO.getFileNames());
        messageBodyFactory.addPropertyToModel("fileVersion", emailDTO.getFileVersion());
        messageBodyFactory.addPropertyToModel("tokens", emailDTO.getTokens());
        messageBodyFactory.addPropertyToModel("body", emailDTO.getBody());

        StringBuilder body = new StringBuilder();
        body.append(emailDTO.getBody() != null ? emailDTO.getBody() : "").append("<br/>");

        if (emailDTO.getFileIds() != null)
        {
            for (Long fileId : emailDTO.getFileIds())
            {
                EcmFile ecmFile = getEcmFileDao().find(fileId);
                String token = authenticationTokenService.generateAndSaveAuthenticationToken(fileId, emailAddress, authentication);
                body.append(fileId).append("&version=").append(ecmFile.getActiveVersionTag()).append("&acm_email_ticket=").append(token)
                        .append("<br/>");
                emailDTO.getTokens().add(token);
                emailDTO.setFileVersion(ecmFile.getActiveVersionTag());
            }
        }
        return messageBodyFactory.buildMessageBodyFromTemplate(body.toString(), emailDTO.getHeader(), emailDTO.getFooter());
    }

    public String generateEmailBody(EmailWithAttachmentsDTO in, String template)
    {
        MessageBodyFactory messageBodyFactory = new MessageBodyFactory(template);
        messageBodyFactory.setTemplatingEngine(getTemplatingEngine());
        messageBodyFactory.setParentNumber(in.getObjectNumber());
        String parentTypeLabel = objectLabelConfig.getLabelForObjectType(in.getObjectType());
        messageBodyFactory.setParentType(parentTypeLabel);
        return messageBodyFactory.buildMessageBodyFromTemplate(in.getBody(), in.getHeader(), in.getFooter());
    }

    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

    public AuthenticationTokenDao getAuthenticationTokenDao()
    {
        return authenticationTokenDao;
    }

    public void setAuthenticationTokenDao(AuthenticationTokenDao authenticationTokenDao)
    {
        this.authenticationTokenDao = authenticationTokenDao;
    }

    public TemplatingEngine getTemplatingEngine()
    {
        return templatingEngine;
    }

    public void setTemplatingEngine(TemplatingEngine templatingEngine)
    {
        this.templatingEngine = templatingEngine;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public ObjectLabelConfig getObjectLabelConfig()
    {
        return objectLabelConfig;
    }

    public void setObjectLabelConfig(ObjectLabelConfig objectLabelConfig)
    {
        this.objectLabelConfig = objectLabelConfig;
    }
}
