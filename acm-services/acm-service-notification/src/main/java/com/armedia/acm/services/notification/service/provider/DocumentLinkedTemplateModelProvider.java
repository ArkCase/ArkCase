package com.armedia.acm.services.notification.service.provider;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.core.model.ApplicationConfig;
import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.provider.model.DocumentLinkedModel;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

public class DocumentLinkedTemplateModelProvider implements TemplateModelProvider<DocumentLinkedModel>
{

    private AuthenticationTokenService authenticationTokenService;
    private ApplicationConfig applicationConfig;

    @Value("${tokenExpiration.fileLinks}")
    private Long tokenExpiry;

    @Override
    public DocumentLinkedModel getModel(Object object)
    {
        Notification notification = (Notification) object;
        List<EcmFileVersion> fileVersions = notification.getFiles();
        List<String> links = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        if (fileVersions != null)
        {
            String token = authenticationTokenService.getUncachedTokenForAuthentication(null);
            String requestUrl = applicationConfig.getBaseUrl() + "/api/latest/plugin/open/downloadFromEmail?";
            String relativePath = "";
            for (int i = 0; i < fileVersions.size(); i++)
            {
                if (fileVersions.get(i).getVersionTag().equals(fileVersions.get(i).getFile().getActiveVersionTag()))
                {
                    fileNames.add(fileVersions.get(i).getFile().getFileName() + fileVersions.get(i).getFile().getFileActiveVersionNameExtension());
                    String url = "ecmFileId=" + fileVersions.get(i).getFile().getFileId() + "&version=&acm_email_ticket=" + token;
                    links.add(url);
                    relativePath += requestUrl + url + "__comma__";
                }
            }
            authenticationTokenService.addTokenToRelativePaths(Arrays.asList(relativePath.split("__comma__")), token, tokenExpiry, notification.getEmailAddresses());
        }
        return new DocumentLinkedModel(links, fileNames, notification.getRelatedObjectType(), notification.getRelatedObjectNumber());
    }

    @Override
    public Class<DocumentLinkedModel> getType()
    {
        return DocumentLinkedModel.class;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

    public ApplicationConfig getApplicationConfig()
    {
        return applicationConfig;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig)
    {
        this.applicationConfig = applicationConfig;
    }
}
