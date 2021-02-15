package gov.foia.model.provider;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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
import com.armedia.acm.plugins.person.service.PersonAssociationService;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.exemption.exception.GetExemptionCodeException;
import com.armedia.acm.services.exemption.model.ExemptionCode;
import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import gov.foia.dao.FOIAExemptionCodeDao;
import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;
import gov.foia.model.FOIATaskRequestModel;
import gov.foia.service.FOIAExemptionService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FOIATaskRequestTemplateModelProvider implements TemplateModelProvider<FOIATaskRequestModel>
{

    private FOIARequestDao foiaRequestDao;
    private ApplicationConfig applicationConfig;
    private UserDao userDao;
    private UserOrgService userOrgService;
    private PersonAssociationService personAssociationService;
    private NoteDao noteDao;
    private TaskDao taskDao;
    private FOIAExemptionService foiaExemptionService;

    @Override
    public FOIATaskRequestModel getModel(Object object)
    {
        AcmTask task = null;
        if(object instanceof Notification)
        {
            task = getTaskDao().findById(((Notification) object).getParentId());
        }
        else
        {
            task = (AcmTask) object;
        }
        FOIATaskRequestModel model = new FOIATaskRequestModel();
        List<String> exemptionCodesAndDescription = new ArrayList<>();
        if(task.getParentObjectId() != null)
        {
            FOIARequest request = foiaRequestDao.find(task.getParentObjectId());
            if(request != null)
            {
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
                model.setRequest(request);

                List<ExemptionCode> exemptionCodes = null;
                try
                {
                    exemptionCodes = foiaExemptionService.getExemptionCodes(request.getId(), request.getObjectType());
                    if(exemptionCodes != null)
                    {
                        for (ExemptionCode exCode : exemptionCodes)
                        {
                            exemptionCodesAndDescription.add(exCode.getExemptionCode());
                        }
                    }
                }
                catch (GetExemptionCodeException e)
                {
                    e.printStackTrace();
                }

            }
        }

        task.setTaskNotes(noteDao.listNotes("GENERAL", task.getId(), task.getObjectType()).stream().map(note -> note.getNote()).collect(Collectors.joining("\n\n")));
        model.setTask(task);
        model.setTaskContact(getPersonAssociationService().getPersonsInAssociatonsByPersonType("TASK", task.getId(), "Contact Person").stream().findFirst().orElse(null));
        model.setExemptionCodesAndDescription(exemptionCodesAndDescription);

        return model;
    }

    @Override
    public Class<FOIATaskRequestModel> getType()
    {
        return FOIATaskRequestModel.class;
    }

    public FOIARequestDao getFoiaRequestDao()
    {
        return foiaRequestDao;
    }

    public void setFoiaRequestDao(FOIARequestDao foiaRequestDao)
    {
        this.foiaRequestDao = foiaRequestDao;
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

    public PersonAssociationService getPersonAssociationService() {
        return personAssociationService;
    }

    public void setPersonAssociationService(PersonAssociationService personAssociationService) {
        this.personAssociationService = personAssociationService;
    }

    public NoteDao getNoteDao() {
        return noteDao;
    }

    public void setNoteDao(NoteDao noteDao) {
        this.noteDao = noteDao;
    }

    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public FOIAExemptionService getFoiaExemptionService()
    {
        return foiaExemptionService;
    }

    public void setFoiaExemptionService(FOIAExemptionService foiaExemptionService)
    {
        this.foiaExemptionService = foiaExemptionService;
    }
}

