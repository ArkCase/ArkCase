package com.armedia.acm.plugins.audit.service;

import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.pluginmanager.model.AcmPlugin;



public class ReplaceEventTypeNames {

    private AcmPlugin pluginEventType;

    public AuditEvent replaceNameInAcmEvent(AuditEvent event){
        String replacementName = (String) getPluginEventType().getPluginProperties().get(AuditConstants.EVENT_TYPE+event.getFullEventType());
        if(replacementName == null ){
            event.setFullEventType(event.getFullEventType());
        }
        else{
            event.setFullEventType(replacementName);
        }
        return event;
    }
    public AcmPlugin getPluginEventType() {
        return pluginEventType;
    }

    public void setPluginEventType(AcmPlugin pluginEventType) {
        this.pluginEventType = pluginEventType;
    }
}
