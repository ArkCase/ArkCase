package gov.foia.service;

import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import javax.jms.JMSException;

import java.util.Date;

import gov.foia.broker.FOIARequestBrokerClient;
import gov.foia.model.PortalFOIARequestStatus;

/**
 * Foia Request update nofifier, Listens for case file update requests and sends notification messages to outbound queue
 *
 * @author dame.gjorgjievski
 */
public class FOIARequestUpdateNotifier implements ApplicationListener<CaseEvent>
{
    private transient Logger LOG = LoggerFactory.getLogger(getClass());

    private FOIARequestBrokerClient brokerClient;

    @Override
    public void onApplicationEvent(CaseEvent event)
    {
        if (CaseFileConstants.EVENT_TYPE_CREATED.equals(event.getEventType().toLowerCase())
                || CaseFileConstants.EVENT_TYPE_UPDATED.equals(event.getEventType().toLowerCase()))
        {
            // sendRequestStatusUpdate(event.getCaseFile());
        }
    }

    /**
     * Populate and send request status update
     *
     * @param caseFile
     */
    private void sendRequestStatusUpdate(CaseFile caseFile)
    {
        PortalFOIARequestStatus status = new PortalFOIARequestStatus();
        status.setRequestId(caseFile.getCaseNumber());
        status.setRequestStatus(caseFile.getStatus());
        status.setUpdateDate(new Date());
        Person person = caseFile.getPersonAssociations().stream().filter(pa -> "Requester".equals(pa.getPersonType())).findFirst()
                .map(PersonAssociation::getPerson).orElse(null);
        if (person != null)
        {
            status.setLastName(person.getFamilyName());

            // String phone = person.getContactMethods().stream().filter(cm -> "Phone".equals(cm.getType())).findFirst()
            // .map(ContactMethod::getValue).orElse(null);
            // status.setPhoneNumber(phone);

            /// String email = person.getContactMethods().stream().filter(cm ->
            /// "Email".equals(cm.getType())).findFirst()
            // .map(ContactMethod::getValue).orElse(null);
            // status.setEmail(email);
        }

        try
        {
            brokerClient.sendObject(status);
        }
        catch (JsonProcessingException | JMSException e)
        {
            LOG.error("Failed to send FOIA request status update", e);
        }
    }

    /**
     * Set broker client for sending request status updates
     *
     * @param brokerClient
     */
    public void setBrokerClient(FOIARequestBrokerClient brokerClient)
    {
        this.brokerClient = brokerClient;
    }

}
