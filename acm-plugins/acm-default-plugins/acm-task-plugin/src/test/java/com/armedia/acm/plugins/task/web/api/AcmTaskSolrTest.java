package com.armedia.acm.plugins.task.web.api;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.armedia.acm.plugins.task.model.AcmTaskSolr;
import com.armedia.acm.plugins.task.model.SolrResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AcmTaskSolrTest {
    @Before
    public void setUp() throws Exception
    {
    }
    
    @Test
    public void jsonPayload() throws Exception {
        // there are docs
        String jsonPayload = "{\"responseHeader\":{\"status\":0,\"QTime\":3,\"params\":{\"indent\":\"true\",\"q\":\"*:*\",\"_\":\"1411423765300\",\"wt\":\"json\"}},\"response\":{\"numFound\":2,\"start\":0,\"docs\":[{\"status_s\":\"CREATE\",\"create_dt\":\"2014-08-27T16:04:13Z\",\"title_t\":\"Approve the complaint; it will become part of a new case file.\",\"object_id_s\":\"123\",\"owner_s\":\"sally-acm\",\"deny_acl_ss\":[\"TEST-DENY-ACL\"],\"allow_acl_ss\":[\"TEST-ALLOW-ACL\"],\"object_type_s\":\"TASK\",\"id\":\"123-TASK\",\"assignee_s\":\"sally-acm\",\"parent_object_type_s\":\"COMPLAINT\",\"name\":\"Approve Complaint 'Test complaint for report'\",\"priority_i\":50,\"parent_object_id_i\":0,\"_version_\":1477621687748919300},{\"status_s\":\"CREATE\",\"create_dt\":\"2014-08-27T16:04:31Z\",\"title_t\":\"Approve the complaint; it will become part of a new case file.\",\"object_id_s\":\"147\",\"owner_s\":\"sally-acm\",\"deny_acl_ss\":[\"TEST-DENY-ACL\"],\"allow_acl_ss\":[\"TEST-ALLOW-ACL\"],\"object_type_s\":\"TASK\",\"id\":\"147-TASK\",\"assignee_s\":\"sally-acm\",\"parent_object_type_s\":\"COMPLAINT\",\"name\":\"Approve Complaint 'Test complaint for report'\",\"priority_i\":50,\"parent_object_id_i\":0,\"_version_\":1477621706484875300},]}}";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SolrResponse solrResponse = gson.fromJson(jsonPayload, SolrResponse.class);
        
        List<AcmTaskSolr> solrDocs = solrResponse.getResponse().getDocs();
        
        assertEquals("123", solrDocs.get(0).getObject_id_s());
        assertEquals("147", solrDocs.get(1).getObject_id_s());

    }

}
