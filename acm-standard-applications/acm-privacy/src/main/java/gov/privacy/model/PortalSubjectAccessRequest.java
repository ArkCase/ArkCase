package gov.privacy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

/**
 * This class represents an HTML form request from an external port site. The HTML form fields must have the same names
 * as the field names in this class.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortalSubjectAccessRequest implements Serializable
{
    private static final long serialVersionUID = 8626934331237410483L;

    private String originalRequestNumber;

    private String requestType;

    private String requestCategory;

    private String deliveryMethodOfResponse;

    private String details;

    private String title;

    private Map<String, List<PortalSARFile>> files;

    private String ipAddress;

    private String userId;

    private PortalPersonDTO subject;

    private PortalPersonDTO requester;

    private boolean swornStatement = false;

    private boolean understandProcessingRequirementStatement = false;

    private boolean informationAgreementStatement = false;

    private boolean accurateAndAuthorizedStatement = false;

    private String signature;

    private LocalDate signatureDate;

    public String getOriginalRequestNumber()
    {
        return originalRequestNumber;
    }

    public void setOriginalRequestNumber(String originalRequestNumber)
    {
        this.originalRequestNumber = originalRequestNumber;
    }

    public String getRequestType()
    {
        return requestType;
    }

    public void setRequestType(String requestType)
    {
        this.requestType = requestType;
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

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Map<String, List<PortalSARFile>> getFiles()
    {
        return files;
    }

    public void setFiles(Map<String, List<PortalSARFile>> files)
    {
        this.files = files;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public PortalPersonDTO getSubject()
    {
        return subject;
    }

    public void setSubject(PortalPersonDTO subject)
    {
        this.subject = subject;
    }

    public PortalPersonDTO getRequester()
    {
        return requester;
    }

    public void setRequester(PortalPersonDTO requester)
    {
        this.requester = requester;
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

    @Override
    public String toString()
    {
        return "PortalSubjectAccessRequest{" +
                "originalRequestNumber='" + originalRequestNumber + '\'' +
                ", requestType='" + requestType + '\'' +
                ", requestCategory='" + requestCategory + '\'' +
                ", deliveryMethodOfResponse='" + deliveryMethodOfResponse + '\'' +
                ", details='" + details + '\'' +
                ", title='" + title + '\'' +
                ", files=" + files +
                ", ipAddress='" + ipAddress + '\'' +
                ", userId='" + userId + '\'' +
                ", subject=" + subject +
                ", requester=" + requester +
                ", swornStatement=" + swornStatement +
                ", understandProcessingRequirementStatement=" + understandProcessingRequirementStatement +
                ", informationAgreementStatement=" + informationAgreementStatement +
                ", accurateAndAuthorizedStatement=" + accurateAndAuthorizedStatement +
                ", signature='" + signature + '\'' +
                ", signatureDate=" + signatureDate +
                '}';
    }
}
