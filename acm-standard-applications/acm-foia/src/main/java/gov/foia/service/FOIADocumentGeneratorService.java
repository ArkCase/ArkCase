package gov.foia.service;

import static gov.foia.model.FOIARequestUtils.extractRequestorAddress;
import static gov.foia.model.FOIARequestUtils.extractRequestorEmailAddress;
import static gov.foia.model.FOIARequestUtils.extractRequestorName;

import com.armedia.acm.spring.SpringContextHolder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIAObject;
import gov.foia.model.FOIAPerson;
import gov.foia.model.FOIARequest;
import gov.foia.model.FOIADocumentDescriptor;

public class FOIADocumentGeneratorService
{
    private final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private SpringContextHolder contextHolder;

    public FOIADocumentDescriptor getDocumentDescriptor(FOIAObject foiaObject, String reqAck)
    {
        return contextHolder.getAllBeansOfType(FOIADocumentDescriptor.class).values().stream()
                .filter(documentDescriptor -> documentDescriptor.getReqAck().equals(reqAck)
                        && documentDescriptor.getType().equals(((FOIARequest) foiaObject).getRequestType()))
                .findFirst().get();
    }

    public Map<String, String> getReportSubstitutions(FOIARequest request)
    {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentDate", LocalDate.now().format(datePattern));
        parameters.put("requestID", request.getCaseNumber().toString());

        FOIAPerson person = (FOIAPerson) request.getOriginator().getPerson();

        parameters.put("requestorName", extractRequestorName(person));
        parameters.put("requestorPosition", person.getPosition());
        parameters.put("requestorEmailAddress", extractRequestorEmailAddress(person));

        parameters.put("requestType", request.getRequestType());
        parameters.put("category", request.getRequestCategory());
        parameters.put("deliveryMethodOfResponse", request.getDeliveryMethodOfResponse());

        parameters.put("requestorAddress", extractRequestorAddress(person));

        parameters.put("subject", request.getTitle());

        if (request.getRequestType().equals(FOIAConstants.NEW_REQUEST_TYPE))
        {

            parameters.put("recordDateFrom",
                    request.getRecordSearchDateFrom() != null ? request.getRecordSearchDateFrom().format(datePattern) : null);
            parameters.put("recordDateTo",
                    request.getRecordSearchDateTo() != null ? request.getRecordSearchDateTo().format(datePattern) : null);

            parameters.put("processingFeeWaiveUpTo", Double.toString(request.getProcessingFeeWaive()));
            parameters.put("feeWaiver",
                    request.getFeeWaiverFlag() != null ? request.getFeeWaiverFlag().toString() : Boolean.FALSE.toString());
            parameters.put("feeWaiverReason", request.getRequestFeeWaiveReason());

            parameters.put("payFee", request.getPayFee());

            parameters.put("expedite", request.getExpediteFlag() != null ? request.getExpediteFlag().toString() : Boolean.FALSE.toString());
            parameters.put("expediteReason", request.getRequestExpediteReason());

            parameters.put("receivedDate", request.getReceivedDate() != null ? request.getReceivedDate().format(datePattern) : null);
        }
        else
        {
            parameters.put("originalRequestNumber", request.getReferences().get(0).getTargetName());
        }

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
