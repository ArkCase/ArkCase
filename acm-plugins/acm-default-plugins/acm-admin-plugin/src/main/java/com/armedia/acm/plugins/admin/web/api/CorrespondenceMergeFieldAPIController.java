package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.service.CorrespondenceService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class CorrespondenceMergeFieldAPIController
{

    private CorrespondenceService correspondenceService;

    @RequestMapping(value = "/mergefields", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceMergeField> getMergeFields() throws IOException
    {
        return correspondenceService.getMergeFields();
    }

    @RequestMapping(value = "/mergefields/active/{objectType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceMergeField> getActiveVersionMergeFieldsByType(@PathVariable(value = "objectType") String objectType)
            throws IOException
    {
        return correspondenceService.getActiveVersionMergeFieldsByType(objectType);
    }

    @RequestMapping(value = "/mergefields", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceMergeField> saveMergeFieldsData(@RequestBody List<CorrespondenceMergeField> mergeFields, Authentication auth)
            throws IOException
    {
        return correspondenceService.saveMergeFieldsData(mergeFields, auth);
    }

    /**
     * @param correspondenceService
     *            the correspondenceService to set
     */
    public void setCorrespondenceService(CorrespondenceService correspondenceService)
    {
        this.correspondenceService = correspondenceService;
    }

}
