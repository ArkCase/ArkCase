package com.armedia.acm.services.notification.service;

public class SmtpStartTLSNotificationSender extends SmtpNotificationSender
{
    @Override
    protected String getFlow()
    {
        return "vm://sendEmailViaSmtpStartTLS.in";
    }
}