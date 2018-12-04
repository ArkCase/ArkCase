package com.armedia.acm.services.email.receiver.modal;

/*-
 * #%L
 * ACM Service: Email
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

public interface EmailReceiverConfigurationConstants
{

    String SHOULD_DELETE_MESSAGE = "email.should-delete-messages";
    String SHOULD_MARK_MESSAGES_AS_READ = "email.should-mark-messages-as-read";
    String MAX_MESSAGES_PER_POLL = "email.max-messages-per-poll";
    String FIXED_RATE = "email.fixed-rate";

    String EMAIL_CASE = "email.CASE_FILE.user";
    String PASSWORD_CASE = "email.CASE_FILE.password";
    String PROTOCOL = "email.protocol";
    String FETCH_FOLDER = "email.fetch.folder";
    String HOST = "email.host";
    String PORT = "email.port";
    String DEBUG = "email.debug";

    String EMAIL_COMPLAINT = "email.COMPLAINT.user";
    String PASSWORD_COMPLAINT = "email.COMPLAINT.password";

}
