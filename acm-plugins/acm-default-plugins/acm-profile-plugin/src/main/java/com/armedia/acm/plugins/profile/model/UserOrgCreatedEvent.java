package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.event.AcmEvent;

/**
 * Created by marjan.stefanoski on 31.10.2014.
 */
public class UserOrgCreatedEvent extends UserOrgPersistentEvent {

        public UserOrgCreatedEvent(UserOrg source) {
            super(source);
        }

        @Override
        public String getEventType() {
            return UserOrgConstants.EVENT_TYPE_USER_PROFILE_CREATED;
        }
}
