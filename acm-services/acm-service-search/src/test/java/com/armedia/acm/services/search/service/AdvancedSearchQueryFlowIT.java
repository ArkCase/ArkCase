package com.armedia.acm.services.search.service;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-search-service-test-mule.xml"
})
public class AdvancedSearchQueryFlowIT
{
    @Autowired
    private MuleClient muleClient;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void largeRequest() throws Exception
    {
        String query = "gratefuldead&fq=%7B%21frange+l%3D1%7Dsum%28if%28exists%28protected_object_b%29%2C+0%2C+1%29%2C+if" +
                "%28protected_object_b%2C+0%2C+1%29%2C+if%28public_doc_b%2C+1%2C+0%29%2C+termfreq%28allow_acl_ss%2C+" +
                "%27ann-acm%27%29%2C+termfreq%28allow_acl_ss%2C+%27ROLE_ADMINISTRATOR%27%29%2C+termfreq%28allow_acl_ss" +
                "%2C+%27ACM_ADMINISTRATOR_DEV%27%29%29&fq=-deny_acl_ss%3Aann-acm+AND+-deny_acl_ss%3AROLE_ADMINISTRATOR+" +
                "AND+-deny_acl_ss%3AACM_ADMINISTRATOR_DEV&start=0&rows=500&wt=json&sort=&indent=true&facet=true&" +
                "facet.field=%7B%21key%3D%27Create+User%27%7Dcreator_lcs&facet.field=%7B%21key%3D%27City%27" +
                "%7Dlocation_city_lcs&facet.query=%7B%21key%3D%27Create+Date%2C+Previous+Week%27%7Dcreate_date_tdt:" +
                "%5BNOW%2FDAY-7DAY+TO+*%5D&facet.query=%7B%21key%3D%27Create+Date%2C+Previous+Month%27%7D" +
                "create_date_tdt:%5BNOW%2FDAY-1MONTH+TO+*%5D&facet.query=%7B%21key%3D%27Create+Date%2C+Previous+Year" +
                "%27%7Dcreate_date_tdt:%5BNOW%2FDAY-1YEAR+TO+*%5D&facet.query=%7B%21key%3D%27Due_Date%2C+Previous+Week" +
                "%27%7DdueDate_tdt:%5BNOW%2FDAY-7DAY+TO+*%5D&facet.query=%7B%21key%3D%27Due_Date%2C+Previous+Month" +
                "%27%7DdueDate_tdt:%5BNOW%2FDAY-1MONTH+TO+*%5D&facet.query=%7B%21key%3D%27Due_Date%2C+Previous+Year" +
                "%27%7DdueDate_tdt:%5BNOW%2FDAY-1YEAR+TO+*%5D&facet.field=%7B%21key%3D%27Task+Status%27%7Dstatus_s&" +
                "facet.field=%7B%21key%3D%27Person%2COrganization+Type%27%7Dtype_lcs&facet.field=%7B%21key%3D%27" +
                "Object+Type%27%7Dobject_type_s&facet.field=%7B%21key%3D%27Assignee+Full+Name%27%7D" +
                "assignee_full_name_lcs&facet.field=%7B%21key%3D%27Priority%27%7Dpriority_lcs&facet.field=%7B%21key" +
                "%3D%27Postal+Code%27%7Dlocation_postal_code_sdo&facet.field=%7B%21key%3D%27Task+Priority%27%7D" +
                "priority_s&facet.field=%7B%21key%3D%27Incident+Date%27%7Dincident_date_tdt&facet.field=%7B%21key%3D%27" +
                "State%27%7Dlocation_state_lcs&facet.field=%7B%21key%3D%27Parent+Type%27%7Dparent_type_s&facet.field=" +
                "%7B%21key%3D%27Parent+Task+Type%27%7Dparent_object_type_s&facet.field=%7B%21key%3D%27Modify+User%27%7" +
                "Dmodifier_lcs&facet.query=%7B%21key%3D%27Modify+Date%2C+Previous+Week%27%7Dmodified_date_tdt:%5BNOW" +
                "%2FDAY-7DAY+TO+*%5D&facet.query=%7B%21key%3D%27Modify+Date%2C+Previous+Month%27%7Dmodified_date_tdt:" +
                "%5BNOW%2FDAY-1MONTH+TO+*%5D&facet.query=%7B%21key%3D%27Modify+Date%2C+Previous+Year%27%7D" +
                "modified_date_tdt:%5BNOW%2FDAY-1YEAR+TO+*%5D&facet.field=%7B%21key%3D%27Incident+Type%27%7D" +
                "incident_type_lcs&facet.field=%7B%21key%3D%27Status%27%7Dstatus_lcs";

//        String query = "Grateful Dead";

        log.debug("query length: " + query.length());

        Authentication authentication = new UsernamePasswordAuthenticationToken("jerry", "garcia");

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", 0);
        headers.put("maxRows", 10);
        headers.put("sort", "object_type_s asc");
        headers.put("acmUser", authentication);

        MuleMessage response = muleClient.send("vm://advancedSearchQuery.in", "", headers);

        assertTrue(response.getPayload() != null && response.getPayload() instanceof String);

        assertNull(response.getExceptionPayload());

        log.debug("response: " + response.getPayloadAsString());


    }
}
