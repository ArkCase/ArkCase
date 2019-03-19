package com.armedia.acm.plugins.complaint.web.api;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping({ "/api/v1/plugin/complaint", "/api/latest/plugin/complaint" })
public class GetComplaintListOfValuesAPIController
{

    private LookupDao lookupDao;

    @RequestMapping(value = "priorities", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_XML_VALUE })
    public @ResponseBody List<String> getComplaintPriorities()
    {
        List<StandardLookupEntry> lookupEntries = (List<StandardLookupEntry>) getLookupDao().getLookupByName("priorities").getEntries();
        return lookupEntries.stream().map(StandardLookupEntry::getKey).collect(Collectors.toList());
    }

    @RequestMapping(value = "types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<String> getComplaintTypes()
    {
        List<StandardLookupEntry> lookupEntries = (List<StandardLookupEntry>) getLookupDao().getLookupByName("complaintTypes").getEntries();
        return lookupEntries.stream().map(StandardLookupEntry::getKey).collect(Collectors.toList());
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }
}
