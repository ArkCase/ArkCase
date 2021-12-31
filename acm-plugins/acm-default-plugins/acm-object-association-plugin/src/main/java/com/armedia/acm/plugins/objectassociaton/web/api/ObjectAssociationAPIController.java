package com.armedia.acm.plugins.objectassociaton.web.api;

/*-
 * #%L
 * ACM Default Plugin: Object Associations
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
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.service.AcmObjectAssociationException;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author nebojsha.davidovikj
 */

@RestController
@RequestMapping({ "/api/v1/service/objectassociations", "/api/latest/service/objectassociations" })
public class ObjectAssociationAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private ObjectAssociationService objectAssociationService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getObjectAssociations(Authentication auth, @RequestParam(value = "parent-id") Long parentId,
            @RequestParam(value = "parent-type", required = false) String parentType,
            @RequestParam(value = "target-type", required = false) String targetType,
            @RequestParam(value = "order-by", required = false) String orderBy,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n) throws AcmObjectNotFoundException
    {
        return objectAssociationService.getAssociations(auth, parentId, parentType, targetType, orderBy, start, n);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteAssociation(@PathVariable Long id, Authentication auth) throws AcmUserActionFailedException
    {
        log.debug("delete Object Association [{}]", id);
        objectAssociationService.deleteAssociation(id, auth);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ObjectAssociation saveAssociation(@RequestBody ObjectAssociation objectAssociation, Authentication auth)
            throws AcmObjectAssociationException
    {
        log.debug("save Object Association [{}]", objectAssociation);
        Objects.requireNonNull(objectAssociation, "objectAssociation must not be null!");
        Objects.requireNonNull(objectAssociation.getParentId(), "objectAssociation parentId must not be null!");
        Objects.requireNonNull(objectAssociation.getParentType(), "objectAssociation parentType must not be null!");

        Objects.requireNonNull(objectAssociation.getTargetId(), "objectAssociation targetId must not be null!");
        Objects.requireNonNull(objectAssociation.getTargetType(), "objectAssociation targetType must not be null!");

        Objects.requireNonNull(objectAssociation.getAssociationType(), "objectAssociation associationType must not be null!");

        return objectAssociationService.saveAssociation(objectAssociation, auth);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/batch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ObjectAssociation> saveAssociations(@RequestBody List<ObjectAssociation> objectAssociations, Authentication auth)
            throws AcmObjectAssociationException
    {
        log.debug("save [{}] Object Association", objectAssociations.size());
        Objects.requireNonNull(objectAssociations, "objectAssociations must not be null!");
        List<ObjectAssociation> retval = new ArrayList<>();
        for (ObjectAssociation objectAssociation : objectAssociations)
        {
            Objects.requireNonNull(objectAssociation.getParentId(), "objectAssociation parentId must not be null!");
            Objects.requireNonNull(objectAssociation.getParentType(), "objectAssociation parentType must not be null!");

            Objects.requireNonNull(objectAssociation.getTargetId(), "objectAssociation targetId must not be null!");
            Objects.requireNonNull(objectAssociation.getTargetType(), "objectAssociation targetType must not be null!");

            Objects.requireNonNull(objectAssociation.getAssociationType(), "objectAssociation associationType must not be null!");
            retval.add(objectAssociationService.saveAssociation(objectAssociation, auth));
        }
        return retval;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ObjectAssociation getAssociation(@PathVariable Long id, Authentication auth) throws AcmUserActionFailedException
    {
        log.debug("get Object Association [{}]", id);
        return objectAssociationService.getAssociation(id, auth);
    }

    public ObjectAssociationService getObjectAssociationService()
    {
        return objectAssociationService;
    }

    public void setObjectAssociationService(ObjectAssociationService objectAssociationService)
    {
        this.objectAssociationService = objectAssociationService;
    }

}
