package com.armedia.acm.services.notification.service.provider;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmAssignee;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.notification.helper.UserInfoHelper;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.provider.model.AcmEntityTemplateModel;
import com.armedia.acm.services.objecttitle.model.TitleConfiguration;
import com.armedia.acm.services.objecttitle.service.ObjectTitleConfigurationService;

import java.util.Map;

public class AcmEntityTemplateNoUserPrefixModelProvider implements TemplateModelProvider<AcmEntityTemplateModel>
{
    private AcmDataService dataService;
    private UserInfoHelper userInfoHelper;
    private ObjectTitleConfigurationService objectTitleConfigurationService;

    @Override
    public AcmEntityTemplateModel getModel(Object object)
    {
        Notification notification = (Notification) object;
        AcmAbstractDao<AcmObject> dao = getDataService().getDaoByObjectType(notification.getParentType());
        AcmObject acmObject = dao.find(notification.getParentId());

        String baseAssigneeLdapId = "";
        String baseModifier = "";
        String baseAssigneeGroupId = "";
        String modifierEmail = "";
        String assigneeEmail = "";

        if (acmObject instanceof AcmAssignee && ((AcmAssignee) acmObject).getAssigneeLdapId() != null)
        {
            AcmAssignee acmAssignee = (AcmAssignee) acmObject;

            String assigneeLdapId = acmAssignee.getAssigneeLdapId();
            String modifier = acmAssignee.getModifier();

            baseAssigneeLdapId = getUserInfoHelper().removeUserPrefix(assigneeLdapId);
            baseModifier = getUserInfoHelper().removeUserPrefix(modifier);

            String assigneeLdapGroupId = acmAssignee.getAssigneeGroupId();
            baseAssigneeGroupId = getUserInfoHelper().removeGroupPrefix(assigneeLdapGroupId);

            modifierEmail = getUserInfoHelper().getUserEmail(modifier);
            assigneeEmail = getUserInfoHelper().getUserEmail(assigneeLdapId);

        }

        AcmEntityTemplateModel acmEntityTemplateModel = new AcmEntityTemplateModel();
        acmEntityTemplateModel.caseFileObject = acmObject;
        acmEntityTemplateModel.assigneeUserId = baseAssigneeLdapId;
        acmEntityTemplateModel.modifierUserId = baseModifier;
        acmEntityTemplateModel.assigneeGroupId = baseAssigneeGroupId;
        acmEntityTemplateModel.modifierEmail = modifierEmail;
        acmEntityTemplateModel.assigneeEmail = assigneeEmail;

        Boolean isTitleEnabled = false;
        Map<String, TitleConfiguration> titleConfigurationMap = objectTitleConfigurationService.getObjectTitleConfig();
        for (TitleConfiguration value : titleConfigurationMap.values())
        {
            isTitleEnabled = value.getEnableTitleField();

        }
        if(isTitleEnabled)
        {
            acmEntityTemplateModel.setTitleEnabled("Title enabled");
        }

        return acmEntityTemplateModel;
    }

    @Override
    public Class<AcmEntityTemplateModel> getType()
    {
        return AcmEntityTemplateModel.class;
    }

    public AcmDataService getDataService()
    {
        return dataService;
    }

    public void setDataService(AcmDataService dataService)
    {
        this.dataService = dataService;
    }

    public UserInfoHelper getUserInfoHelper()
    {
        return userInfoHelper;
    }

    public void setUserInfoHelper(UserInfoHelper userInfoHelper)
    {
        this.userInfoHelper = userInfoHelper;
    }

    public ObjectTitleConfigurationService getObjectTitleConfigurationService()
    {
        return objectTitleConfigurationService;
    }

    public void setObjectTitleConfigurationService(ObjectTitleConfigurationService objectTitleConfigurationService)
    {
        this.objectTitleConfigurationService = objectTitleConfigurationService;
    }
}
