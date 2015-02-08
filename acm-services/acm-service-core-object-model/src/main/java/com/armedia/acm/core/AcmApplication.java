package com.armedia.acm.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class AcmApplication implements Serializable
{
    private static final long serialVersionUID = -4533090175042467646L;
    private String applicationName;
    private List<AcmUserAction> topbarActions;
    private List<AcmUserAction> navigatorTabs;
    private List<AcmObjectType> objectTypes;

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

    private boolean isNotEmptyString(String s) {
        if (null == s) {
            return false;
        }
        if ("".equals(s)) {
            return false;
        }
        return true;
    }

    public String getObjectTypesAsJson() {
        String json = "[";
        if (null != objectTypes) {
            for (AcmObjectType objectType : getObjectTypes()) {
                if (isNotEmptyString(objectType.getName())) {
                    if ("[".equals(json)) {
                        json += "{";
                    } else {
                        json += ",{";
                    }

                    json += "\"name\":\"" + objectType.getName() + "\"";

                    if (isNotEmptyString(objectType.getDescription())) {
                        json += ",\"description\":\"" + objectType.getDescription() + "\"";
                    }
                    if (isNotEmptyString(objectType.getIconName())) {
                        json += ",\"iconName\":\"" + objectType.getIconName() + "\"";
                    }
                    if (isNotEmptyString(objectType.getUrl())) {
                        json += ",\"url\":\"" + objectType.getUrl() + "\"";
                    }
                    if (isNotEmptyString(objectType.getUrlEnd())) {
                        json += ",\"urlEnd\":\"" + objectType.getUrlEnd() + "\"";
                    }

                    json += "}";
                }
            }
        }
        json += "]";
        return json;

//        JSONArray arr = new JSONArray();
//        Collection<AcmPlugin> plugins = getAcmPluginManager().getAcmPlugins();
//        for (AcmPlugin plugin : plugins) {
//            Map<String, Object> props = plugin.getPluginProperties();
//            if (null != props) {
//                Object prop = props.get("search.ex");
//                if (null != prop) {
//                    try {
//                        JSONObject searchEx = new JSONObject(prop.toString());
//                        arr.put(searchEx);
//                    } catch (JSONException e) {
//                        log.error(e.getMessage());
//                    }
//                }
//            }
//        }
//
//        return objectTypes;
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
}
