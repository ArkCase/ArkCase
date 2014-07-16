package com.armedia.acm.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class AcmAuthenticationDetailsFactory
        implements AuthenticationDetailsSource<HttpServletRequest, AcmAuthenticationDetails>
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public AcmAuthenticationDetails buildDetails(HttpServletRequest context)
    {


        AcmAuthenticationDetails retval = new AcmAuthenticationDetails(context);
        log.debug("initial ip address: " + retval.getRemoteAddress());

        findXForwardedHeader(retval, context);

        return retval;
    }

    private void findXForwardedHeader(AcmAuthenticationDetails details, HttpServletRequest context)
    {

        /*
            The general format of the x-forwarded-for header is:
            X-Forwarded-For: client, proxy1, proxy2
            where the value is a comma+space separated list of IP addresses, the left-most being the original client,
            and each successive proxy that passed the request adding the IP address where it received the request
            from. In this example, the request passed through proxy1, proxy2, and then proxy3 (not shown in the header).
            proxy3 appears as remote address of the request.
         */
        Enumeration<String> headerNames = context.getHeaderNames();
        while ( headerNames.hasMoreElements() )
        {
            String header = headerNames.nextElement();
            if ( "x-forwarded-for".equalsIgnoreCase(header) )
            {
                String value = context.getHeader(header);
                int comma = value.indexOf(",");
                if ( comma > 0 )
                {
                    value = value.substring(0, comma);
                }
                details.setUserIpAddress(value);
                log.debug("Remote IP: " + details.getUserIpAddress());

                break;
            }
        }
    }
}
