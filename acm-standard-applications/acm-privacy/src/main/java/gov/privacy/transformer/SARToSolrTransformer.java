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

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.CaseFileToSolrTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import gov.privacy.model.SubjectAccessRequest;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SARToSolrTransformer extends CaseFileToSolrTransformer
{
    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return SubjectAccessRequest.class.equals(acmObjectType);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(CaseFile in)
    {
        SolrAdvancedSearchDocument solr = null;

        if (in instanceof SubjectAccessRequest)
        {
            SubjectAccessRequest requestIn = (SubjectAccessRequest) in;
            solr = super.toSolrAdvancedSearch(requestIn);

            if (solr != null)
            {
                mapRequestProperties(requestIn, solr.getAdditionalProperties());
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
    public Class<?> getAcmObjectTypeSupported()
    {
        return SubjectAccessRequest.class;
    }

    /**
     * @param requestIn
     * @param additionalProperties
     */
    protected void mapRequestProperties(SubjectAccessRequest requestIn, Map<String, Object> additionalProperties)
    {
        additionalProperties.put("object_sub_type_s", "SUBJECT_ACCESS_REQUEST");

        additionalProperties.put("received_date_tdt", requestIn.getReceivedDate());
        additionalProperties.put("request_type_lcs", requestIn.getRequestType());
        additionalProperties.put("return_reason_lcs", requestIn.getReturnReason());
        additionalProperties.put("request_source_lcs", requestIn.getRequestSource());
        additionalProperties.put("request_due_date_tdt", requestIn.getDueDate());
        additionalProperties.put("request_status_lcs", requestIn.getStatus());
        additionalProperties.put("request_object_type_lcs", requestIn.getObjectType());
        additionalProperties.put("request_id_lcs", requestIn.getId().toString());

        if (requestIn.getQueue() != null)
        {
            additionalProperties.put("queue_name_s", requestIn.getQueue().getName());
            additionalProperties.put("queue_id_s", requestIn.getQueue().getId().toString());
        }
        else
        {
            // queue is removed when the order process is complete, so we want to unset these fields
            additionalProperties.put("queue_name_s", "");
            additionalProperties.put("queue_id_s", "");
        }

        if (requestIn.getOriginator() != null && requestIn.getOriginator().getPerson() != null)
        {
            if(requestIn.getOriginator().getPerson().getFullName() != null)
            {
                additionalProperties.put("requester_name_s", requestIn.getOriginator().getPerson().getFullName());
            }
            else {
                additionalProperties.put("requester_name_s", "");
            }
            if(requestIn.getOriginator().getPerson().getDefaultEmail().getValue() != null)
            {
                additionalProperties.put("requester_email_s", requestIn.getOriginator().getPerson().getDefaultEmail().getValue());
            }
            else {
                additionalProperties.put("requester_email_s", requestIn.getOriginator().getPerson().getDefaultEmail().getValue());
            }
        }
        if (requestIn.getSubject() != null && requestIn.getSubject().getPerson() != null)
        {
            additionalProperties.put("subject_name_s", requestIn.getSubject().getPerson().getFullName());
        }
        else
        {
            additionalProperties.put("subject_name_s", "");
        }

        additionalProperties.put("queue_enter_date_tdt", requestIn.getQueueEnterDate());

        additionalProperties.put("request_agency_s", requestIn.getComponentAgency());
    }
}
