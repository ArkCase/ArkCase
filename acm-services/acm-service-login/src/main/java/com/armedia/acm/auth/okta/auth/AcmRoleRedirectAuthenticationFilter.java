package com.armedia.acm.auth.okta.auth;

import com.armedia.acm.auth.okta.model.OktaAPIConstants;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class AcmRoleRedirectAuthenticationFilter extends GenericFilterBean
{
    private AcmMultiFactorConfig multiFactorConfig;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication == null)
        {
            chain.doFilter(request, response);
        }
        else
        {
            Set<String> collect = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
            if (collect.contains(OktaAPIConstants.ROLE_PRE_AUTHENTICATED)
                    && !collect.contains("ROLE_AUTHENTICATED")
                    && !request.getServletPath().toLowerCase().contains("mfa")
                    && !request.getServletPath().toLowerCase().contains("logout"))
            {
                response.sendRedirect(request.getContextPath() + getMultiFactorConfig().getSelectMethodTargetUrl());
                return;
            }

            chain.doFilter(request, response);
        }
    }

    public AcmMultiFactorConfig getMultiFactorConfig()
    {
        return multiFactorConfig;
    }

    public void setMultiFactorConfig(AcmMultiFactorConfig multiFactorConfig)
    {
        this.multiFactorConfig = multiFactorConfig;
    }
}
