package com.armedia.acm.services.notification.service.provider;

import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.provider.model.DocumentLinkedModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentLinkedTemplateModelProvider implements TemplateModelProvider<DocumentLinkedModel>
{

    private AuthenticationTokenService authenticationTokenService;

    @Override
    public DocumentLinkedModel getModel(Object object)
    {
        Notification notification = (Notification) object;
        List<EcmFileVersion> fileVersions = notification.getFiles();
        List<String> links = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        if (fileVersions.size() > 0)
        {
            fileVersions = fileVersions.stream()
                    .filter(filversion -> filversion.getVersionTag().equals(filversion.getFile().getActiveVersionTag()))
                    .collect(Collectors.toList());
            for (int i = 0; i < fileVersions.size(); i++)
            {
                fileNames.add(fileVersions.get(i).getFile().getFileName() + fileVersions.get(i).getFile().getFileActiveVersionNameExtension());
                links.add("ecmFileId=" + fileVersions.get(i).getFile().getFileId() + "&version=&acm_email_ticket="
                        + authenticationTokenService.generateAndSaveAuthenticationToken(fileVersions.get(i).getId(),
                                notification.getEmailAddresses(), null));
            }
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
}
