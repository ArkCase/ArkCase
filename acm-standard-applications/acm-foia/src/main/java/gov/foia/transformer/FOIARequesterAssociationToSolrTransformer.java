package gov.foia.transformer;

import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonAssociationToSolrTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import gov.foia.model.FOIARequesterAssociation;

public class FOIARequesterAssociationToSolrTransformer extends PersonAssociationToSolrTransformer
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return FOIARequesterAssociation.class.equals(acmObjectType);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(PersonAssociation in)
    {
        SolrAdvancedSearchDocument solr = null;

        if (in instanceof FOIARequesterAssociation)
        {
            FOIARequesterAssociation requesterAssociationIn = (FOIARequesterAssociation) in;
            solr = super.toSolrAdvancedSearch(requesterAssociationIn);

            if (solr != null)
            {
                mapRequestProperties(requesterAssociationIn, solr.getAdditionalProperties());
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
    public SolrDocument toSolrQuickSearch(PersonAssociation in)
    {
        SolrDocument solr = null;

        if (in instanceof FOIARequesterAssociation)
        {
            FOIARequesterAssociation requesterAssociationIn = (FOIARequesterAssociation) in;
            solr = super.toSolrQuickSearch(requesterAssociationIn);

            if (solr != null)
            {
                mapRequestProperties(requesterAssociationIn, solr.getAdditionalProperties());
            }

            return solr;
        }
        else
        {
            log.error("Could not send to quick search class name {}!.", in.getClass().getName());
        }
        throw new RuntimeException("Could not send to advanced search class name " + in.getClass().getName() + "!.");
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return FOIARequesterAssociation.class;
    }

    private void mapRequestProperties(FOIARequesterAssociation requesterAssociationIn, Map<String, Object> additionalProperties)
    {
        additionalProperties.put("requester_source_s", requesterAssociationIn.getRequesterSource());
    }
}
