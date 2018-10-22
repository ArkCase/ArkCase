package gov.foia.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * This class represents an HTML form request from an external port site. The HTML form fields must have the same names
 * as the field names in this class.
 */
public class PortalFOIARequest implements Serializable
{

    private static final long serialVersionUID = 8626934331237410483L;

    private String originalRequestNumber;

    private String prefix;

    private String firstName;

    private String middleName;

    private String lastName;

    private String title;

    private String email;

    private String requestType;

    private String requestCategory;

    private String deliveryMethodOfResponse;

    private String address1;

    private String address2;

    private String city;

    private String state;

    private String country;

    private String zip;

    private String subject;

    private LocalDate recordSearchDateFrom;

    private LocalDate recordSearchDateTo;

    private double processingFeeWaive;

    private boolean requestFeeWaive;

    private String requestFeeWaiveReason;

    private String payFee;

    private boolean requestExpedite;

    private String requestExpediteReason;

    private List<PortalFOIARequestFile> files;

    private String ipAddress;

    private String userId;

    private String phone;

    private String organization;

    /**
     * @return the prefix
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * @param prefix
     *            the prefix to set
     */
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    /**
     * @return the firstName
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * @return the middleName
     */
    public String getMiddleName()
    {
        return middleName;
    }

    /**
     * @param middleName
     *            the middleName to set
     */
    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }

    /**
     * @return the lastName
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
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
     * @return the address1
     */
    public String getAddress1()
    {
        return address1;
    }

    /**
     * @param address1
     *            the address1 to set
     */
    public void setAddress1(String address1)
    {
        this.address1 = address1;
    }

    /**
     * @return the address2
     */
    public String getAddress2()
    {
        return address2;
    }

    /**
     * @param address2
     *            the address2 to set
     */
    public void setAddress2(String address2)
    {
        this.address2 = address2;
    }

    /**
     * @return the city
     */
    public String getCity()
    {
        return city;
    }

    /**
     * @param city
     *            the city to set
     */
    public void setCity(String city)
    {
        this.city = city;
    }

    /**
     * @return the state
     */
    public String getState()
    {
        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(String state)
    {
        this.state = state;
    }

    /**
     * @return the country
     */
    public String getCountry()
    {
        return country;
    }

    /**
     * @param country
     *            the country to set
     */
    public void setCountry(String country)
    {
        this.country = country;
    }

    /**
     * @return the zip
     */
    public String getZip()
    {
        return zip;
    }

    /**
     * @param zip
     *            the zip to set
     */
    public void setZip(String zip)
    {
        this.zip = zip;
    }

    /**
     * @return the subject
     */
    public String getSubject()
    {
        return subject;
    }

    /**
     * @param subject
     *            the subject to set
     */
    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    /**
     * @return the recordSearchDateFrom
     */
    public LocalDate getRecordSearchDateFrom()
    {
        return recordSearchDateFrom;
    }

    /**
     * @param recordSearchDateFrom
     *            the recordSearchDateFrom to set
     */
    public void setRecordSearchDateFrom(LocalDate recordSearchDateFrom)
    {
        this.recordSearchDateFrom = recordSearchDateFrom;
    }

    /**
     * @return the recordSearchDateTo
     */
    public LocalDate getRecordSearchDateTo()
    {
        return recordSearchDateTo;
    }

    /**
     * @param recordSearchDateTo
     *            the recordSearchDateTo to set
     */
    public void setRecordSearchDateTo(LocalDate recordSearchDateTo)
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
     * @return the requestFeeWaive
     */
    public boolean isRequestFeeWaive()
    {
        return requestFeeWaive;
    }

    /**
     * @param requestFeeWaive
     *            the requestFeeWaive to set
     */
    public void setRequestFeeWaive(boolean requestFeeWaive)
    {
        this.requestFeeWaive = requestFeeWaive;
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
     * @return the requestExpedite
     */
    public boolean isRequestExpedite()
    {
        return requestExpedite;
    }

    /**
     * @param requestExpedite
     *            the requestExpedite to set
     */
    public void setRequestExpedite(boolean requestExpedite)
    {
        this.requestExpedite = requestExpedite;
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
     * @return the files
     */
    public List<PortalFOIARequestFile> getFiles()
    {
        return files;
    }

    /**
     * @param files
     *            the files to set
     */
    public void setFiles(List<PortalFOIARequestFile> files)
    {
        this.files = files;
    }

    /**
     * @return the ipAddress
     */
    public String getIpAddress()
    {
        return ipAddress;
    }

    /**
     * @param ipAddress
     *            the ipAddress to set
     */
    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    /**
     * @return the userId
     */
    public String getUserId()
    {
        return userId;
    }

    /**
     * @param userId
     *            the userId to set
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "PortalFOIARequest [originalRequestNumber=" + originalRequestNumber + ", prefix=" + prefix + ", firstName=" + firstName
                + ", middleName=" + middleName + ", lastName=" + lastName + ", title=" + title + ", email=" + email + ", phone=" + phone + ", organization=" + organization + ", requestType="
                + requestType + ", requestCategory=" + requestCategory + ", deliveryMethodOfResponse=" + deliveryMethodOfResponse
                + ", address1=" + address1 + ", address2=" + address2 + ", city=" + city + ", state=" + state + ", country=" + country
                + ", zip=" + zip + ", subject=" + subject + ", recordSearchDateFrom=" + recordSearchDateFrom + ", recordSearchDateTo="
                + recordSearchDateTo + ", processingFeeWaive=" + processingFeeWaive + ", requestFeeWaive=" + requestFeeWaive
                + ", requestFeeWaiveReason=" + requestFeeWaiveReason + ", payFee=" + payFee + ", requestExpedite=" + requestExpedite
                + ", files=" + files + ", ipAddress=" + ipAddress + ", userId=" + userId + "]";
    }

}
