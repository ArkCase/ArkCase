package gov.privacy.model;

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

import static gov.privacy.model.SubjectAccessRequest.PURGE_HOLD_QUEUE;
import static gov.privacy.model.SubjectAccessRequest.REQUESTS_BY_STATUS;

import com.armedia.acm.data.converter.LocalDateConverter;
import com.armedia.acm.data.converter.LocalDateTimeConverter;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
@DiscriminatorValue("gov.privacy.model.SubjectAccessRequest")
@NamedQueries({
        @NamedQuery(name = PURGE_HOLD_QUEUE, query = "SELECT sar FROM SubjectAccessRequest sar, AcmQueue aq WHERE sar.queue.id = aq.id AND aq.name = :queueName AND sar.holdEnterDate < :holdEnterDate"),
        @NamedQuery(name = REQUESTS_BY_STATUS, query = "SELECT sar FROM SubjectAccessRequest sar WHERE UPPER(sar.status) IN :requestStatuses") })
public class SubjectAccessRequest extends CaseFile implements SARObject
{
    public static final String PURGE_HOLD_QUEUE = "SubjectAccessRequest.purgeHoldQueue";
    public static final String REQUESTS_BY_STATUS = "SubjectAccessRequest.requestsByStatus";
    private static final long serialVersionUID = -8883225846554730667L;
    @Column(name = "sar_received_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime receivedDate;

    @Column(name = "sar_hold_enter_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Convert(converter = LocalDateConverter.class)
    private LocalDate holdEnterDate;

    @Column(name = "sar_request_type")
    private String requestType;

    @Column(name = "sar_release_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime releasedDate;

    @Column(name = "sar_component_agency")
    private String componentAgency;

    @Column(name = "sar_return_reason")
    private String returnReason;

    @Column(name = "sar_request_source")
    private String requestSource;

    @Column(name = "sar_record_search_date_from")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime recordSearchDateFrom;

    @Column(name = "sar_record_search_date_to")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime recordSearchDateTo;

    @Column(name = "sar_request_category")
    private String requestCategory;

    @Column(name = "sar_delivery_method_of_response")
    private String deliveryMethodOfResponse;

    @Column(name = "sar_sworn_statement")
    private boolean swornStatement = false;

    @Column(name = "sar_understand_processing_requirement_statement")
    private boolean understandProcessingRequirementStatement = false;

    @Column(name = "sar_information_agreement_statement")
    private boolean informationAgreementStatement = false;

    @Column(name = "sar_accurate_and_authorized_statement")
    private boolean accurateAndAuthorizedStatement = false;

    @Column(name = "sar_signature")
    private String signature;

    @Column(name = "sar_signature_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Convert(converter = LocalDateConverter.class)
    private LocalDate signatureDate;

    @Transient
    private String originalRequestNumber;

    @Transient
    private SARConfiguration SARConfiguration;

    /**
     * @return the receivedDate
     */
    public LocalDateTime getReceivedDate()
    {
        return receivedDate;
    }

    /**
     * @param receivedDate
     *            the receivedDate to set
     */
    public void setReceivedDate(LocalDateTime receivedDate)
    {
        this.receivedDate = receivedDate;
    }

    /**
     * @return the holdEnterDate
     */
    public LocalDate getHoldEnterDate()
    {
        return holdEnterDate;
    }

    /**
     * @param holdEnterDate
     *            the holdEnterDate to set
     */
    public void setHoldEnterDate(LocalDate holdEnterDate)
    {
        this.holdEnterDate = holdEnterDate;
    }

    /**
     * @return the requestType
     */
    public String getRequestType()
    {
        return requestType;
    }

    /**
     * @param requestType
     *            the requestType to set
     */
    public void setRequestType(String requestType)
    {
        this.requestType = requestType;
    }

    public String getComponentAgency()
    {
        return componentAgency;
    }

    public void setComponentAgency(String componentAgency)
    {
        this.componentAgency = componentAgency;
    }

    /**
     * @return the returnReason
     */
    public String getReturnReason()
    {
        return returnReason;
    }

    /**
     * @param returnReason
     *            the returnReason to set
     */
    public void setReturnReason(String returnReason)
    {
        this.returnReason = returnReason;
    }

    /**
     * @return the requestSource
     */
    public String getRequestSource()
    {
        return requestSource;
    }

    /**
     * @param requestSource
     *            the requestSource to set
     */
    public void setRequestSource(String requestSource)
    {
        this.requestSource = requestSource;
    }



    /**
     * @return the originalRequestNumber
     */
    public String getOriginalRequestNumber()
    {
        return originalRequestNumber;
    }

    /**
     * @param originalRequestNumber
     *            the originalRequestNumber to set
     */
    public void setOriginalRequestNumber(String originalRequestNumber)
    {
        this.originalRequestNumber = originalRequestNumber;
    }

    public SARConfiguration getSARConfiguration()
    {
        return SARConfiguration;
    }

    public void setSARConfiguration(SARConfiguration SARConfiguration)
    {
        this.SARConfiguration = SARConfiguration;
    }

    public LocalDateTime getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(LocalDateTime releasedDate) {
        this.releasedDate = releasedDate;
    }

    public LocalDateTime getRecordSearchDateFrom()
    {
        return recordSearchDateFrom;
    }

    public void setRecordSearchDateFrom(LocalDateTime recordSearchDateFrom)
    {
        this.recordSearchDateFrom = recordSearchDateFrom;
    }

    public LocalDateTime getRecordSearchDateTo()
    {
        return recordSearchDateTo;
    }

    public void setRecordSearchDateTo(LocalDateTime recordSearchDateTo)
    {
        this.recordSearchDateTo = recordSearchDateTo;
    }

    public String getRequestCategory()
    {
        return requestCategory;
    }

    public void setRequestCategory(String requestCategory)
    {
        this.requestCategory = requestCategory;
    }

    public String getDeliveryMethodOfResponse()
    {
        return deliveryMethodOfResponse;
    }

    public void setDeliveryMethodOfResponse(String deliveryMethodOfResponse)
    {
        this.deliveryMethodOfResponse = deliveryMethodOfResponse;
    }

    public boolean isSwornStatement()
    {
        return swornStatement;
    }

    public void setSwornStatement(boolean swornStatement)
    {
        this.swornStatement = swornStatement;
    }

    public boolean isUnderstandProcessingRequirementStatement()
    {
        return understandProcessingRequirementStatement;
    }

    public void setUnderstandProcessingRequirementStatement(boolean understandProcessingRequirementStatement)
    {
        this.understandProcessingRequirementStatement = understandProcessingRequirementStatement;
    }

    public boolean isInformationAgreementStatement()
    {
        return informationAgreementStatement;
    }

    public void setInformationAgreementStatement(boolean informationAgreementStatement)
    {
        this.informationAgreementStatement = informationAgreementStatement;
    }

    public boolean isAccurateAndAuthorizedStatement()
    {
        return accurateAndAuthorizedStatement;
    }

    public void setAccurateAndAuthorizedStatement(boolean accurateAndAuthorizedStatement)
    {
        this.accurateAndAuthorizedStatement = accurateAndAuthorizedStatement;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public LocalDate getSignatureDate()
    {
        return signatureDate;
    }

    public void setSignatureDate(LocalDate signatureDate)
    {
        this.signatureDate = signatureDate;
    }

    public PersonAssociation getSubject()
    {
        if (getPersonAssociations() == null)
        {
            return null;
        }

        return getPersonAssociations().stream()
                .filter(personAssociation -> "Subject".equalsIgnoreCase(personAssociation.getPersonType()))
                .findFirst()
                .orElse(null);
    }

    public void setSubject(PersonAssociation subject)
    {
        if (subject != null)
        {

            Optional<PersonAssociation> found = getPersonAssociations().stream()
                    .filter(personAssociation -> "Subject".equalsIgnoreCase(personAssociation.getPersonType())).findFirst();

            if (!found.isPresent())
            {
                getPersonAssociations().add(subject);
            }
        }
    }


    @Override
    public PersonAssociation getOriginator()
    {
        if (getPersonAssociations() == null)
        {
            return null;
        }

        return getPersonAssociations().stream()
                .filter(personAssociation -> "Requester".equalsIgnoreCase(personAssociation.getPersonType()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void setOriginator(PersonAssociation originator)
    {
        if (getPersonAssociations() == null)
        {
            setPersonAssociations(new ArrayList<>());
        }

        if (originator != null)
        {

            Optional<PersonAssociation> found = getPersonAssociations().stream()
                    .filter(personAssociation -> "Requester".equalsIgnoreCase(personAssociation.getPersonType())).findFirst();

            if (!found.isPresent())
            {
                getPersonAssociations().add(originator);
            }
        }
    }

    @Override
    public String toString() {
        return "SubjectAccessRequest{" +
                "receivedDate=" + receivedDate +
                ", holdEnterDate=" + holdEnterDate +
                ", requestType='" + requestType + '\'' +
                ", releasedDate=" + releasedDate +
                ", componentAgency='" + componentAgency + '\'' +
                ", returnReason='" + returnReason + '\'' +
                ", requestSource='" + requestSource + '\'' +
                ", recordSearchDateFrom=" + recordSearchDateFrom +
                ", recordSearchDateTo=" + recordSearchDateTo +
                ", requestCategory='" + requestCategory + '\'' +
                ", deliveryMethodOfResponse='" + deliveryMethodOfResponse + '\'' +
                ", swornStatement=" + swornStatement +
                ", understandProcessingRequirementStatement=" + understandProcessingRequirementStatement +
                ", informationAgreementStatement=" + informationAgreementStatement +
                ", accurateAndAuthorizedStatement=" + accurateAndAuthorizedStatement +
                ", signature='" + signature + '\'' +
                ", signatureDate=" + signatureDate +
                ", originalRequestNumber='" + originalRequestNumber + '\'' +
                ", SARConfiguration=" + SARConfiguration +
                '}';
    }
}
