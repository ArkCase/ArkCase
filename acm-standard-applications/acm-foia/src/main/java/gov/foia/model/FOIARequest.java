package gov.foia.model;

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

import static gov.foia.model.FOIARequest.PURGE_BILLING_QUEUE;
import static gov.foia.model.FOIARequest.PURGE_HOLD_QUEUE;
import static gov.foia.model.FOIARequest.REQUESTS_BY_STATUS;

import com.armedia.acm.data.converter.BooleanToStringConverter;
import com.armedia.acm.data.converter.LocalDateConverter;
import com.armedia.acm.data.converter.LocalDateTimeConverter;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 21, 2016
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
@DiscriminatorValue("gov.foia.model.FOIARequest")
@NamedQueries({
        @NamedQuery(name = PURGE_BILLING_QUEUE, query = "SELECT fr FROM FOIARequest fr, AcmQueue aq WHERE fr.queue.id = aq.id AND aq.name = :queueName AND fr.billingEnterDate < :billingEnterDate"),
        @NamedQuery(name = PURGE_HOLD_QUEUE, query = "SELECT fr FROM FOIARequest fr, AcmQueue aq WHERE fr.queue.id = aq.id AND aq.name = :queueName AND fr.holdEnterDate < :holdEnterDate"),
        @NamedQuery(name = REQUESTS_BY_STATUS, query = "SELECT fr FROM FOIARequest fr WHERE UPPER(fr.status) IN :requestStatuses") })
public class FOIARequest extends CaseFile implements FOIAObject
{

    public static final String PURGE_BILLING_QUEUE = "FOIARequest.purgeBillingQueue";
    public static final String PURGE_HOLD_QUEUE = "FOIARequest.purgeHoldQueue";
    public static final String REQUESTS_BY_STATUS = "FOIARequest.requestsByStatus";
    private static final long serialVersionUID = -8883225846554730667L;
    @Column(name = "fo_received_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime receivedDate;

    @Column(name = "fo_final_reply_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Convert(converter = LocalDateConverter.class)
    private LocalDate finalReplyDate;

    @Column(name = "fo_scanned_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Convert(converter = LocalDateConverter.class)
    private LocalDate scannedDate;

    @Column(name = "fo_release_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime releasedDate;

    @Column(name = "fo_billing_enter_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime billingEnterDate;

    @Column(name = "fo_hold_enter_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime holdEnterDate;

    @Column(name = "fo_expedite_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean expediteFlag;

    @Column(name = "fo_amendment_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean amendmentFlag;

    @Column(name = "fo_fee_waiver_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean feeWaiverFlag;

    @Column(name = "fo_litigation_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean litigationFlag;

    @Column(name = "fo_request_type")
    private String requestType;

    @Column(name = "fo_request_sub_type")
    private String requestSubType;

    @Column(name = "fo_request_category")
    private String requestCategory;

    @Column(name = "fo_component_agency")
    private String componentAgency;

    @Column(name = "fo_return_reason")
    private String returnReason;

    @Column(name = "fo_request_source")
    private String requestSource;

    @Column(name = "fo_disposition_sub_type")
    private String dispositionSubtype;

    @Column(name = "fo_paid_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean paidFlag;

    @Column(name = "fo_public_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean publicFlag;

    @Column(name = "fo_delivery_method_of_response")
    private String deliveryMethodOfResponse;

    @Column(name = "fo_record_search_date_from")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime recordSearchDateFrom;

    @Column(name = "fo_record_search_date_to")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime recordSearchDateTo;

    @Column(name = "fo_disposition_closed_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime dispositionClosedDate;

    @Column(name = "fo_processing_fee_waive")
    private double processingFeeWaive;

    @Column(name = "fo_request_fee_waive_reason")
    private String requestFeeWaiveReason;

    @Column(name = "fo_pay_fee")
    private String payFee;

    @Column(name = "fo_request_expedite_reason")
    private String requestExpediteReason;

    @Column(name = "fo_request_amendment_details")
    private String requestAmendmentDetails;

    @Column(name = "fo_request_track")
    private String requestTrack;

    @Column(name = "fo_other_reason")
    private String otherReason;

    @Column(name = "fo_extension")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean extensionFlag;

    @Column(name = "fo_notification_group")
    private String notificationGroup;

    @Column(name = "fo_external_identifier")
    private String externalIdentifier;

    @Column(name = "fo_tolling_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean tollingFlag;

    @Column(name = "fo_limited_delivery_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private boolean limitedDeliveryFlag = false;

    @Column(name = "fo_generated_zip_flag")
    private Boolean generatedZipFlag;

    @Column(name = "fo_declared_as_record")
    private Boolean declaredAsRecord = false;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cm_case_id")
    private List<DispositionReason> dispositionReasons = new ArrayList<>();

    @Column(name = "fo_perfected_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime perfectedDate;

    @Column(name = "fo_ttc_on_last_redirection")
    private Integer ttcOnLastRedirection;

    @Column(name = "fo_withdraw_requested_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private boolean withdrawRequestedFlag = false;

    @Column(name = "fo_redirected_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime redirectedDate;

    @Column(name = "fo_expedite_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime expediteDate;

    @Column(name = "fo_feeWaived_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime feeWaivedDate;

    @Transient
    private String originalRequestNumber;

    @Transient
    private FoiaConfiguration foiaConfiguration;

    @Transient
    private String dispositionValue;

    @Column(name = "fo_difficulty_rating")
    private String difficultyRating;

    @Column(name="fo_portal_request_tracking_id")
    private String portalRequestTrackingId;

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
     * @return the finalReplyDate
     */
    public LocalDate getFinalReplyDate()
    {
        return finalReplyDate;
    }

    /**
     * @param finalReplyDate
     *            the finalReplyDate to set
     */
    public void setFinalReplyDate(LocalDate finalReplyDate)
    {
        this.finalReplyDate = finalReplyDate;
    }

    /**
     * @return the scannedDate
     */
    public LocalDate getScannedDate()
    {
        return scannedDate;
    }

    /**
     * @param scannedDate
     *            the scannedDate to set
     */
    public void setScannedDate(LocalDate scannedDate)
    {
        this.scannedDate = scannedDate;
    }

    /**
     * @return the releasedDate
     */
    public LocalDateTime getReleasedDate()
    {
        return releasedDate;
    }

    /**
     * @param releasedDate
     *            the releasedDate to set
     */
    public void setReleasedDate(LocalDateTime releasedDate)
    {
        this.releasedDate = releasedDate;
    }

    /**
     * @return the billingEnterDate
     */
    public LocalDateTime getBillingEnterDate()
    {
        return billingEnterDate;
    }

    /**
     * @param billingEnterDate
     *            the billingEnterDate to set
     */
    public void setBillingEnterDate(LocalDateTime billingEnterDate)
    {
        this.billingEnterDate = billingEnterDate;
    }

    /**
     * @return the holdEnterDate
     */
    public LocalDateTime getHoldEnterDate()
    {
        return holdEnterDate;
    }

    /**
     * @param holdEnterDate
     *            the holdEnterDate to set
     */
    public void setHoldEnterDate(LocalDateTime holdEnterDate)
    {
        this.holdEnterDate = holdEnterDate;
    }

    /**
     * @return the expediteFlag
     */
    public Boolean getExpediteFlag()
    {
        return expediteFlag;
    }

    /**
     * @param expediteFlag
     *            the expediteFlag to set
     */
    public void setExpediteFlag(Boolean expediteFlag)
    {
        this.expediteFlag = expediteFlag;
    }

    /**
     * @return the feeWaiverFlag
     */
    public Boolean getFeeWaiverFlag()
    {
        return feeWaiverFlag;
    }

    /**
     * @param feeWaiverFlag
     *            the feeWaiverFlag to set
     */
    public void setFeeWaiverFlag(Boolean feeWaiverFlag)
    {
        this.feeWaiverFlag = feeWaiverFlag;
    }

    /**
     * @return the litigationFlag
     */
    public Boolean getLitigationFlag()
    {
        return litigationFlag;
    }

    /**
     * @param litigationFlag
     *            the litigationFlag to set
     */
    public void setLitigationFlag(Boolean litigationFlag)
    {
        this.litigationFlag = litigationFlag;
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

    /**
     * @return the requestSubType
     */
    public String getRequestSubType()
    {
        return requestSubType;
    }

    /**
     * @param requestSubType
     *            the requestSubType to set
     */
    public void setRequestSubType(String requestSubType)
    {
        this.requestSubType = requestSubType;
    }

    /**
     * @return the requestCategory
     */
    public String getRequestCategory()
    {
        return requestCategory;
    }

    /**
     * @param requestCategory
     *            the requestCategory to set
     */
    public void setRequestCategory(String requestCategory)
    {
        this.requestCategory = requestCategory;
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
     * @return the dispositionSubtype
     */
    public String getDispositionSubtype()
    {
        return dispositionSubtype;
    }

    /**
     * @param dispositionSubtype
     *            the dispositionSubtype to set
     */
    public void setDispositionSubtype(String dispositionSubtype)
    {
        this.dispositionSubtype = dispositionSubtype;
    }

    public Boolean getPaidFlag()
    {
        return paidFlag;
    }

    public void setPaidFlag(Boolean paidFlag)
    {
        this.paidFlag = paidFlag;
    }

    /**
     * @return the publicFlag
     */
    public Boolean getPublicFlag()
    {
        return publicFlag;
    }

    /**
     * @param publicFlag
     *            the publicFlag to set
     */
    public void setPublicFlag(Boolean publicFlag)
    {
        this.publicFlag = publicFlag;
    }

    /**
     * @return the deliveryMethodOfResponse
     */
    public String getDeliveryMethodOfResponse()
    {
        return deliveryMethodOfResponse;
    }

    /**
     * @param deliveryMethodOfResponse
     *            the deliveryMethodOfResponse to set
     */
    public void setDeliveryMethodOfResponse(String deliveryMethodOfResponse)
    {
        this.deliveryMethodOfResponse = deliveryMethodOfResponse;
    }

    /**
     * @return the recordSearchDateFrom
     */
    public LocalDateTime getRecordSearchDateFrom()
    {
        return recordSearchDateFrom;
    }

    /**
     * @param recordSearchDateFrom
     *            the recordSearchDateFrom to set
     */
    public void setRecordSearchDateFrom(LocalDateTime recordSearchDateFrom)
    {
        this.recordSearchDateFrom = recordSearchDateFrom;
    }

    /**
     * @return the recordSearchDateTo
     */
    public LocalDateTime getRecordSearchDateTo()
    {
        return recordSearchDateTo;
    }

    /**
     * @param recordSearchDateTo
     *            the recordSearchDateTo to set
     */
    public void setRecordSearchDateTo(LocalDateTime recordSearchDateTo)
    {
        this.recordSearchDateTo = recordSearchDateTo;
    }

    /**
     * @return the processingFeeWaive
     */
    public double getProcessingFeeWaive()
    {
        return processingFeeWaive;
    }

    /**
     * @param processingFeeWaive
     *            the processingFeeWaive to set
     */
    public void setProcessingFeeWaive(double processingFeeWaive)
    {
        this.processingFeeWaive = processingFeeWaive;
    }

    /**
     * @return the requestFeeWaiveReason
     */
    public String getRequestFeeWaiveReason()
    {
        return requestFeeWaiveReason;
    }

    /**
     * @param requestFeeWaiveReason
     *            the requestFeeWaiveReason to set
     */
    public void setRequestFeeWaiveReason(String requestFeeWaiveReason)
    {
        this.requestFeeWaiveReason = requestFeeWaiveReason;
    }

    /**
     * @return the payFee
     */
    public String getPayFee()
    {
        return payFee;
    }

    /**
     * @param payFee
     *            the payFee to set
     */
    public void setPayFee(String payFee)
    {
        this.payFee = payFee;
    }

    /**
     * @return the requestExpediteReason
     */
    public String getRequestExpediteReason()
    {
        return requestExpediteReason;
    }

    /**
     * @param requestExpediteReason
     *            the requestExpediteReason to set
     */
    public void setRequestExpediteReason(String requestExpediteReason)
    {
        this.requestExpediteReason = requestExpediteReason;
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

    public String getRequestTrack()
    {
        return requestTrack;
    }

    public void setRequestTrack(String requestTrack)
    {
        this.requestTrack = requestTrack;
    }

    public String getOtherReason()
    {
        return otherReason;
    }

    public void setOtherReason(String otherReason)
    {
        this.otherReason = otherReason;
    }

    public String getNotificationGroup()
    {
        return notificationGroup;
    }

    public void setNotificationGroup(String notificationGroup)
    {
        this.notificationGroup = notificationGroup;
    }

    public FoiaConfiguration getFoiaConfiguration()
    {
        return foiaConfiguration;
    }

    public void setFoiaConfiguration(FoiaConfiguration foiaConfiguration)
    {
        this.foiaConfiguration = foiaConfiguration;
    }

    public Boolean getAmendmentFlag()
    {
        return amendmentFlag;
    }

    public void setAmendmentFlag(Boolean amendmentFlag)
    {
        this.amendmentFlag = amendmentFlag;
    }

    public String getRequestAmendmentDetails()
    {
        return requestAmendmentDetails;
    }

    public void setRequestAmendmentDetails(String requestAmendmentDetails)
    {
        this.requestAmendmentDetails = requestAmendmentDetails;
    }

    public String getExternalIdentifier()
    {
        return externalIdentifier;
    }

    public void setExternalIdentifier(String externalIdentifier)
    {
        this.externalIdentifier = externalIdentifier;
    }

    public List<DispositionReason> getDispositionReasons()
    {
        return dispositionReasons;
    }

    public void setDispositionReasons(List<DispositionReason> dispositionReasons)
    {
        this.dispositionReasons = dispositionReasons;
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

    public LocalDateTime getDispositionClosedDate()
    {
        return dispositionClosedDate;
    }

    public void setDispositionClosedDate(LocalDateTime dispositionClosedDate)
    {
        this.dispositionClosedDate = dispositionClosedDate;
    }

    public Boolean getExtensionFlag()
    {
        return extensionFlag;
    }

    public void setExtensionFlag(Boolean extensionFlag)
    {
        this.extensionFlag = extensionFlag;
    }

    /**
     * @return the tollingFlag
     */
    public Boolean getTollingFlag()
    {
        return tollingFlag;
    }

    /**
     * @param tollingFlag
     *            the tollingFlag to set
     */
    public void setTollingFlag(Boolean tollingFlag)
    {
        this.tollingFlag = tollingFlag;
    }

    public boolean getLimitedDeliveryFlag()
    {
        return limitedDeliveryFlag;
    }

    public void setLimitedDeliveryFlag(boolean limitedDeliveryFlag)
    {
        this.limitedDeliveryFlag = limitedDeliveryFlag;
    }

    public Boolean getGeneratedZipFlag()
    {
        return generatedZipFlag;
    }

    public void setGeneratedZipFlag(Boolean generatedZipFlag)
    {
        this.generatedZipFlag = generatedZipFlag;
    }

    public String getDispositionValue()
    {
        return dispositionValue;
    }

    public void setDispositionValue(String dispositionValue)
    {
        this.dispositionValue = dispositionValue;
    }

    public LocalDateTime getPerfectedDate()
    {
        return perfectedDate;
    }

    public void setPerfectedDate(LocalDateTime perfectedDate)
    {
        this.perfectedDate = perfectedDate;
    }

    /**
     * This property is used only for misdirected request calculations.
     * We need it in DB that so we can track the previous ttc state and calculate the current ttc state.
     * (originalTTCstate - elapsed days from the original perfected day to current perfected day)
     *
     * @return ttc of last redirection.
     */
    public Integer getTtcOnLastRedirection()
    {
        return ttcOnLastRedirection;
    }

    public void setTtcOnLastRedirection(Integer ttcOnLastRedirection)
    {
        this.ttcOnLastRedirection = ttcOnLastRedirection;
    }

    public boolean getWithdrawRequestedFlag()
    {
        return withdrawRequestedFlag;
    }

    public void setWithdrawRequestedFlag(boolean withdrawRequestedFlag)
    {
        this.withdrawRequestedFlag = withdrawRequestedFlag;
    }

    public LocalDateTime getRedirectedDate()
    {
        return redirectedDate;
    }

    public void setRedirectedDate(LocalDateTime redirectedDate)
    {
        this.redirectedDate = redirectedDate;
    }

    public LocalDateTime getExpediteDate()
    {
        return expediteDate;
    }

    public void setExpediteDate(LocalDateTime expediteDate)
    {
        this.expediteDate = expediteDate;
    }

    public LocalDateTime getFeeWaivedDate()
    {
        return feeWaivedDate;
    }

    public void setFeeWaivedDate(LocalDateTime feeWaivedDate)
    {
        this.feeWaivedDate = feeWaivedDate;
    }

    public String getDifficultyRating()
    {
        return difficultyRating;
    }

    public void setDifficultyRating(String difficultyRating)
    {
        this.difficultyRating = difficultyRating;
    }

    public Boolean getDeclaredAsRecord() {
        return declaredAsRecord;
    }

    public void setDeclaredAsRecord(Boolean declaredAsRecord) {
        this.declaredAsRecord = declaredAsRecord;
    }

    public String getPortalRequestTrackingId()
    {
        return portalRequestTrackingId;
    }

    public void setPortalRequestTrackingId(String portalRequestTrackingId)
    {
        this.portalRequestTrackingId = portalRequestTrackingId;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "FOIARequest {receivedDate=" + receivedDate + ", finalReplyDate=" + finalReplyDate + ", scannedDate=" + scannedDate
                + ", releasedDate=" + releasedDate + ", billingEnterDate=" + billingEnterDate + ", holdEnterDate=" + holdEnterDate
                + ", expediteFlag=" + expediteFlag + ", feeWaiverFlag=" + feeWaiverFlag + ", litigationFlag=" + litigationFlag
                + ", requestType=" + requestType + ", requestSubType=" + requestSubType + ", requestCategory=" + requestCategory
                + ", returnReason=" + returnReason + ", requestSource=" + requestSource + ", dispositionSubtype=" + dispositionSubtype
                + ", paidFlag=" + paidFlag + ", publicFlag=" + publicFlag + ", deliveryMethodOfResponse=" + deliveryMethodOfResponse
                + ", recordSearchDateFrom=" + recordSearchDateFrom + ", recordSearchDateTo=" + recordSearchDateTo + ", processingFeeWaive="
                + processingFeeWaive + ", requestFeeWaiveReason=" + requestFeeWaiveReason + ", payFee=" + payFee
                + ", requestExpediteReason=" + requestExpediteReason + ", extensionFlag=" + extensionFlag + ", amendmentFlag="
                + amendmentFlag
                + ", requestAmendmentDetails=" + requestAmendmentDetails + ", dispositionClosedDate=" + dispositionClosedDate
                + ", tollingFlag=" + tollingFlag + ", limitedDeliveryFlag=" + limitedDeliveryFlag + ", generatedZipFlag=" + generatedZipFlag
                + ", perfectedDate=" + perfectedDate + ", timeToComplete=" + ttcOnLastRedirection + ", redirectedDate=" + redirectedDate
                + ", expediteDate=" + expediteDate + ", feeWaivedDate=" + feeWaivedDate + ", difficultyRating=" + difficultyRating + "} "
                + super.toString();
    }
}
