package gov.foia.model.provider;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.core.model.ApplicationConfig;
import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import com.armedia.acm.services.exemption.exception.GetExemptionCodeException;
import com.armedia.acm.services.exemption.model.ExemptionCode;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import gov.foia.model.FOIARequest;
import gov.foia.model.FOIARequestModel;
import gov.foia.service.FOIAExemptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author darko.dimitrievski
 */
public class FOIARequestTemplateModelProvider implements TemplateModelProvider<FOIARequestModel>
{
    
    private ObjectAssociationDao objectAssociationDao;
    private ApplicationConfig applicationConfig;
    private UserDao userDao;
    private UserOrgService userOrgService;
    private FOIAExemptionService foiaExemptionService;
    private FOIATemplateModelExemption foiaTemplateModelExemption;
    private transient final Logger LOG = LogManager.getLogger(getClass());

    @Override
    public FOIARequestModel getModel(Object foiaRequest)
    {
        FOIARequest request = (FOIARequest) foiaRequest;
        if(request.getRequestType().equals("Appeal"))
        {
            List<ObjectAssociation> objectAssociations = objectAssociationDao.findByParentTypeAndId(request.getObjectType(), request.getId());
            request.setOriginalRequestNumber(objectAssociations.get(0).getTargetName());
        }
        request.setApplicationConfig(applicationConfig);
        String assigneeLdapID = request.getAssigneeLdapId();
        AcmUser assignee = null;
        if(assigneeLdapID != null)
        {
            assignee = userDao.findByUserId(assigneeLdapID);
            UserOrg userOrg = userOrgService.getUserOrgForUserId(assigneeLdapID);
            if (userOrg != null)
            {
                request.setAssigneeTitle(userOrg.getTitle());
                request.setAssigneePhone(userOrg.getOfficePhoneNumber());
            }
            request.setAssigneeFullName(assignee.getFirstName() + " " + assignee.getLastName());
        }

        List<ExemptionCode> exemptionCodes;
        try
        {
            exemptionCodes = foiaExemptionService.getExemptionCodes(request.getId(), request.getObjectType());
        }
        catch (GetExemptionCodeException e)
        {
            LOG.warn("Failed to fetch exemption codes for object with type [{}] and id [{}]", request.getObjectType(), request.getId());
            exemptionCodes = new ArrayList<>();
        }

        FOIARequestModel requestModel = new FOIARequestModel();
        requestModel.setExemptionCodeSummary(getFoiaTemplateModelExemption().exemptionCodesAndSummary(exemptionCodes));
        requestModel.setExemptionCodesAndDescription(getFoiaTemplateModelExemption().exemptionCodesAndDescription(exemptionCodes));
        requestModel.setRequest(request);
        return requestModel;
    }

    @Override
    public Class<FOIARequestModel> getType()
    {
        return FOIARequestModel.class;
    }

    public ObjectAssociationDao getObjectAssociationDao() 
    {
        return objectAssociationDao;
    }

    public void setObjectAssociationDao(ObjectAssociationDao objectAssociationDao) 
    {
        this.objectAssociationDao = objectAssociationDao;
    }

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserOrgService getUserOrgService() {
        return userOrgService;
    }

    public void setUserOrgService(UserOrgService userOrgService) {
        this.userOrgService = userOrgService;
    }

    public FOIAExemptionService getFoiaExemptionService()
    {
        return foiaExemptionService;
    }

    public void setFoiaExemptionService(FOIAExemptionService foiaExemptionService)
    {
        this.foiaExemptionService = foiaExemptionService;
    }

    public FOIATemplateModelExemption getFoiaTemplateModelExemption()
    {
        return foiaTemplateModelExemption;
    }

    public void setFoiaTemplateModelExemption(FOIATemplateModelExemption foiaTemplateModelExemption)
    {
        this.foiaTemplateModelExemption = foiaTemplateModelExemption;
    }
}
