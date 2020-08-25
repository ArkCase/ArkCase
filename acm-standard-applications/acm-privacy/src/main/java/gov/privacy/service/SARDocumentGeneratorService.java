package gov.privacy.service;

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

import static gov.privacy.model.SARUtils.extractRequestorAddress;
import static gov.privacy.model.SARUtils.extractRequestorEmailAddress;
import static gov.privacy.model.SARUtils.extractRequestorName;

import com.armedia.acm.spring.SpringContextHolder;

import javax.ws.rs.NotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import gov.privacy.model.SARDocumentDescriptor;
import gov.privacy.model.SARObject;
import gov.privacy.model.SARPerson;
import gov.privacy.model.SubjectAccessRequest;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SARDocumentGeneratorService
{
    private final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private SpringContextHolder contextHolder;

    public SARDocumentDescriptor getDocumentDescriptor(SARObject SARObject, String reqAck)
    {
        Optional<SARDocumentDescriptor> optional = contextHolder.getAllBeansOfType(SARDocumentDescriptor.class).values().stream()
                .filter(documentDescriptor -> documentDescriptor.getReqAck().equals(reqAck)
                        && documentDescriptor.getType().equals(((SubjectAccessRequest) SARObject).getRequestType()))
                .findFirst();
        if (optional.isPresent())
        {
            return optional.get();
        }
        throw new NotFoundException("Document Descriptor Not Found");

    }

    public Map<String, String> getReportSubstitutions(SubjectAccessRequest request)
    {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentDate", LocalDate.now().format(datePattern));
        parameters.put("requestID", request.getCaseNumber().toString());

        SARPerson person = (SARPerson) request.getOriginator().getPerson();

        parameters.put("requestorName", extractRequestorName(person));
        parameters.put("requestorPosition", person.getPosition());
        parameters.put("requestorEmailAddress", extractRequestorEmailAddress(person));

        parameters.put("requestType", request.getRequestType());

        parameters.put("requestorAddress", extractRequestorAddress(person));

        parameters.put("subject", request.getTitle());

        parameters.put("receivedDate", request.getReceivedDate() != null ? request.getReceivedDate().format(datePattern) : null);

        return parameters;
    }

    public SpringContextHolder getContextHolder()
    {
        return contextHolder;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }
}
