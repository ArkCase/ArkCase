package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.person.dao.OrganizationAssociationDao;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.plugins.person.service.OrganizationAssociationEventPublisher;
import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;

@Controller
@RequestMapping({"/api/v1/plugin/organizationAssociation", "/api/latest/plugin/organizationAssociation"})
public class DeleteOrganizationAssociationAPIController
{
    private OrganizationAssociationDao organizationAssociationDao;
    private OrganizationAssociationEventPublisher organizationAssociationEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/delete/{organizationAssocId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteOrganizationById(
            @PathVariable("organizationAssocId") Long organizationAssocId
    ) throws AcmObjectNotFoundException, AcmUserActionFailedException
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
            } catch (PersistenceException e)
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
