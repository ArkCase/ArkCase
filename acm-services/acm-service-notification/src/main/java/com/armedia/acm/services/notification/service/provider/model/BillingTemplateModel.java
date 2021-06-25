package com.armedia.acm.services.notification.service.provider.model;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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
import java.time.LocalDateTime;
import java.util.Date;

public class BillingTemplateModel
{
    private final String amount;
    private final String token;
    private final String fileId;
    private final String objectId;
    private final String objectType;
    private final String objectNumber;
    private final String billName;
    private final String paymentMethod;
    private final String cardNumber;
    private final LocalDateTime paymentDate;
    private final String sessionId;
    private final String message;

    public BillingTemplateModel(String amount, String token, String fileId, String objectId, String objectType, String objectNumber, String billName,
            String paymentMethod, String cardNumber, LocalDateTime paymentDate, String sessionId, String message)
    {
        this.amount = amount;
        this.token = token;
        this.fileId = fileId;
        this.objectId = objectId;
        this.objectType = objectType;
        this.objectNumber = objectNumber;
        this.billName = billName;
        this.paymentMethod = paymentMethod;
        this.cardNumber = cardNumber;
        this.paymentDate = paymentDate;
        this.sessionId = sessionId;
        this.message = message;
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

    public String getObjectNumber()
    {
        return objectNumber;
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

    public LocalDateTime getPaymentDate()
    {
        return paymentDate;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public String getMessage()
    {
        return message;
    }
}
