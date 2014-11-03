package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.event.AcmEvent;

/**
 * Created by marjan.stefanoski on 31.10.2014.
 */
public class UserOrgCreatedEvent extends UserOrgPersistentEvent {

        private static final String EVENT_TYPE = "com.armedia.acm.profile.userorg.created";

        public UserOrgCreatedEvent(UserOrg source) {
            super(source);
        }

        @Override
        public String getEventType() {
            return EVENT_TYPE;
        }
}
