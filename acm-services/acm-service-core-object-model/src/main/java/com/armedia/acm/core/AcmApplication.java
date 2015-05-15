package com.armedia.acm.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class AcmApplication implements Serializable
{
    private static final long serialVersionUID = -4533090175042467646L;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String applicationName;
    private List<AcmUserAction> topbarActions;
    private List<AcmUserAction> navigatorTabs;
    private List<AcmObjectType> objectTypes;
    private Map<Object, Object> settings;

    private Boolean issueCollectorFlag;

    private List<AcmObjectType> businessObjects;

    public String getApplicationName()
    {
        return applicationName;
    }

    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }

    public List<AcmUserAction> getTopbarActions()
    {
        return Collections.unmodifiableList(topbarActions);
    }

    public void setTopbarActions(List<AcmUserAction> topbarActions)
    {
        this.topbarActions = topbarActions;
    }

    public List<AcmUserAction> getNavigatorTabs()
    {
        return Collections.unmodifiableList(navigatorTabs);
    }

    public void setNavigatorTabs(List<AcmUserAction> navigatorTabs)
    {
        this.navigatorTabs = navigatorTabs;
    }

    public List<AcmObjectType> getObjectTypes() {
        return objectTypes;
    }

    public void setObjectTypes(List<AcmObjectType> objectTypes) {
        this.objectTypes = objectTypes;
    }

    public Boolean getIssueCollectorFlag() {
        return issueCollectorFlag;
    }

    public void setIssueCollectorFlag(Boolean issueCollectorFlag) {
        this.issueCollectorFlag = issueCollectorFlag;
    }

//    public String toJson() {
//        String json = "[]";
//        ObjectMapper om = new ObjectMapper();
//        try {
//            json =  om.writeValueAsString(this);
//        } catch (JsonProcessingException e) {
//            log.error(e.getMessage());
//        }
//        return json;
//    }

    public String getObjectTypesAsJson() {
        String json = "[]";
        ObjectMapper om = new ObjectMapper();
        try {
            json =  om.writeValueAsString(getObjectTypes());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return json;
    }

    public List<AcmObjectType> getBusinessObjects()
    {
        return businessObjects;
    }

    public void setBusinessObjects(List<AcmObjectType> businessObjects)
    {
        this.businessObjects = businessObjects;
    }

    public AcmObjectType getBusinessObjectByName(String name)
    {
        for ( AcmObjectType objectType : getBusinessObjects() )
        {
            if ( objectType.getName().equalsIgnoreCase(name) )
            {
                return objectType;
            }
        }

        throw new IllegalArgumentException("No such business object with name '" + name + "'");
    }

    public Map<Object, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<Object, Object> settings) {
        this.settings = settings;
    }
}
