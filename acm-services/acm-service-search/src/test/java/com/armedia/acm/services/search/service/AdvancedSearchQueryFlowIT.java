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

import static org.junit.Assert.assertTrue;

import com.armedia.acm.services.search.model.AcmAuthenticationMock;
import com.armedia.acm.services.search.model.AcmGrantedAuthorityMock;
import com.armedia.acm.services.search.model.AcmGrantedGroupAuthorityMock;
import com.armedia.acm.services.search.model.solr.SolrCore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-search-service-test-solrj.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-object-converter.xml" })
public class AdvancedSearchQueryFlowIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    private Logger log = LogManager.getLogger(getClass());

    @Autowired
    private ExecuteSolrQuery executeSolrQuery;

    private AcmAuthenticationMock authentication;

    @Test
    public void largeRequest() throws Exception
    {
        String query = "%22Test*%22+AND+-object_type_s%3ANOTIFICATION+AND+-object_type_s%3ASUBSCRIPTION_EVENT" +
                "+AND+-object_type_s%3ATAG+AND+-object_type_s%3AREFERENCE+AND+hidden_b%3Afalse";

        log.debug("query length: " + query.length());

        Set<AcmGrantedAuthorityMock> authsFromProvider = new HashSet<>(Arrays.asList(
                new AcmGrantedGroupAuthorityMock("LDAP_INVESTIGATOR", 100L),
                new AcmGrantedGroupAuthorityMock("LDAP_INVESTIGATOR", 200L)));

        authentication = new AcmAuthenticationMock(authsFromProvider, null, null,
                true, "acmUser", 100L);

        String response = executeSolrQuery.getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, 0, 500,
                "object_type_s asc");

        assertTrue(response != null);

        log.debug("response: " + response);

        JSONObject json = new JSONObject(response);
        JSONObject jsonResponse = json.getJSONObject("response");
        int numFound = jsonResponse.getInt("numFound");
        assertTrue(numFound >= 0);

        log.debug("num found: " + numFound);
    }
}
