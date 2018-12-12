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

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.CaseFileToSolrTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import gov.foia.model.FOIARequest;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 23, 2016
 */
public class FOIARequestToSolrTransformer extends CaseFileToSolrTransformer
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return FOIARequest.class.equals(acmObjectType);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(CaseFile in)
    {
        SolrAdvancedSearchDocument solr = null;

        if (in instanceof FOIARequest)
        {
            FOIARequest requestIn = (FOIARequest) in;
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
    public SolrDocument toSolrQuickSearch(CaseFile in)
    {
        SolrDocument solr = null;

        if (in instanceof FOIARequest)
        {
            FOIARequest requestIn = (FOIARequest) in;
            solr = super.toSolrQuickSearch(requestIn);

            if (solr != null)
            {
                mapRequestProperties(requestIn, solr.getAdditionalProperties());
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
        return FOIARequest.class;
    }

    /**
     * @param requestIn
     * @param additionalProperties
     */
    protected void mapRequestProperties(FOIARequest requestIn, Map<String, Object> additionalProperties)
    {
        additionalProperties.put("object_sub_type_s", "FOIA_REQUEST");

        additionalProperties.put("received_date_tdt", solrDateFromLocalDateTime(requestIn.getReceivedDate()));
        additionalProperties.put("final_reply_date_tdt", solrDateFromLocalDate(requestIn.getFinalReplyDate()));
        additionalProperties.put("scanned_date_tdt", solrDateFromLocalDate(requestIn.getScannedDate()));
        additionalProperties.put("expedite_flag_b", requestIn.getExpediteFlag());
        additionalProperties.put("fee_waiver_flag_b", requestIn.getFeeWaiverFlag());
        additionalProperties.put("litigation_flag_b", requestIn.getLitigationFlag());
        additionalProperties.put("public_flag_b", requestIn.getPublicFlag());
        additionalProperties.put("request_type_lcs", requestIn.getRequestType());
        additionalProperties.put("request_sub_type_lcs", requestIn.getRequestSubType());
        additionalProperties.put("request_category_lcs", requestIn.getRequestCategory());
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
            additionalProperties.put("requester_name_s", requestIn.getOriginator().getPerson().getFullName());
        }
        else
        {
            additionalProperties.put("requester_name_s", "");
        }

        additionalProperties.put("queue_enter_date_tdt", solrDateFromLocalDate(requestIn.getQueueEnterDate()));

        additionalProperties.put("record_search_date_from_tdt", solrDateFromLocalDateTime(requestIn.getRecordSearchDateFrom()));
        additionalProperties.put("record_search_date_to_tdt", solrDateFromLocalDateTime(requestIn.getRecordSearchDateTo()));
        additionalProperties.put("request_fee_waive_reason_s", requestIn.getRequestFeeWaiveReason());
        additionalProperties.put("request_expedite_reason_s", requestIn.getRequestExpediteReason());
        additionalProperties.put("pay_fee_s", requestIn.getPayFee());
        additionalProperties.put("processing_fee_waive_s", Double.toString(requestIn.getProcessingFeeWaive()));
    }

    private Date solrDateFromLocalDate(LocalDate localDate)
    {
        return localDate == null ? null : Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date solrDateFromLocalDateTime(LocalDateTime localDateTime)
    {
        return localDateTime == null ? null : Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
