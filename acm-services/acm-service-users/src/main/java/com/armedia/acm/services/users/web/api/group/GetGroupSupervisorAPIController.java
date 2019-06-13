/**
 *
 */
package com.armedia.acm.services.users.web.api.group;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;

import org.json.JSONObject;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author riste.tutureski
 */
@Controller
@RequestMapping({ "/api/v1/users", "/api/latest/users" })
public class GetGroupSupervisorAPIController
{

    private Logger LOG = LogManager.getLogger(getClass());

    private AcmGroupDao groupDao;
    private MuleContextManager muleContextManager;

    @RequestMapping(value = "/group/{groupId}/get/supervisor", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGroupSupervisors(@PathVariable("groupId") String groupId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws Exception
    {

        groupId = new String(Base64.getUrlDecoder().decode(groupId.getBytes()));
        LOG.info("Taking group supervisor from Solr for group ID = {}", groupId);

        String groupString = getGroup(groupId, 0, 1, "", auth);

        if (groupString != null)
        {
            JSONObject groupJson = new JSONObject(groupString);

            if (groupJson != null && groupJson.getJSONObject("response") != null &&
                    groupJson.getJSONObject("response").getJSONArray("docs") != null &&
                    groupJson.getJSONObject("response").getJSONArray("docs").length() == 1)
            {
                JSONObject doc = groupJson.getJSONObject("response").getJSONArray("docs").getJSONObject(0);
                JSONObject supervisorId = null;
                try
                {
                    supervisorId = doc.getJSONObject("supervisor_id_s");
                }
                catch (Exception e)
                {
                    throw new IllegalStateException("There are no supervisor for group with ID = " + groupId);
                }

                return getSupervisor(groupId, supervisorId, startRow, maxRows, sort, auth);

            }
        }

        throw new IllegalStateException("Cannot retrieve supervisors for group with ID = " + groupId);
    }

    private String getGroup(String groupId, int startRow, int maxRows, String sort, Authentication auth) throws MuleException
    {
        String query = "object_id_s:" + groupId
                + " AND object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        if (LOG.isDebugEnabled())
        {
            LOG.debug("User '" + auth.getName() + "' is searching for '" + query + "'");
        }

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", auth);

        MuleMessage response = getMuleContextManager().send("vm://advancedSearchQuery.in", "", headers);

        LOG.debug("Response type: " + response.getPayload().getClass());

        if (response.getPayload() instanceof String)
        {
            String responsePayload = (String) response.getPayload();

            return responsePayload;
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
    }

    private String getSupervisor(String groupId, JSONObject supervisorId, int startRow, int maxRows, String sort, Authentication auth)
            throws MuleException
    {
        if (supervisorId != null)
        {
            String query = "object_id_s:" + supervisorId.toString() + " AND object_type_s:USER AND status_lcs:VALID";

            if (LOG.isDebugEnabled())
            {
                LOG.debug("User '" + auth.getName() + "' is searching for '" + query + "'");
            }

            Map<String, Object> headers = new HashMap<>();
            headers.put("query", query);
            headers.put("firstRow", startRow);
            headers.put("maxRows", maxRows);
            headers.put("sort", sort);
            headers.put("acmUser", auth);

            MuleMessage response = getMuleContextManager().send("vm://advancedSearchQuery.in", "", headers);

            LOG.debug("Response type: " + response.getPayload().getClass());

            if (response.getPayload() instanceof String)
            {
                String responsePayload = (String) response.getPayload();

                return responsePayload;
            }

            throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
        }

        throw new IllegalStateException("There are no any supervisors for group with ID = " + groupId);
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }
}
