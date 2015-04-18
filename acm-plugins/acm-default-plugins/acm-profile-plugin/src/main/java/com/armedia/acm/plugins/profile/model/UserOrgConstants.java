package com.armedia.acm.plugins.profile.model;

/**
 * Created by nebojsha on 09.04.2015.
 */
public interface UserOrgConstants {
    String OBJECT_TYPE = "USER_ORG";

    String EVENT_TYPE_OUTLOOK_PASSWORD_CHANGED = "com.armedia.acm.profile.userorg.outlook_password_changed";
    String EVENT_TYPE_USER_PROFILE_CREATED = "com.armedia.acm.profile.userorg.created";
    String EVENT_TYPE_USER_PROFILE_MODIFIED = "com.armedia.acm.profile.userorg.updated";

    String ERROR_PASSWORD_MISSING = "New password must be supplied";
    String ERROR_PASSWORD_EMPTY = "New password must not be the empty string";

    String ACTION_UPDATE_OUTLOOK_PASSWORD = "update Outlook password";
}
