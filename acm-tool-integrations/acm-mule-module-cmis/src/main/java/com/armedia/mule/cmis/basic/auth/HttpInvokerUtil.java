package com.armedia.mule.cmis.basic.auth;

import org.slf4j.MDC;

/**
 * Helper class for HttpInvoker classes providing the external user authentication values.
 * <p>
 * Created by bojan.milenkoski on 23.11.2016
 */
public class HttpInvokerUtil
{
    public static final String EXTERNAL_AUTH_KEY = "X-Alfresco-Remote-User";

    private static final String ANONYMOUS_USER = "anonymous";

    private static final String EVENT_MDC_REQUEST_USER_ID_KEY = "MDC_USER_ID";

    /**
     * Returns the userId set in the thread local variable of {@link MDC} class. If the userId is 'anonymous' this method returns null.
     * 
     * @return the userId or null
     */
    public static final String getExternalUserIdValue()
    {
        String userId = MDC.get(EVENT_MDC_REQUEST_USER_ID_KEY);
        if ((userId == null) || ANONYMOUS_USER.equals(userId)) {
            return null;
        }
        return userId.indexOf("@") > 0 ? userId.substring(0, userId.indexOf("@")) : userId;
    }
}
