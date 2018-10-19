package gov.foia.transformer;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileToSolrTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import gov.foia.model.FOIAFile;

public class FOIAFileToSolrTransformer extends EcmFileToSolrTransformer
{

    private final Logger log = LoggerFactory.getLogger(getClass());

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
    public SolrContentDocument toContentFileIndex(EcmFile in) {

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
    }
}
