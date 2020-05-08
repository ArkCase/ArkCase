package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import static com.armedia.acm.services.users.model.ldap.MapperUtils.prefixTrailingDot;

import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.users.model.AcmUsersConstants;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.service.AcmUserServiceImpl;
import com.armedia.acm.spring.SpringContextHolder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

/**
 * Created by ana.serafimoska
 */
public class FOIAAcmUserServiceImpl extends AcmUserServiceImpl
{
    @Value("${foia.portalserviceprovider.directory.name}")
    private String directoryName;

    private SpringContextHolder acmContextHolder;

    @Override
    public String getNUsers(Authentication auth, String sortBy, String sortDirection, int startRow, int maxRows)
            throws SolrException
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));

        String userPrefix = prefixTrailingDot(ldapSyncConfig.getUserPrefix());
        String query = "object_type_s:USER AND status_lcs:VALID AND -object_id_s:" + userPrefix + "*";
        String filterSystemUsers = String.format("fq=-name:%s&fq=-name:%s", AcmUsersConstants.OCR_SYSTEM_USER,
                AcmUsersConstants.TRANSCRIBE_SYSTEM_USER);

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows,
                sortBy + " " + sortDirection, filterSystemUsers);
    }

    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

}