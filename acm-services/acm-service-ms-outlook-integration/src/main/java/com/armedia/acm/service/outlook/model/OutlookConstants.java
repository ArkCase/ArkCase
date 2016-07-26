package com.armedia.acm.service.outlook.model;

public interface OutlookConstants
{
    String OBJECT_TYPE = "OutlookPassword";

    String EVENT_TYPE_OUTLOOK_PASSWORD_CHANGED = "com.armedia.acm.service.outlook.model.outlook_password_changed";

    String ERROR_PASSWORD_MISSING = "New password must be supplied";
    String ERROR_PASSWORD_EMPTY = "New password must not be the empty string";

    String ACTION_UPDATE_OUTLOOK_PASSWORD = "update Outlook password";
}
