package com.armedia.acm.services.search.service;

/*-
 * #%L
 * ACM Service: Search
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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-search-service-test-mule.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-object-converter.xml" })
public class AdvancedSearchQueryFlowIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
    }

    private transient final Logger log = LogManager.getLogger(getClass());
    @Autowired
    private MuleContextManager muleContextManager;

    @Test
    public void largeRequest() throws Exception
    {
        String query = "COMPLAINT&fq=%7B%21frange+l%3D1%7Dsum%28if%28exists%28protected_object_b%29%2C+0%2C+1%29%2C+if"
                + "%28protected_object_b%2C+0%2C+1%29%2C+if%28public_doc_b%2C+1%2C+0%29%2C+termfreq%28allow_user_ls%2C+"
                + "%27100%27%29%2C+termfreq%28allow_group_ls%2C+%27100%27%29%2C+termfreq%28allow_group_ls"
                + "%2C+%27200%27%29%29&fq=-deny_user_ls%3A100+AND+-deny_group_ls%3A100+"
                + "AND+-deny_group_ls%3A200&start=0&rows=500&wt=json&sort=&indent=true&facet=true&"
                + "facet.field=%7B%21key%3D%27Create+User%27%7Dcreator_lcs&facet.field=%7B%21key%3D%27City%27"
                + "%7Dlocation_city_lcs&facet.query=%7B%21key%3D%27Create+Date%2C+Previous+Week%27%7Dcreate_date_tdt:"
                + "%5BNOW%2FDAY-7DAY+TO+*%5D&facet.query=%7B%21key%3D%27Create+Date%2C+Previous+Month%27%7D"
                + "create_date_tdt:%5BNOW%2FDAY-1MONTH+TO+*%5D&facet.query=%7B%21key%3D%27Create+Date%2C+Previous+Year"
                + "%27%7Dcreate_date_tdt:%5BNOW%2FDAY-1YEAR+TO+*%5D&facet.query=%7B%21key%3D%27Due+Date%2C+Previous+Week"
                + "%27%7DdueDate_tdt:%5BNOW%2FDAY-7DAY+TO+*%5D&facet.query=%7B%21key%3D%27Due+Date%2C+Previous+Month"
                + "%27%7DdueDate_tdt:%5BNOW%2FDAY-1MONTH+TO+*%5D&facet.query=%7B%21key%3D%27Due+Date%2C+Previous+Year"
                + "%27%7DdueDate_tdt:%5BNOW%2FDAY-1YEAR+TO+*%5D&facet.field=%7B%21key%3D%27Task+Status%27%7Dstatus_s&"
                + "facet.field=%7B%21key%3D%27Person%2COrganization+Type%27%7Dtype_lcs&facet.field=%7B%21key%3D%27"
                + "Object+Type%27%7Dobject_type_s&facet.field=%7B%21key%3D%27Assignee+Full+Name%27%7D"
                + "assignee_full_name_lcs&facet.field=%7B%21key%3D%27Priority%27%7Dpriority_lcs&facet.field=%7B%21key"
                + "%3D%27Postal+Code%27%7Dlocation_postal_code_sdo&facet.field=%7B%21key%3D%27Task+Priority%27%7D"
                + "priority_s&facet.field=%7B%21key%3D%27Incident+Date%27%7Dincident_date_tdt&facet.field=%7B%21key%3D%27"
                + "State%27%7Dlocation_state_lcs&facet.field=%7B%21key%3D%27Parent+Type%27%7Dparent_type_s&facet.field="
                + "%7B%21key%3D%27Parent+Task+Type%27%7Dparent_object_type_s&facet.field=%7B%21key%3D%27Modify+User%27%7"
                + "Dmodifier_lcs&facet.query=%7B%21key%3D%27Modify+Date%2C+Previous+Week%27%7Dmodified_date_tdt:%5BNOW"
                + "%2FDAY-7DAY+TO+*%5D&facet.query=%7B%21key%3D%27Modify+Date%2C+Previous+Month%27%7Dmodified_date_tdt:"
                + "%5BNOW%2FDAY-1MONTH+TO+*%5D&facet.query=%7B%21key%3D%27Modify+Date%2C+Previous+Year%27%7D"
                + "modified_date_tdt:%5BNOW%2FDAY-1YEAR+TO+*%5D&facet.field=%7B%21key%3D%27Incident+Type%27%7D"
                + "incident_type_lcs&facet.field=%7B%21key%3D%27Status%27%7Dstatus_lcs";

        // String query = "Grateful Dead";

        log.debug("query length: " + query.length());

        Long authenticatedUserId = 100L;
        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", 0);
        headers.put("maxRows", 10);
        headers.put("sort", "object_type_s asc");
        headers.put("acmUser", authenticatedUserId);
        headers.put("acmUserGroupIds", Arrays.asList(100L, 200L));

        MuleMessage response = muleContextManager.send("vm://advancedSearchQuery.in", "", headers);

        assertTrue(response.getPayload() != null && response.getPayload() instanceof String);

        assertNull(response.getExceptionPayload());

        log.debug("response: " + response.getPayloadAsString());

        JSONObject json = new JSONObject(response.getPayloadAsString());
        JSONObject jsonResponse = json.getJSONObject("response");
        int numFound = jsonResponse.getInt("numFound");
        assertTrue(numFound >= 0);

        log.debug("num found: " + numFound);

    }

}
