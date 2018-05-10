package com.armedia.acm.plugins.task.web.api;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import static org.junit.Assert.assertEquals;

import com.armedia.acm.plugins.task.model.AcmTaskSolr;
import com.armedia.acm.plugins.task.model.SolrResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class AcmTaskSolrTest
{
    @Before
    public void setUp() throws Exception
    {
    }

    @Test
    public void jsonPayload() throws Exception
    {
        // there are docs
        String jsonPayload = "{\"responseHeader\":{\"status\":0,\"QTime\":3,\"params\":{\"indent\":\"true\",\"q\":\"*:*\",\"_\":\"1411423765300\",\"wt\":\"json\"}},\"response\":{\"numFound\":2,\"start\":0,\"docs\":[{\"status_s\":\"CREATE\",\"create_dt\":\"2014-08-27T16:04:13Z\",\"title_t\":\"Approve the complaint; it will become part of a new case file.\",\"object_id_s\":\"123\",\"owner_s\":\"sally-acm\",\"deny_acl_ss\":[\"TEST-DENY-ACL\"],\"allow_acl_ss\":[\"TEST-ALLOW-ACL\"],\"object_type_s\":\"TASK\",\"id\":\"123-TASK\",\"assignee_s\":\"sally-acm\",\"parent_object_type_s\":\"COMPLAINT\",\"name\":\"Approve Complaint 'Test complaint for report'\",\"priority_i\":50,\"parent_object_id_i\":0,\"_version_\":1477621687748919300},{\"status_s\":\"CREATE\",\"create_dt\":\"2014-08-27T16:04:31Z\",\"title_t\":\"Approve the complaint; it will become part of a new case file.\",\"object_id_s\":\"147\",\"owner_s\":\"sally-acm\",\"deny_acl_ss\":[\"TEST-DENY-ACL\"],\"allow_acl_ss\":[\"TEST-ALLOW-ACL\"],\"object_type_s\":\"TASK\",\"id\":\"147-TASK\",\"assignee_s\":\"sally-acm\",\"parent_object_type_s\":\"COMPLAINT\",\"name\":\"Approve Complaint 'Test complaint for report'\",\"priority_i\":50,\"parent_object_id_i\":0,\"_version_\":1477621706484875300},]}}";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SolrResponse solrResponse = gson.fromJson(jsonPayload, SolrResponse.class);

        List<AcmTaskSolr> solrDocs = solrResponse.getResponse().getDocs();

        assertEquals("123", solrDocs.get(0).getObject_id_s());
        assertEquals("147", solrDocs.get(1).getObject_id_s());

    }

}
