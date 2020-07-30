package com.armedia.acm.email.model;

/*-
 * #%L
 * Acm Mail Tools
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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;

import java.util.Properties;

@Configuration
@EnableIntegration
public class AcmEmailReceiverAdapter
{
    private EmailReceiverConfig emailReceiverConfig;
    private Properties javaMailPropertiesCaseFile;
    private Properties javaMailPropertiesComplaint;

    @Bean
    @InboundChannelAdapter(channel = "mailChannelCaseFile", poller = @Poller(fixedRate = "${email.fixed-rate}", maxMessagesPerPoll = "${email.max-messages-per-poll}", errorChannel = "mailErrorChannelCaseFile"))
    public MessageSource caseMailMessageSource(ImapMailReceiver caseImapMailReceiver)
    {
        caseImapMailReceiver.setShouldDeleteMessages(emailReceiverConfig.getShouldDeleteMessages());
        caseImapMailReceiver.setShouldMarkMessagesAsRead(emailReceiverConfig.getShouldMarkMessagesAsRead());
        caseImapMailReceiver.setJavaMailProperties(getJavaMailPropertiesCaseFile());
        caseImapMailReceiver.setMaxFetchSize(1);
        return new MailReceivingMessageSource(caseImapMailReceiver);
    }

    @Bean
    @InboundChannelAdapter(channel = "mailChannelComplaint", poller = @Poller(fixedRate = "${email.fixed-rate}", maxMessagesPerPoll = "${email.max-messages-per-poll}", errorChannel = "mailErrorChannelComplaint"))
    public MessageSource complaintMailMessageSource(ImapMailReceiver complaintImapMailReceiver)
    {
        complaintImapMailReceiver.setShouldDeleteMessages(emailReceiverConfig.getShouldDeleteMessages());
        complaintImapMailReceiver.setShouldMarkMessagesAsRead(emailReceiverConfig.getShouldMarkMessagesAsRead());
        complaintImapMailReceiver.setJavaMailProperties(getJavaMailPropertiesComplaint());
        complaintImapMailReceiver.setMaxFetchSize(1);
        return new MailReceivingMessageSource(complaintImapMailReceiver);
    }

    public EmailReceiverConfig getEmailReceiverConfig()
    {
        return emailReceiverConfig;
    }

    public void setEmailReceiverConfig(EmailReceiverConfig emailReceiverConfig)
    {
        this.emailReceiverConfig = emailReceiverConfig;
    }

    public Properties getJavaMailPropertiesCaseFile() {
        return javaMailPropertiesCaseFile;
    }

    public void setJavaMailPropertiesCaseFile(Properties javaMailPropertiesCaseFile) {
        this.javaMailPropertiesCaseFile = javaMailPropertiesCaseFile;
    }

    public Properties getJavaMailPropertiesComplaint() {
        return javaMailPropertiesComplaint;
    }

    public void setJavaMailPropertiesComplaint(Properties javaMailPropertiesComplaint) {
        this.javaMailPropertiesComplaint = javaMailPropertiesComplaint;
    }
}
