package gov.foia.transformer;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileToSolrTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;

import gov.foia.model.FOIAEcmFileVersion;
import gov.foia.model.FOIAFile;

public class FOIAFileToSolrTransformer extends EcmFileToSolrTransformer
{

    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return FOIAFile.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return FOIAFile.class;
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(EcmFile in)
    {
        SolrAdvancedSearchDocument solr = null;

        if (in instanceof FOIAFile)
        {
            FOIAFile foiaFile = (FOIAFile) in;
            solr = super.toSolrAdvancedSearch(foiaFile);

            if (solr != null)
            {
                mapRequestProperties(foiaFile, solr.getAdditionalProperties());
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
    public SolrDocument toSolrQuickSearch(EcmFile in)
    {
        SolrDocument solr = null;

        if (in instanceof FOIAFile)
        {
            FOIAFile foiaFile = (FOIAFile) in;
            solr = super.toSolrQuickSearch(foiaFile);

            if (solr != null)
            {
                mapRequestProperties(foiaFile, solr.getAdditionalProperties());
            }
            return solr;
        }
        else
        {
            log.error("Could not send to quick search class name {}!.", in.getClass().getName());
        }

        throw new RuntimeException("Could not send to quick search class name " + in.getClass().getName() + "!.");

    }

    @Override
    public SolrContentDocument toContentFileIndex(EcmFile in)
    {
        SolrContentDocument solr = null;

        if (in instanceof FOIAFile)
        {
            FOIAFile foiaFile = (FOIAFile) in;
            solr = super.toContentFileIndex(foiaFile);

            if (solr != null)
            {
                mapRequestProperties(foiaFile, solr.getAdditionalProperties());
            }
            return solr;
        }
        else
        {
            log.error("Could not send to content file index class name {}!.", in.getClass().getName());
        }

        throw new RuntimeException("Could not send to content file index class name " + in.getClass().getName() + "!.");
    }

    private void mapRequestProperties(FOIAFile file, Map<String, Object> additionalProperties)
    {
        additionalProperties.put("public_flag_b", file.getPublicFlag());
        if (file.getMadePublicDate() != null)
        {
            additionalProperties.put("made_public_date_tdt", file.getMadePublicDate());
        }
        Optional<FOIAEcmFileVersion> activeFileVersion = file.getVersions()
                .stream()
                .filter(ecmFileVersion -> ecmFileVersion.getVersionTag().equals(file.getActiveVersionTag()))
                .map(it -> (FOIAEcmFileVersion) it)
                .findFirst();
        additionalProperties.put("redaction_status_s", activeFileVersion.map(FOIAEcmFileVersion::getRedactionStatus).orElse(null));
        additionalProperties.put("review_status_s", activeFileVersion.map(FOIAEcmFileVersion::getReviewStatus).orElse(null));
    }
}
