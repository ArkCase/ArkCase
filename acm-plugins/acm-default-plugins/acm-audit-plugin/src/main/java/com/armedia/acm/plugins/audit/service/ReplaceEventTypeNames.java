package com.armedia.acm.plugins.audit.service;

import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.pluginmanager.model.AcmPlugin;



public class ReplaceEventTypeNames {

    private AcmPlugin pluginEventType;

    public AuditEvent replaceNameInAcmEvent(AuditEvent event){
        event.setFullEventType((String) getPluginEventType().getPluginProperties().get(event.getFullEventType()));
        return event;
    }
    public AcmPlugin getPluginEventType() {
        return pluginEventType;
    }

    public void setPluginEventType(AcmPlugin pluginEventType) {
        this.pluginEventType = pluginEventType;
    }
}
