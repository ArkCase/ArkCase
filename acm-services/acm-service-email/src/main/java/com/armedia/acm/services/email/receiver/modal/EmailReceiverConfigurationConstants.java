package com.armedia.acm.services.email.receiver.modal;

public interface EmailReceiverConfigurationConstants
{

    String SHOULD_DELETE_MESSAGE = "email.should-delete-messages";
    String SHOULD_MARK_MESSAGES_AS_READ = "email.should-mark-messages-as-read";
    String MAX_MESSAGES_PER_POLL = "email.max-messages-per-poll";
    String FIXED_RATE = "email.fixed-rate";

    String EMAIL = "email.CASE_FILE.user";
    String PASSWORD = "email.CASE_FILE.password";
    String PROTOCOL = "email.protocol";
    String FETCH_FOLDER = "email.fetch.folder";
    String HOST = "email.host";
    String PORT = "email.port";
    String DEBUG = "email.debug";

}
