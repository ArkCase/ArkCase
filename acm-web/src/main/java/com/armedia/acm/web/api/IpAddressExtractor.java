package com.armedia.acm.web.api;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Created by dmiller on 8/16/2016.
 */
public class IpAddressExtractor
{
    public String extractIpAddress(HttpServletRequest httpServletRequest)
    {
        String ipAddress = getRealClientIpAddress(httpServletRequest);

        ipAddress = ipAddress == null ? httpServletRequest.getRemoteAddr() : ipAddress;

        return ipAddress;
    }

    /**
     * The general format of the x-forwarded-for header is:
     * X-Forwarded-For: client, proxy1, proxy2
     * where the value is a comma+space separated list of IP addresses, the left-most being the original client,
     * and each successive proxy that passed the request adding the IP address where it received the request
     * from. In this example, the request passed through proxy1, proxy2, and then proxy3 (not shown in the header).
     * proxy3 appears as remote address of the request.
     */
    private String getRealClientIpAddress(HttpServletRequest httpServletRequest)
    {
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            String header = headerNames.nextElement();
            if ("x-forwarded-for".equalsIgnoreCase(header))
            {
                String value = httpServletRequest.getHeader(header);
                int comma = value.indexOf(",");
                if (comma > 0)
                {
                    value = value.substring(0, comma);
                }
                return value;
            }
        }

        return null;
    }
}
