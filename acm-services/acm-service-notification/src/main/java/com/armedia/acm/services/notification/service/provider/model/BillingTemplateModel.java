package com.armedia.acm.services.notification.service.provider.model;

import java.util.Date;

public class BillingTemplateModel
{
    private final String amount;
    private final String token;
    private final String fileId;
    private final String objectId;
    private final String objectType;
    private final String billName;
    private final String paymentMethod;
    private final String cardNumber;
    private final Date paymentDate;

    public BillingTemplateModel(String amount, String token, String fileId, String objectId, String objectType, String billName, String paymentMethod, String cardNumber, Date paymentDate)
    {
        this.amount = amount;
        this.token = token;
        this.fileId = fileId;
        this.objectId = objectId;
        this.objectType = objectType;
        this.billName = billName;
        this.paymentMethod = paymentMethod;
        this.cardNumber = cardNumber;
        this.paymentDate = paymentDate;
    }

    public String getAmount()
    {
        return amount;
    }

    public String getToken()
    {
        return token;
    }

    public String getFileId()
    {
        return fileId;
    }

    public String getObjectId()
    {
        return objectId;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public String getBillName()
    {
        return billName;
    }

    public String getPaymentMethod()
    {
        return paymentMethod;
    }

    public String getCardNumber()
    {
        return cardNumber;
    }

    public Date getPaymentDate()
    {
        return paymentDate;
    }
}
