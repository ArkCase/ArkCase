package gov.privacy.transformer;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileToSolrTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;

import gov.privacy.model.SAREcmFileVersion;
import gov.privacy.model.SARFile;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SARFileToSolrTransformer extends EcmFileToSolrTransformer
{

    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return SARFile.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return SARFile.class;
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(EcmFile in)
    {
        SolrAdvancedSearchDocument solr = null;

        if (in instanceof SARFile)
        {
            SARFile SARFile = (SARFile) in;
            solr = super.toSolrAdvancedSearch(SARFile);

            if (solr != null)
            {
                mapRequestProperties(SARFile, solr.getAdditionalProperties());
            }
            return solr;
        }
        else
        {
            log.error("Could not send to advanced search class name {}!.", in.getClass().getName());
        }

        throw new RuntimeException("Could not send to advanced search class name " + in.getClass().getName() + "!.");
    }

    @Override
    public SolrContentDocument toContentFileIndex(EcmFile in)
    {
        SolrContentDocument solr = null;

        if (in instanceof SARFile)
        {
            SARFile SARFile = (SARFile) in;
            solr = super.toContentFileIndex(SARFile);

            if (solr != null)
            {
                mapRequestProperties(SARFile, solr.getAdditionalProperties());
            }
            return solr;
        }
        else
        {
            log.error("Could not send to content file index class name {}!.", in.getClass().getName());
        }

        throw new RuntimeException("Could not send to content file index class name " + in.getClass().getName() + "!.");
    }

    private void mapRequestProperties(SARFile file, Map<String, Object> additionalProperties)
    {
        additionalProperties.put("public_flag_b", file.getPublicFlag());

        Optional<SAREcmFileVersion> activeFileVersion = file.getVersions()
                .stream()
                .filter(ecmFileVersion -> ecmFileVersion.getVersionTag().equals(file.getActiveVersionTag()))
                .map(it -> (SAREcmFileVersion) it)
                .findFirst();
        additionalProperties.put("redaction_status_s", activeFileVersion.map(SAREcmFileVersion::getRedactionStatus).orElse(null));
        additionalProperties.put("review_status_s", activeFileVersion.map(SAREcmFileVersion::getReviewStatus).orElse(null));
    }
}
