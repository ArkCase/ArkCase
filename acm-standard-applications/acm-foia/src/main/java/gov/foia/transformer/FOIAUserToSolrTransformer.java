package gov.foia.transformer;

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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.HIDDEN_B;
import static com.armedia.acm.services.users.model.ldap.MapperUtils.prefixTrailingDot;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.service.ldap.UserToSolrTransformer;
import com.armedia.acm.spring.SpringContextHolder;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by ana.serafimoska
 */
public class FOIAUserToSolrTransformer extends UserToSolrTransformer
{
    @Value("${portal.serviceProvider.directory.name}")
    private String directoryName;

    private SpringContextHolder acmContextHolder;

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmUser in)
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));

        String userPrefix = prefixTrailingDot(ldapSyncConfig.getUserPrefix());

        SolrAdvancedSearchDocument solr;

        solr = super.toSolrAdvancedSearch(in);

        if (solr != null)
        {
            // set hidden_b to true if user has portal prefix
            if (solr.getObject_id_s().startsWith(userPrefix))
            {
                solr.setAdditionalProperty(HIDDEN_B, true);
            }
        }
        return solr;
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
