package com.armedia.acm.plugins.person.web.api;

/*-
 * #%L
 * ACM Default Plugin: Person
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.person.dao.OrganizationAssociationDao;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.plugins.person.service.OrganizationAssociationEventPublisher;

import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;

@Controller
@RequestMapping({ "/api/v1/plugin/organizationAssociation", "/api/latest/plugin/organizationAssociation" })
public class DeleteOrganizationAssociationAPIController
{
    private OrganizationAssociationDao organizationAssociationDao;
    private OrganizationAssociationEventPublisher organizationAssociationEventPublisher;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/delete/{organizationAssocId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteOrganizationById(
            @PathVariable("organizationAssocId") Long organizationAssocId) throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        log.info("Finding organization association by id:'{}'", organizationAssocId);

        if (organizationAssocId != null)
        {
            try
            {
                JSONObject objectToReturnJSON = new JSONObject();
                OrganizationAssociation source = getOrganizationAssociationDao().find(organizationAssocId);
                getOrganizationAssociationDao().deleteOrganizationAssociationById(organizationAssocId);
                log.info("Deleting organization association by id:'{}'", organizationAssocId);

                getOrganizationAssociationEventPublisher().publishOrganizationAssociationDeletedEvent(source);

                objectToReturnJSON.put("deletedOrganizationAssociationId", organizationAssocId);
                return objectToReturnJSON.toString();
            }
            catch (PersistenceException e)
            {
                throw new AcmUserActionFailedException("Delete", "organizationAssoc", organizationAssocId, e.getMessage(), e);
            }
        }

        throw new AcmObjectNotFoundException("couldn't find", organizationAssocId, "organization association with this id", null);
    }

    public OrganizationAssociationDao getOrganizationAssociationDao()
    {
        return organizationAssociationDao;
    }

    public void setOrganizationAssociationDao(OrganizationAssociationDao organizationAssociationDao)
    {
        this.organizationAssociationDao = organizationAssociationDao;
    }

    public OrganizationAssociationEventPublisher getOrganizationAssociationEventPublisher()
    {
        return organizationAssociationEventPublisher;
    }

    public void setOrganizationAssociationEventPublisher(OrganizationAssociationEventPublisher organizationAssociationEventPublisher)
    {
        this.organizationAssociationEventPublisher = organizationAssociationEventPublisher;
    }

}
